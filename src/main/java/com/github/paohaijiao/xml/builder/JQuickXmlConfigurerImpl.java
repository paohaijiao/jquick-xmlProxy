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
 * XML配置动态设置实现类
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/12/16
 */
public class JQuickXmlConfigurerImpl implements JQuickXmlConfigurer {

    @Override
    public void setNamespace(JQuickXmlNamespace namespace, String namespaceValue) {
        if (namespace != null) {
            namespace.setNamespace(namespaceValue);
        }
    }

    @Override
    public void setMethod(JQuickXmlNamespace namespace, String methodName, JQuickXmlMethod method) {
        if (namespace != null && method != null) {
            namespace.addMethod(methodName, method);
        }
    }

    @Override
    public void setMethods(JQuickXmlNamespace namespace, Map<String, JQuickXmlMethod> methods) {
        if (namespace != null && methods != null) {
            for (Map.Entry<String, JQuickXmlMethod> entry : methods.entrySet()) {
                namespace.addMethod(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void setMethodName(JQuickXmlMethod method, String name) {
        if (method != null) {
            method.setName(name);
        }
    }

    @Override
    public void setMethodReturnClass(JQuickXmlMethod method, String returnClass) {
        if (method != null) {
            method.setReturnClass(returnClass);
        }
    }

    @Override
    public void setMethodContent(JQuickXmlMethod method, String content) {
        if (method != null) {
            method.setContent(content);
        }
    }

    @Override
    public void setMethodAttributes(JQuickXmlMethod method, String name, String returnClass, String content) {
        if (method != null) {
            method.setName(name);
            method.setReturnClass(returnClass);
            method.setContent(content);
        }
    }

    @Override
    public void setMethodAttributesMap(JQuickXmlMethod method, HashMap<String, String> map) {
        if (method != null) {
            method.setMap(map);
        }
    }
}