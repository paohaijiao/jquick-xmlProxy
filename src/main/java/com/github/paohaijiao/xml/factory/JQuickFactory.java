package com.github.paohaijiao.xml.factory;

public interface JQuickFactory {

    public <T> T createApi(Class<T> apiInterface);
}
