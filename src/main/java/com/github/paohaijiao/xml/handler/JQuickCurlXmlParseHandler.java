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
package com.github.paohaijiao.xml.handler;

import com.github.paohaijiao.xml.element.JQuickCurlXmlElement;
import com.github.paohaijiao.xml.element.JQuickXmlElement;
import com.github.paohaijiao.xml.parser.JQuickCurlXmlParser;

/**
 * packageName com.github.paohaijiao.xml.handler
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/3/22
 */
public class JQuickCurlXmlParseHandler implements JQuickParseHandler{

    @Override
    public JQuickXmlElement getJQuickXmlElement() {
        return new JQuickCurlXmlElement();
    }

    @Override
    public JQuickCurlXmlParser getJQuickXmlParser(JQuickXmlElement element) {
        return new JQuickCurlXmlParser(element);
    }
}
