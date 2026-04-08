package com.github.paohaijiao.xml.element;

import java.util.List;

public interface JQuickXmlElement {
    /**
     * 获取元素名称
     */
    public String getNameSpaceName();

    public String getRootElementTagName();

    public List<String> getChildElementTagName();

    public String getMethodName();

    public String getMethodReturnClass();

    public String getMethodParamClass();
    /**
     * 获取元素值
     */
    public String getValue();
}
