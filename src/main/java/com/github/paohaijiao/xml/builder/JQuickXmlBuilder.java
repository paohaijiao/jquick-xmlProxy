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
package com.github.paohaijiao.xml.builder;


import com.github.paohaijiao.xml.method.JQuickXmlMethod;
import com.github.paohaijiao.xml.namespace.JQuickXmlNamespace;

import java.util.HashMap;
import java.util.Map;

/**
 * XML配置构建器，提供流式API设置模型值
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/12/16
 */
public class JQuickXmlBuilder {

    private final JQuickXmlConfigurer configurer = new JQuickXmlConfigurerImpl();

    private final Map<String, JQuickXmlMethod> pendingMethods = new HashMap<>();

    private final JQuickXmlNamespace namespace;

    public JQuickXmlBuilder() {
        this.namespace = new JQuickXmlNamespace();
    }

    /**
     * 创建新的构建器实例
     */
    public static JQuickXmlBuilder create() {
        return new JQuickXmlBuilder();
    }

    /**
     * 设置命名空间
     */
    public JQuickXmlBuilder namespace(String namespaceValue) {
        configurer.setNamespace(namespace, namespaceValue);
        return this;
    }

    /**
     * 添加方法
     */
    public JQuickXmlBuilder addMethod(String methodName, String returnClass, String content) {
        JQuickXmlMethod method = new JQuickXmlMethod();
        configurer.setMethodAttributes(method, methodName, returnClass, content);
        pendingMethods.put(methodName, method);
        return this;
    }

    /**
     * 添加方法（使用已有方法对象）
     */
    public JQuickXmlBuilder addMethod(String methodName, JQuickXmlMethod method) {
        pendingMethods.put(methodName, method);
        return this;
    }

    /**
     * 批量添加方法
     */
    public JQuickXmlBuilder addMethods(Map<String, JQuickXmlMethod> methods) {
        pendingMethods.putAll(methods);
        return this;
    }

    /**
     * 构建并返回JQuickXmlNamespace对象
     */
    public JQuickXmlNamespace build() {
        configurer.setMethods(namespace, pendingMethods);
        return namespace;
    }

    /**
     * 获取当前命名空间对象
     */
    public JQuickXmlNamespace getNamespace() {
        return namespace;
    }

    /**
     * 获取配置器
     */
    public JQuickXmlConfigurer getConfigurer() {
        return configurer;
    }
}