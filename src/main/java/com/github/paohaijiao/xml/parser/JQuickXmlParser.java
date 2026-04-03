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
            throw new RuntimeException("Failed to parse curl XML: " + xmlPath, e);
        }
        return namespaceMap;
    }

    private JQuickXmlNamespace parseElement(Element element) {
        String namespaceName = element.getAttribute(jQuickXmlElement.getNameSpaceName());
        JQuickXmlNamespace namespace = JQuickXmlBuilder.create().namespace(namespaceName).build();
        NodeList nodeList = element.getElementsByTagName(jQuickXmlElement.getChildElementTagName());
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element curlElement = (Element) nodeList.item(i);
            JQuickXmlMethod method = parseMethodElement(curlElement);
            namespace.addMethod(method.getName(), method);
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
        method.setContent(element.getTextContent().trim());
        method.setMap(attr);
        return method;
    }
}
