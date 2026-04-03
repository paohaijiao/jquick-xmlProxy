package com.github.paohaijiao.xml.builder;
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

import com.github.paohaijiao.xml.method.JQuickXmlMethod;
import com.github.paohaijiao.xml.namespace.JQuickXmlNamespace;

import java.util.HashMap;
import java.util.Map;

/**
 * XML配置动态设置接口
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/12/16
 */
public interface JQuickXmlConfigurer {

    /**
     * 设置命名空间
     *
     * @param namespace      命名空间对象
     * @param namespaceValue 命名空间值
     */
    void setNamespace(JQuickXmlNamespace namespace, String namespaceValue);

    /**
     * 设置方法
     *
     * @param namespace  命名空间对象
     * @param methodName 方法名
     * @param method     方法对象
     */
    void setMethod(JQuickXmlNamespace namespace, String methodName, JQuickXmlMethod method);

    /**
     * 批量设置方法
     *
     * @param namespace 命名空间对象
     * @param methods   方法映射表
     */
    void setMethods(JQuickXmlNamespace namespace, Map<String, JQuickXmlMethod> methods);

    /**
     * 设置方法名称
     *
     * @param method 方法对象
     * @param name   方法名称
     */
    void setMethodName(JQuickXmlMethod method, String name);

    /**
     * 设置方法返回类型
     *
     * @param method      方法对象
     * @param returnClass 返回类型
     */
    void setMethodReturnClass(JQuickXmlMethod method, String returnClass);

    /**
     * 设置方法内容
     *
     * @param method  方法对象
     * @param content 方法内容
     */
    void setMethodContent(JQuickXmlMethod method, String content);

    /**
     * 批量设置方法属性
     *
     * @param method      方法对象
     * @param name        方法名称
     * @param returnClass 返回类型
     * @param content     方法内容
     */
    void setMethodAttributes(JQuickXmlMethod method, String name, String returnClass, String content);

    /**
     *
     * @param method
     * @param map
     */
    void setMethodAttributesMap(JQuickXmlMethod method, HashMap<String, String> map);
}
