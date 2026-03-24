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

import com.github.paohaijiao.xml.factory.JQuickFactory;
import com.github.paohaijiao.xml.factory.JQuickXmlFactory;
import com.github.paohaijiao.xml.handler.JQuickCurlXmlParseHandler;
import com.github.paohaijiao.xml.handler.JQuickParseHandler;
import com.paohaijiao.UserApi;

import java.util.List;
import java.util.Map;


public class XmlParserExample {

    public static void main(String[] args) throws Exception {
        JQuickParseHandler handler=new JQuickCurlXmlParseHandler();
        JQuickFactory factory = new JQuickXmlFactory(handler,"apis.xml");
        System.out.println(factory);
        UserApi userApi = factory.createApi(UserApi.class);
        System.out.println(userApi);
        List<Map<String, Object>> list= userApi.all();
        System.out.println(list);
    }
}
