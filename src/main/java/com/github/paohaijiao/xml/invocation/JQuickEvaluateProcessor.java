/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.xml.invocation;

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.enums.JLogLevel;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.value.ValueResolver;
import com.github.paohaijiao.xml.enums.JQuickXmlEscapeEnums;
import com.github.paohaijiao.xml.ongl.OgnlUtils;
import com.github.paohaijiao.xml.wrapper.WrapperManager;
import com.github.paohaijiao.xml.wrapper.data.JQuickXmlWrapperData;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Slf4j
public class JQuickEvaluateProcessor {

    private static JConsole console=new JConsole();

    private static final DocumentBuilderFactory factory;

    static {
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (Exception e) {
            log.warn("Failed to set XML parser features", e);
        }
    }

    /**
     * 解析动态标签内容
     * @param content 包含动态标签的内容
     * @param context 上下文
     * @return 解析后的内容
     */
    public static String parse(String content, JContext context) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        try {
            console.log(JLogLevel.INFO,"the  content is \n" + content);
            String preprocessed = processAndEscapeAttributes(content,context);
            String wrappedContent = wrapAsXml(preprocessed);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new org.xml.sax.helpers.DefaultHandler() {
                @Override
                public void fatalError(org.xml.sax.SAXParseException e) {}
                @Override
                public void error(org.xml.sax.SAXParseException e) {}
            });
            Document doc = builder.parse(new org.xml.sax.InputSource(new StringReader(wrappedContent)));
            Element root = doc.getDocumentElement();
            String result = parseNode(root, context);
            console.log(JLogLevel.INFO,"the result is \n" + result);
            return result;
        } catch (Exception e) {//兼容里面含有<>标签的数据比如说java 泛型
            return content;
        }
    }
    private static String processAndEscapeAttributes(String content, JContext context) {
        if (content == null) return null;
      //  String rendered = ValueResolver.renderTemplate(content, context);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("test\\s*=\\s*\"([^\"]*)\"");
        java.util.regex.Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String expr = matcher.group(1);
            String escapedExpr = escapeXml(expr);
            matcher.appendReplacement(sb, "test=\"" + escapedExpr + "\"");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 检查是否包含动态标签
     */
    private static boolean containsDynamicTags(String content) {
        return content.contains("<if") || content.contains("<foreach") ||
                content.contains("<choose") || content.contains("<where") ||
                content.contains("<set") || content.contains("<trim");
    }

    /**
     * 将内容包装为 XML 文档
     */
    private static String wrapAsXml(String content) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>" + content + "</root>";
    }

    /**
     * 递归解析 XML 节点
     */
    private static String parseNode(Node node, JContext context) {
        StringBuilder result = new StringBuilder();
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            switch (child.getNodeType()) {
                case Node.TEXT_NODE:
                    String text = child.getTextContent();
                    if (text != null && !text.trim().isEmpty()) {
                        result.append(replaceVariables(text, context));
                    }
                    break;
                case Node.CDATA_SECTION_NODE:
                    String cdata = child.getTextContent();
                    if (cdata != null) {
                        result.append(cdata);
                    }
                    break;
                case Node.ELEMENT_NODE:
                    String parsed = parseDynamicTag((Element) child, context);
                    if (parsed != null) {
                        result.append(parsed);
                    }
                    break;
                default:
                    break;
            }
        }
        return result.toString();
    }

    /**
     * 解析动态标签
     */
    private static String parseDynamicTag(Element element, JContext context) {
        String tagName = element.getTagName().toLowerCase();
        switch (tagName) {
            case "if":
                return parseIfTag(element, context);
            case "foreach":
                return parseForeachTag(element, context);
            case "choose":
                return parseChooseTag(element, context);
            case "where":
                return parseWhereTag(element, context);
            case "set":
                return parseSetTag(element, context);
            case "trim":
                return parseTrimTag(element, context);
            default:
                return parseNode(element, context);
        }
    }

    /**
     * 解析 if 标签
     */
    private static String parseIfTag(Element element, JContext context) {
        String test = element.getAttribute("test");
        if (test == null || test.trim().isEmpty()) {
            return "";
        }
        try {
            boolean condition = OgnlUtils.getBoolean(test, context);
            if (condition) {
                return parseNode(element, context);
            }
        } catch (Exception e) {
            log.error("Failed to evaluate if condition: {}", test, e);
        }

        return "";
    }

    /**
     * 解析 foreach 标签
     */
    private static String parseForeachTag(Element element, JContext context) {
        String collection = element.getAttribute("collection");
        String item = element.getAttribute("item");
        String index = element.getAttribute("index");
        String separator = element.getAttribute("separator");
        String open = element.getAttribute("open");
        String close = element.getAttribute("close");
        if (collection == null || collection.isEmpty() || item == null || item.isEmpty()) {
            log.warn("foreach tag missing required attributes: collection or item");
            return "";
        }
        Object collectionObj = OgnlUtils.getValue(collection, context);
        if (collectionObj == null) {
            return "";
        }
        Iterable<?> iterable = toIterable(collectionObj);
        if (iterable == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        if (open != null && !open.isEmpty()) {
            result.append(open);
        }
        int idx = 0;
        for (Object obj : iterable) {
            if (idx > 0 && separator != null && !separator.isEmpty()) {
                result.append(separator);
            }
            JContext itemContext = new JContext();
            itemContext.putAll(context);
            itemContext.put(item,obj);
            String itemContent = parseNode(element, itemContext);
            result.append(itemContent);
            idx++;
        }

        if (close != null && !close.isEmpty()) {
            result.append(close);
        }
        return result.toString();
    }

    /**
     * 解析 choose 标签（类似 switch-case）
     */
    private static String parseChooseTag(Element element, JContext context) {
        NodeList children = element.getChildNodes();
        // 首先查找 when 标签
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                if ("when".equalsIgnoreCase(childElement.getTagName())) {
                    String test = childElement.getAttribute("test");
                    if (test != null && !test.isEmpty()) {
                        try {
                            if (OgnlUtils.getBoolean(test, context)) {
                                return parseNode(childElement, context);
                            }
                        } catch (Exception e) {
                            log.error("Failed to evaluate when condition: {}", test, e);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                if ("otherwise".equalsIgnoreCase(childElement.getTagName())) {
                    return parseNode(childElement, context);
                }
            }
        }
        return "";
    }

    /**
     * 解析 where 标签（自动处理 AND/OR）
     */
    private static String parseWhereTag(Element element, JContext context) {
        String content = parseNode(element, context);
        content = content.trim();
        if (content.isEmpty()) {
            return "";
        }
        content = content.replaceAll("^(?i)\\s*(AND|OR)\\s+", "");
        return "WHERE " + content;
    }

    /**
     * 解析 set 标签（自动处理逗号）
     */
    private static String parseSetTag(Element element, JContext context) {
        String content = parseNode(element, context);
        content = content.trim();
        if (content.isEmpty()) {
            return "";
        }
        content = content.replaceAll(",\\s*$", "");
        return "SET " + content;
    }

    /**
     * 解析 trim 标签（自定义前后缀处理）
     */
    private static String parseTrimTag(Element element, JContext context) {
        String prefix = element.getAttribute("prefix");
        String suffix = element.getAttribute("suffix");
        String prefixOverrides = element.getAttribute("prefixOverrides");
        String suffixOverrides = element.getAttribute("suffixOverrides");
        String content = parseNode(element, context);
        content = content.trim();
        if (content.isEmpty()) {
            return "";
        }
        if (prefixOverrides != null && !prefixOverrides.isEmpty()) {
            String[] overrides = prefixOverrides.split("\\|");
            for (String override : overrides) {
                if (content.toUpperCase().startsWith(override.toUpperCase())) {
                    content = content.substring(override.length()).trim();
                    break;
                }
            }
        }
        if (suffixOverrides != null && !suffixOverrides.isEmpty()) {
            String[] overrides = suffixOverrides.split("\\|");
            for (String override : overrides) {
                if (content.toUpperCase().endsWith(override.toUpperCase())) {
                    content = content.substring(0, content.length() - override.length()).trim();
                    break;
                }
            }
        }
        if (prefix != null && !prefix.isEmpty()) {
            content = prefix + content;
        }
        if (suffix != null && !suffix.isEmpty()) {
            content = content + suffix;
        }

        return content;
    }

    private static Iterable<?> toIterable(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Iterable) {
            return (Iterable<?>) obj;
        }

        if (obj instanceof int[]) {
            return IntStream.of((int[]) obj).boxed().collect(Collectors.toList());
        }
        if (obj instanceof long[]) {
            return LongStream.of((long[]) obj).boxed().collect(Collectors.toList());
        }
        if (obj instanceof double[]) {
            return DoubleStream.of((double[]) obj).boxed().collect(Collectors.toList());
        }
        if (obj instanceof Object[]) {
            return new ArrayList<>(Arrays.asList((Object[]) obj));
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).entrySet();
        }
        if (obj instanceof Iterator) {
            List<Object> list = new ArrayList<>();
            ((Iterator<?>) obj).forEachRemaining(list::add);
            return list;
        }

        return null;
    }

   public static String escapeXml(String content) {
       if (content == null) {
           return null;
       }
       String result = content;
       for (JQuickXmlEscapeEnums escapeEnum : JQuickXmlEscapeEnums.values()) {
           result = escapeEnum.escape(result);
       }
       return result;
   }
    private static String replaceVariables(String text, JContext context) {
        if (text == null || context == null) return text;
        Pattern pattern = Pattern.compile("#\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String expression = matcher.group(1);
            Object value = OgnlUtils.getValue(expression, context);
            if (value != null) {
                JQuickXmlWrapperData data=new JQuickXmlWrapperData(value);
                Object wrappedValue = WrapperManager.wrap(data);
                matcher.appendReplacement(sb, Matcher.quoteReplacement(wrappedValue.toString()));// Matcher.quoteReplacement(value.toString())
            } else {
                matcher.appendReplacement(sb, matcher.group(0));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
