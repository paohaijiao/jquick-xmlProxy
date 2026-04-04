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
package com.github.paohaijiao.xml.tag.impl;

import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.xml.ongl.OgnlUtils;
import com.github.paohaijiao.xml.tag.JQuickXmlTag;
import lombok.Data;

@Data
public class JQuickXmlIfTag implements JQuickXmlTag {

    private String test;

    private String content;

    @Override
    public String apply(JContext context) {
        if (evaluateTest(context)) {
            return content;
        }
        return "";
    }

    private boolean evaluateTest(JContext context) {
        if (test == null || test.trim().isEmpty()) {
            return false;
        }
        try {
            return OgnlUtils.getBoolean(test, context);
        } catch (Exception e) {
            return evaluateLegacyTest(context);
        }
    }

    /**
     * 兼容旧版表达式
     */
    private boolean evaluateLegacyTest(JContext context) {
        String expr = test.trim();
        if (expr.contains("!=")) {
            String[] parts = expr.split("!=");
            Object left = getValueLegacy(parts[0].trim(), context);
            Object right = getValueLegacy(parts[1].trim(), context);
            return !equalsValue(left, right);
        } else if (expr.contains("==")) {
            String[] parts = expr.split("==");
            Object left = getValueLegacy(parts[0].trim(), context);
            Object right = getValueLegacy(parts[1].trim(), context);
            return equalsValue(left, right);
        } else if (expr.contains(">")) {
            String[] parts = expr.split(">");
            Object left = getValueLegacy(parts[0].trim(), context);
            Object right = getValueLegacy(parts[1].trim(), context);
            return compareNumeric(left, right) > 0;
        } else if (expr.contains("<")) {
            String[] parts = expr.split("<");
            Object left = getValueLegacy(parts[0].trim(), context);
            Object right = getValueLegacy(parts[1].trim(), context);
            return compareNumeric(left, right) < 0;
        }

        Object val = getValueLegacy(expr, context);
        if (val == null) return false;
        if (val instanceof String) return !((String) val).isEmpty();
        if (val instanceof Boolean) return (Boolean) val;
        return true;
    }

    private Object getValueLegacy(String name, JContext context) {
        if ((name.startsWith("'") && name.endsWith("'")) ||
                (name.startsWith("\"") && name.endsWith("\""))) {
            return name.substring(1, name.length() - 1);
        }
        try {
            if (name.contains(".")) {
                return Double.parseDouble(name);
            }
            return Integer.parseInt(name);
        } catch (NumberFormatException e) {
        }
        if ("true".equalsIgnoreCase(name)) return true;
        if ("false".equalsIgnoreCase(name)) return false;
        return context.get(name);
    }

    private boolean equalsValue(Object left, Object right) {
        if (left == null && right == null) return true;
        if (left == null || right == null) return false;
        return left.toString().equals(right.toString());
    }

    private int compareNumeric(Object left, Object right) {
        double leftNum = toDouble(left);
        double rightNum = toDouble(right);
        return Double.compare(leftNum, rightNum);
    }

    private double toDouble(Object val) {
        if (val == null) return 0;
        if (val instanceof Number) return ((Number) val).doubleValue();
        try {
            return Double.parseDouble(val.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}