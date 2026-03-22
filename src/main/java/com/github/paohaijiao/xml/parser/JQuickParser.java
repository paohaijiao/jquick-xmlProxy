package com.github.paohaijiao.xml.parser;

import com.github.paohaijiao.xml.namespace.JQuickXmlNamespace;

import java.util.Map;

public interface JQuickParser {

    public Map<String, JQuickXmlNamespace> parse(String xmlPath);
}
