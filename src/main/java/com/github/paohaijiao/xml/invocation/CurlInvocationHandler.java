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
package com.github.paohaijiao.xml.invocation;

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.type.JTypeReference;
import com.github.paohaijiao.xml.method.CurlMethod;
import com.github.paohaijiao.xml.namespace.CurlNamespace;
import com.github.paohaijiao.xml.param.Param;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.xml.invocation
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/11/27
 */
@Slf4j
public class CurlInvocationHandler implements InvocationHandler {

    private static JConsole console=new JConsole();

    private final CurlNamespace namespace;

    public CurlInvocationHandler(CurlNamespace namespace) {
        this.namespace = namespace;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        String methodName = method.getName();
        CurlMethod curlMethod = namespace.getMethods().get(methodName);
        JAssert.notNull(curlMethod,"No curl configuration found for method: " + methodName);
        Map<String, Object> paramMap = buildParamMap(method, args);
        JContext context = new JContext();
        context.putAll(paramMap);
        return executeCurl(curlMethod, context, method);
    }

    private Map<String, Object> buildParamMap(Method method, Object[] args) {
        Map<String, Object> paramMap = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameters.length; i++) {
            Object argValue = args[i];
            boolean hasParamAnnotation = false;
            for (Annotation annotation : parameterAnnotations[i]) {// 检查 @Param 注解
                if (annotation instanceof Param) {
                    Param paramAnnotation = (Param) annotation;
                    paramMap.put(paramAnnotation.value(), argValue);
                    hasParamAnnotation = true;
                    break;
                }
            }
            if (!hasParamAnnotation) {
                paramMap.put(parameters[i].getName(), argValue);
            }
        }

        return paramMap;
    }

    private Object executeCurl(CurlMethod curlMethod, JContext context,  Method method) {
        String curlCommand = curlMethod.getCurlCommand();
        return null;
    }

    private JTypeReference<?> createTypeReference(Type genericType) {
        return new JTypeReference<Object>() {
            @Override
            public Type getType() {
                return genericType;
            }
        };
    }
}