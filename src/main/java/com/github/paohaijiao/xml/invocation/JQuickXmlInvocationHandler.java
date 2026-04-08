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
import com.github.paohaijiao.enums.JLogLevel;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.result.JResult;
import com.github.paohaijiao.result.factory.JResultFactory;
import com.github.paohaijiao.type.JTypeReference;
import com.github.paohaijiao.xml.method.JQuickXmlMethod;
import com.github.paohaijiao.xml.namespace.JQuickXmlNamespace;
import com.github.paohaijiao.xml.param.Param;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * packageName com.github.paohaijiao.xml.invocation
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/11/27
 */
@Slf4j
public abstract class JQuickXmlInvocationHandler implements InvocationHandler {

    protected static JConsole console=new JConsole();

    protected  JQuickXmlNamespace namespace;

    protected  JContext context ;

    public void init(JQuickXmlNamespace namespace, JContext context){
        this.namespace=namespace;
        this.context=context;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        String methodName = method.getName();
        JAssert.notNull(namespace,"No NameSpace Configuration Found For Xml ");
        JQuickXmlMethod xmlMethod = namespace.getMethods().get(methodName);
        JAssert.notNull(xmlMethod,"No XmlMethod Configuration Found For Method: " + methodName);
        Map<String, Object> paramMap = buildParamMap(method, args);
        if(null==context){
            context = new JContext();
        }
        context.putAll(paramMap);
        return execute(xmlMethod, context, method);
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
    protected abstract Object loadResult(String rawResult, JContext context, Method method);

    protected  Object execute(JQuickXmlMethod xmlMethod, JContext context, Method method) throws IOException {
        String content = xmlMethod.getContent();
        JContext jContext=new JContext();
        jContext.putAll(context);
        String dynamicParsedContent = JQuickEvaluateProcessor.parse(content, jContext);
        String lexer = replaceVariables(dynamicParsedContent, jContext);
        Object rawResult = loadResult(lexer,context,method);
        console.log(JLogLevel.INFO,"result:"+ rawResult);
        return rawResult;
    }

    protected JTypeReference<?> createTypeReference(Type genericType) {
        return new JTypeReference<Object>() {
            @Override
            public Type getType() {
                return genericType;
            }
        };
    }
    protected String replaceVariables(String command, JContext context) {
        if (command == null || command.isEmpty()) {
            return command;
        }
        Pattern pattern = Pattern.compile("#\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(command);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object value = context.get(variableName);
            if (value != null) {
                String replacement = Matcher.quoteReplacement(value.toString());
                matcher.appendReplacement(sb,replacement);
            } else {
                log.info("Variable #{} not found in context, keeping placeholder", variableName);
                matcher.appendReplacement(sb, matcher.group(0)); // 保持原样
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}