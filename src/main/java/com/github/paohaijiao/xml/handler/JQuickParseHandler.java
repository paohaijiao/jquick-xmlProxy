package com.github.paohaijiao.xml.handler;

import com.github.paohaijiao.xml.element.JQuickXmlElement;
import com.github.paohaijiao.xml.parser.JQuickCurlXmlParser;

public interface JQuickParseHandler {

    JQuickXmlElement getJQuickXmlElement();

    JQuickCurlXmlParser getJQuickXmlParser(JQuickXmlElement element);




}
