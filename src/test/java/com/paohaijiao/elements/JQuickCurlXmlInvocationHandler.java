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
package com.paohaijiao.elements;

import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.result.JResult;
import com.github.paohaijiao.type.JTypeReference;
import com.github.paohaijiao.xml.invocation.JQuickXmlInvocationHandler;

import java.lang.reflect.Method;

/**
 * packageName com.paohaijiao.elements
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/3/26
 */
public class JQuickCurlXmlInvocationHandler extends JQuickXmlInvocationHandler {
    @Override
    protected Object loadResult(String rawResult, JContext context, Method typeReference, Object[] args) {
        JResult result=JResult.builder().string("[{\"userId\":\"12345\",\"userName\":\"张三\"}]").build();
        return result;
    }
}
