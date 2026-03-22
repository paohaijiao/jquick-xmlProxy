package com.github.paohaijiao.xml.element;

public interface JQuickXmlElement {
    /**
     * 获取元素名称
     */
    String getNameSpaceName();

    String getRootElementTagName();

    String getChildElementTagName();

    String getMethodName();

    String getMethodReturnClass();

    String getMethodParamClass();
    /**
     * 获取元素值
     */
    String getValue();
}
