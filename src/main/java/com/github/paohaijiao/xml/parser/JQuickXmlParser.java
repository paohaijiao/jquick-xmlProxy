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

import com.github.paohaijiao.xml.method.JQuickXmlMethod;
import com.github.paohaijiao.xml.namespace.JQuickXmlNamespace;
import com.github.paohaijiao.xml.resolver.ClasspathEntityResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
public class JQuickXmlParser {

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
            NodeList curlsNodes = document.getElementsByTagName("curls");
            for (int i = 0; i < curlsNodes.getLength(); i++) {
                Element curlsElement = (Element) curlsNodes.item(i);
                JQuickXmlNamespace curlNamespace = parseCurlsElement(curlsElement);
                namespaceMap.put(curlNamespace.getNamespace(), curlNamespace);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse curl XML: " + xmlPath, e);
        }
        return namespaceMap;
    }

    private JQuickXmlNamespace parseCurlsElement(Element curlsElement) {
        JQuickXmlNamespace namespace = new JQuickXmlNamespace();
        String namespaceName = curlsElement.getAttribute("namespace");
        namespace.setNamespace(namespaceName);
        NodeList curlNodes = curlsElement.getElementsByTagName("curl");
        for (int i = 0; i < curlNodes.getLength(); i++) {
            Element curlElement = (Element) curlNodes.item(i);
            JQuickXmlMethod method = parseCurlElement(curlElement);
            namespace.addMethod(method.getName(), method);
        }
        return namespace;
    }

    private JQuickXmlMethod parseCurlElement(Element curlElement) {
        JQuickXmlMethod method = new JQuickXmlMethod();
        method.setName(curlElement.getAttribute("name"));
        method.setReturnClass(curlElement.getAttribute("returnClass"));
        method.setContent(curlElement.getTextContent().trim());
        return method;
    }
}
