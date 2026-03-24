package com.github.paohaijiao.xml.handler;

import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.xml.element.JQuickXmlElement;
import com.github.paohaijiao.xml.invocation.JQuickXmlInvocationHandler;

public interface JQuickParseHandler {
    /**
     * define the xml structs
     * @return
     */
    JQuickXmlElement createJQuickXmlElement();
    /**
     *  composed the result
     * @param context
     * @return
     */
    JQuickXmlInvocationHandler createlInvocationHandler(JContext context);

}
