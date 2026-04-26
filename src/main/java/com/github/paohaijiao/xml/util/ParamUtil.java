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
package com.github.paohaijiao.xml.util;

import com.github.paohaijiao.xml.param.Param;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.xml.util
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/4/26
 */
public class ParamUtil {

    public static Map<String,Object> bindParams(Method method, Object[] args){
        HashMap<String,Object> map=new HashMap<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Param param = parameters[i].getAnnotation(Param.class);
            if (param != null) {
                String paramName = param.value();
                Object value = args[i];
                map.put(paramName, value);
            }else{
                String paramName  = parameters[i].getName();
                Object value = args[i];
                map.put(paramName, value);
            }
        }
        return map;
    }
}
