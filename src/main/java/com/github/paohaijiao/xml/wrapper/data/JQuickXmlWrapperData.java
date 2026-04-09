package com.github.paohaijiao.xml.wrapper.data;

import lombok.Data;

import java.util.HashMap;
@Data
public class JQuickXmlWrapperData {

    private Object data;

    private HashMap<String,Object> attributes=new HashMap<>();
    public JQuickXmlWrapperData() {

    }
    public JQuickXmlWrapperData(Object data) {
       this.data=data;
    }
    public JQuickXmlWrapperData(Object data,HashMap<String,Object> attributes) {
        this.data=data;
        this.attributes=attributes;
    }
    public void addAttribute(String name, Object value) {
        attributes.put(name, value);
    }
    public Object getAttribute(String name) {
        return attributes.get(name);
    }
}
