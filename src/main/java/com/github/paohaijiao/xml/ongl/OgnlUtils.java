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
package com.github.paohaijiao.xml.ongl;

/**
 * packageName com.github.paohaijiao.xml.ongl
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/4/4
 */
import com.github.paohaijiao.param.JContext;
import lombok.extern.slf4j.Slf4j;
import ognl.Ognl;
import ognl.OgnlException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class OgnlUtils {

    private static final Map<String, Object> EXPRESSION_CACHE = new ConcurrentHashMap<>();

    /**
     * 解析 OGNL 表达式并求值
     * @param expression OGNL 表达式
     * @param context 上下文对象
     * @return 表达式求值结果
     */
    public static Object getValue(String expression, JContext context) {
        try {
            Object parsedExpression = parseExpression(expression);
            return Ognl.getValue(parsedExpression, context);
        } catch (OgnlException e) {
            log.error("Failed to evaluate OGNL expression: {}", expression, e);
            return null;
        }
    }

    /**
     * 解析 OGNL 表达式并返回布尔值
     * @param expression OGNL 表达式
     * @param context 上下文对象
     * @return 布尔结果
     */
    public static boolean getBoolean(String expression, JContext context) {
        Object value = getValue(expression, context);
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0;
        }
        if (value instanceof String) {
            return !((String) value).isEmpty();
        }
        return value != null;
    }

    /**
     * 解析并缓存 OGNL 表达式
     */
    private static Object parseExpression(String expression) throws OgnlException {
        Object parsed = EXPRESSION_CACHE.get(expression);
        if (parsed == null) {
            parsed = Ognl.parseExpression(expression);
            EXPRESSION_CACHE.put(expression, parsed);
        }
        return parsed;
    }

    /**
     * 设置值到上下文
     */
    public static void setValue(String expression, JContext context, Object value) {
        try {
            Object parsedExpression = parseExpression(expression);
            Ognl.setValue(parsedExpression, context, value);
        } catch (OgnlException e) {
            log.error("Failed to set OGNL expression: {}", expression, e);
        }
    }
}
