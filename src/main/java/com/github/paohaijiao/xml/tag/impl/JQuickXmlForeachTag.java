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

/**
 * packageName com.github.paohaijiao.xml.tag.impl
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/4/4
 */

import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.xml.ongl.OgnlUtils;
import com.github.paohaijiao.xml.tag.JQuickXmlTag;
import lombok.Data;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class JQuickXmlForeachTag implements JQuickXmlTag {

    private String collection;

    private String item;

    private String index;

    private String separator;

    private String open;

    private String close;

    private String content;

    @Override
    public String apply(JContext context) {
        Object collectionObj = OgnlUtils.getValue(collection, context);
        if (collectionObj == null) {
            return "";
        }
        Collection<?> items;
        if (collectionObj instanceof Collection) {
            items = (Collection<?>) collectionObj;
        } else if (collectionObj instanceof Object[]) {
            items = java.util.Arrays.asList((Object[]) collectionObj);
        } else if (collectionObj instanceof Map) {
            items = ((Map<?, ?>) collectionObj).entrySet();
        } else {
            return "";
        }
        StringBuilder result = new StringBuilder();
        if (open != null) {
            result.append(open);
        }
        int idx = 0;
        for (Object obj : items) {
            if (idx > 0 && separator != null) {
                result.append(separator);
            }
            JContext itemContext = new JContext();
            itemContext.putAll(context);
            itemContext.put(item, obj);
            if (index != null && !index.isEmpty()) {
                itemContext.put(index, idx);
            }
            itemContext.put(item + "_index", idx);
            // 在这里替换变量
            String processedContent = replaceVariables(content, itemContext);
            result.append(processedContent);
            idx++;
        }

        if (close != null) {
            result.append(close);
        }

        return result.toString();
    }


    private String replaceVariables(String text, JContext context) {
        Pattern pattern = Pattern.compile("#\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String expression = matcher.group(1);
            Object value = OgnlUtils.getValue(expression, context);
            if (value != null) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(value.toString()));
            } else {
                matcher.appendReplacement(sb, matcher.group(0));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
