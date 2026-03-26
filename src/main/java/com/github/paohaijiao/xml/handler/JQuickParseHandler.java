package com.github.paohaijiao.xml.handler;
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
     * @return
     */
    JQuickXmlInvocationHandler createlInvocationHandler();

}
