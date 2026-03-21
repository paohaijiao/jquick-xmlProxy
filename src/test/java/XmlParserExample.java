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

import com.github.paohaijiao.xml.proxry.XmlMapperProxy;
import com.paohaijiao.UserApi;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlParserExample {

    public static void main(String[] args) throws Exception {
        InputStream xmlStream = XmlParserExample.class.getResourceAsStream("apis.xml");
        UserApi userApi = XmlMapperProxy.getMapper(UserApi.class, xmlStream);
        List<Map<String, Object>> allUsers = userApi.all();
        System.out.println("All users: " + allUsers);
        Map<String, Object> user = userApi.findByName("张三");
        System.out.println("User: " + user);

        // 参数化查询
        Map<String, Object> params = new HashMap<>();
        params.put("status", "active");
        List<Map<String, Object>> activeUsers = userApi.search(params);
        System.out.println("Active users: " + activeUsers);
    }
}
