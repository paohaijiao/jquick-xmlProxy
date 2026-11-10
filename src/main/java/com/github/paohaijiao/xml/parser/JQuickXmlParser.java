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
package com.github.paohaijiao.xml.parser;

import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.xml.builder.JQuickXmlBuilder;
import com.github.paohaijiao.xml.element.JQuickXmlElement;
import com.github.paohaijiao.xml.method.JQuickXmlMethod;
import com.github.paohaijiao.xml.namespace.JQuickXmlNamespace;
import com.github.paohaijiao.xml.resolver.ClasspathEntityResolver;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.xml.parser
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/11/27
 */
public class JQuickXmlParser implements JQuickParser{

    private JQuickXmlElement jQuickXmlElement;

    public JQuickXmlParser(JQuickXmlElement jQuickXmlElement){
        JAssert.notNull(jQuickXmlElement,"jQuickXmlElement must not be null");
        this.jQuickXmlElement=jQuickXmlElement;
    }

    public Map<String, JQuickXmlNamespace> parse(String xmlPath) {
        Map<String, JQuickXmlNamespace> namespaceMap = new HashMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new ClasspathEntityResolver());
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlPath);
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found in classpath: " + xmlPath);
            }
            Document document = builder.parse(inputStream);
            NodeList nodeList = document.getElementsByTagName(jQuickXmlElement.getRootElementTagName());
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                JQuickXmlNamespace curlNamespace = parseElement(element);
                namespaceMap.put(curlNamespace.getNamespace(), curlNamespace);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse  XML: " + xmlPath, e);
        }
        return namespaceMap;
    }

    private JQuickXmlNamespace parseElement(Element element) {
        String namespaceName = element.getAttribute(jQuickXmlElement.getNameSpaceName());
        JQuickXmlNamespace namespace = JQuickXmlBuilder.create().namespace(namespaceName).build();
        List<String> tagList= jQuickXmlElement.getChildElementTagName();
        for(String tag:tagList){
            NodeList nodeList = element.getElementsByTagName(tag);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element curlElement = (Element) nodeList.item(i);
                JQuickXmlMethod method = parseMethodElement(curlElement);
                method.setTag(tag);
                namespace.addMethod(method.getName(), method);
            }
        }
        return namespace;
    }

    private JQuickXmlMethod parseMethodElement(Element element) {
        HashMap<String,String> attr = new HashMap<>();
        JQuickXmlMethod method = new JQuickXmlMethod();
        NamedNodeMap namedNodeMap=element.getAttributes();
        for (int i = 0; i < namedNodeMap.getLength(); i++) {
            Node attrNode = namedNodeMap.item(i);
            String attrName = attrNode.getNodeName();
            if(attrName.equalsIgnoreCase(jQuickXmlElement.getMethodName())){
                method.setName(element.getAttribute(jQuickXmlElement.getMethodName()));
            }else if(attrName.equalsIgnoreCase(jQuickXmlElement.getMethodReturnClass())){
                method.setReturnClass(element.getAttribute(jQuickXmlElement.getMethodReturnClass()));
            }else if(attrName.equalsIgnoreCase(jQuickXmlElement.getMethodParamClass())){
                method.setParamClass(element.getAttribute(jQuickXmlElement.getMethodParamClass()));
            }else if(attrName.equalsIgnoreCase(jQuickXmlElement.getMethodParamClass())){
                method.setParamClass(element.getAttribute(jQuickXmlElement.getMethodParamClass()));
            }else if(attrName.equalsIgnoreCase(jQuickXmlElement.getValue())){
                method.setValue(element.getAttribute(jQuickXmlElement.getValue()));
            }else{
                String attrValue = attrNode.getNodeValue();
                attr.put(attrName, attrValue);
            }
            method.setContent(element.getTextContent().trim());
            method.setMap(attr);
        }
        String fullContent = getElementContent(element);
        method.setContent(fullContent);
        method.setMap(attr);
        return method;
    }
    private String getElementContent(Element element) {
        StringBuilder content = new StringBuilder();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                content.append(escapeText(child.getTextContent()));
            } else if (child.getNodeType() == Node.CDATA_SECTION_NODE) {
                content.append(escapeText(child.getTextContent()));
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                content.append(serializeElement((Element) child));
            }
        }
        return content.toString().trim();
    }
    private String serializeElement(Element element) {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(element.getTagName());
        NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node attr = attrs.item(i);
            sb.append(" ").append(attr.getNodeName()).append("=\"")
                    .append(escapeAttr(attr.getNodeValue())).append("\"");
        }
        sb.append(">");
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                sb.append(escapeText(child.getTextContent()));
            } else if (child.getNodeType() == Node.CDATA_SECTION_NODE) {
                sb.append(escapeText(child.getTextContent()));
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                sb.append(serializeElement((Element) child));
            }
        }

        sb.append("</").append(element.getTagName()).append(">");
        return sb.toString();
    }

    /**
     * 转义文本节点内容，保证序列化后的字符串仍是合法 XML
     * DOM 解析会把 &lt; 等实体解码成 <，序列化时必须重新转义，
     * 否则二次解析会抛 SAXParseException
     * Escape text node content. Example: escapeText("a<b & c") returns "a&lt;b &amp; c"
     *
     * @param text DOM 文本节点的原始值（已解码）
     * @return 转义后的合法 XML 字符数据
     */
    private static String escapeText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    /**
     * 转义属性值，在文本转义基础上额外处理双引号
     * Escape attribute value. Example: escapeAttr("a\"b<c") returns "a&quot;b&lt;c"
     *
     * @param value DOM 属性的原始值（已解码）
     * @return 可安全放入双引号属性中的转义值
     */
    private static String escapeAttr(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return escapeText(value).replace("\"", "&quot;");
    }
}
