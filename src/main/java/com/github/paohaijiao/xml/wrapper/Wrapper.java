package com.github.paohaijiao.xml.wrapper;

import com.github.paohaijiao.spi.anno.Priority;
import com.github.paohaijiao.spi.constants.PriorityConstants;
import com.github.paohaijiao.xml.wrapper.data.JQuickXmlWrapperData;

@Priority(PriorityConstants.APPLICATION_MEDIUM)
public interface Wrapper {
    /**
     * whether support the convert Function
     * @return
     */
    public boolean support(JQuickXmlWrapperData value);

    /**
     * wrap the value
     * @param value
     * @return
     */
    public Object wrap(JQuickXmlWrapperData value);
}
