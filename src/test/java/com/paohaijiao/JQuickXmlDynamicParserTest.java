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
package com.paohaijiao;

import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.xml.invocation.JQuickEvaluateProcessor;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class JQuickXmlDynamicParserTest {


    @Test
    public void testPlainText() {
        JContext context = new JContext();
        String content = "curl -X GET http://localhost:8080/api/users";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals(content, result);
    }

    @Test
    public void testVariableSubstitution() {
        JContext context = new JContext();
        context.put("name", "John Doe");
        String content = "curl -X GET http://localhost:8080/api/users/#{#name}";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users/John Doe", result);
    }

    @Test
    public void testMultipleVariableSubstitution() {
        JContext context = new JContext();
        context.put("name", "John Doe");
        context.put("age", 25);
        context.put("city", "Beijing");
        String content = "curl -X POST http://localhost:8080/api/users -d '{\"name\":\"#{#name}\",\"age\":#{#age},\"city\":\"#{#city}\"}'";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X POST http://localhost:8080/api/users -d '{\"name\":\"John Doe\",\"age\":25,\"city\":\"Beijing\"}'", result);
    }

    @Test
    public void testOgnlVariableSubstitution() {
        JContext context = new JContext();
        Map<String, Object> address = new HashMap<>();
        address.put("city", "Beijing");
        address.put("zipCode", 100000);
        context.put("address", address);
        String content = "curl -X GET http://localhost:8080/api/users?city=#{address.city}&zip=#{address.zipCode}";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users?city=Beijing&zip=100000", result);
    }

    @Test
    public void testVariableNotFound() {
        JContext context = new JContext();
        context.put("name", "John Doe");
        String content = "curl -X GET http://localhost:8080/api/users/#{nonExistent}";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users/#{nonExistent}", result);
    }

    @Test
    public void testEmptyContent() {
        JContext context = new JContext();
        assertEquals("", JQuickEvaluateProcessor.parse("", context));
        assertNull(JQuickEvaluateProcessor.parse(null, context));
    }

    @Test
    public void testIfTagTrue() {
        JContext context = new JContext();
        context.put("age", 25);
        String content = "curl -X GET http://localhost:8080/api/users<if test=\"age > 18\">/adult</if>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users/adult", result);
    }

    @Test
    public void testIfTagFalse() {
        JContext context = new JContext();
        context.put("age", 16);
        String content = "curl -X GET http://localhost:8080/api/users<if test=\"age < 18\">/adult</if>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users", result);
    }

    @Test
    public void testIfTagStringEquals() {
        JContext context = new JContext();
        context.put("status", "active");
        String content = "curl -X GET http://localhost:8080/api/users<if test=\"status == 'active'\">/active</if>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users/active", result);
    }

    @Test
    public void testIfTagStringNotEquals() {
        JContext context = new JContext();
        context.put("status", "inactive");
        String content = "curl -X GET http://localhost:8080/api/users<if test=\"status != 'active'\">/inactive</if>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users/inactive", result);
    }

    @Test
    public void testIfTagNullCheck() {
        JContext context = new JContext();
        context.put("name", null);
        String content = "curl -X GET http://localhost:8080/api/users<if test=\"name != null\">/named</if>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users", result);
    }

    @Test
    public void testIfTagNotNullCheck() {
        JContext context = new JContext();
        context.put("name", "John");
        String content = "curl -X GET http://localhost:8080/api/users<if test=\"name != null\">/named</if>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users/named", result);
    }

    @Test
    public void testIfTagComplexOgnl() {
        JContext context = new JContext();
        Map<String, Object> user = new HashMap<>();
        user.put("age", 30);
        user.put("role", "admin");
        List<String> permissions = Arrays.asList("read", "write", "delete");
        user.put("permissions", permissions);
        context.put("user", user);
        String content = "curl -X POST http://localhost:8080/api/users" +
                "<if test=\"user != null &&user.age > 25 && user.role == 'admin' && user.permissions.contains('delete')\">/admin/delete</if>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X POST http://localhost:8080/api/users/admin/delete", result);
    }

    @Test
    public void testIfTagBooleanTrue() {
        JContext context = new JContext();
        context.put("isActive", true);
        String content = "curl -X GET http://localhost:8080/api/users<if test=\"isActive\">/active</if>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users/active", result);
    }

    @Test
    public void testIfTagBooleanFalse() {
        JContext context = new JContext();
        context.put("isActive", false);
        String content = "curl -X GET http://localhost:8080/api/users<if test=\"isActive\">/active</if>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users", result);
    }

    @Test
    public void testIfElseSimulation() {
        JContext context = new JContext();
        context.put("age", 20);
        String content = "curl -X GET http://localhost:8080/api/users" +
                "<if test=\"age >= 18\">/adult</if>" +
                "<if test=\"age < 18\">/minor</if>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users/adult", result);
    }
    @Test
    public void testForeachTagBasic() {
        JContext context = new JContext();
        context.put("userIds", Arrays.asList(1, 2, 3, 4, 5));
        String content = "curl -X GET http://localhost:8080/api/users/batch?ids=" +
                "<foreach collection=\"userIds\" item=\"id\" separator=\",\">#{id}</foreach>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users/batch?ids=1,2,3,4,5", result);
    }

    @Test
    public void testForeachTagWithOpenClose() {
        JContext context = new JContext();
        context.put("userIds", Arrays.asList(1, 2, 3));

        String content = "curl -X POST http://localhost:8080/api/users/batch -d '{\"ids\":[" +
                "<foreach collection=\"userIds\" item=\"id\" separator=\",\" open=\" \" close=\" \">#{id}</foreach>" +
                "]}'";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X POST http://localhost:8080/api/users/batch -d '{\"ids\":[ 1,2,3 ]}'", result);
    }

    @Test
    public void testForeachTagStringList() {
        JContext context = new JContext();
        context.put("names", Arrays.asList("Alice", "Bob", "Charlie"));

        String content = "curl -X POST http://localhost:8080/api/users/search -d '{\"names\":[" +
                "<foreach collection=\"names\" item=\"name\" separator=\",\">\"#{name}\"</foreach>" +
                "]}'";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X POST http://localhost:8080/api/users/search -d '{\"names\":[\"Alice\",\"Bob\",\"Charlie\"]}'", result);
    }

    @Test
    public void testForeachTagArray() {
        JContext context = new JContext();
        context.put("scores", new int[]{85, 90, 95});

        String content = "curl -X POST http://localhost:8080/api/users/scores -d '{\"scores\":[" +
                "<foreach collection=\"scores\" item=\"score\" separator=\",\">#{score}</foreach>" +
                "]}'";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X POST http://localhost:8080/api/users/scores -d '{\"scores\":[85,90,95]}'", result);
    }

    @Test
    public void testForeachTagWithIndex() {
        JContext context = new JContext();
        context.put("names", Arrays.asList("Alice", "Bob"));

        String content = "curl -X POST http://localhost:8080/api/users -d '{" +
                "<foreach collection=\"names\" item=\"name\" separator=\",\">\"#{name}\":#{name_index}</foreach>" +
                "}'";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X POST http://localhost:8080/api/users -d '{\"Alice\":0,\"Bob\":1}'", result);
    }

    @Test
    public void testForeachTagEmptyCollection() {
        JContext context = new JContext();
        context.put("emptyList", Collections.emptyList());

        String content = "curl -X GET http://localhost:8080/api/users/batch?ids=" +
                "<foreach collection=\"emptyList\" item=\"id\" separator=\",\">#{id}</foreach>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users/batch?ids=", result);
    }

    @Test
    public void testForeachTagNullCollection() {
        JContext context = new JContext();
        context.put("nullList", null);

        String content = "curl -X GET http://localhost:8080/api/users/batch?ids=" +
                "<foreach collection=\"nullList\" item=\"id\" separator=\",\">#{id}</foreach>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users/batch?ids=", result);
    }

    // ==================== choose-when-otherwise 标签测试 ====================

    @Test
    public void testChooseWhenOtherwise() {
        JContext context = new JContext();
        context.put("operation", "update");

        String content = "curl -X POST http://localhost:8080/api/users" +
                "<choose>" +
                "<when test=\"operation == 'create'\">/create</when>" +
                "<when test=\"operation == 'update'\">/update</when>" +
                "<when test=\"operation == 'delete'\">/delete</when>" +
                "<otherwise>/list</otherwise>" +
                "</choose>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X POST http://localhost:8080/api/users/update", result);
    }

    @Test
    public void testChooseWhenFirstMatch() {
        JContext context = new JContext();
        context.put("operation", "create");

        String content = "curl -X POST http://localhost:8080/api/users" +
                "<choose>" +
                "<when test=\"operation == 'create'\">/create</when>" +
                "<when test=\"operation == 'update'\">/update</when>" +
                "<otherwise>/default</otherwise>" +
                "</choose>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X POST http://localhost:8080/api/users/create", result);
    }

    @Test
    public void testChooseOtherwiseNoMatch() {
        JContext context = new JContext();
        context.put("operation", "unknown");

        String content = "curl -X GET http://localhost:8080/api/users" +
                "<choose>" +
                "<when test=\"operation == 'create'\">/create</when>" +
                "<when test=\"operation == 'update'\">/update</when>" +
                "<otherwise>/default</otherwise>" +
                "</choose>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users/default", result);
    }

    @Test
    public void testChooseNoOtherwise() {
        JContext context = new JContext();
        context.put("operation", "unknown");

        String content = "curl -X GET http://localhost:8080/api/users" +
                "<choose>" +
                "<when test=\"operation == 'create'\">/create</when>" +
                "<when test=\"operation == 'update'\">/update</when>" +
                "</choose>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users", result);
    }

    // ==================== where 标签测试 ====================

    @Test
    public void testWhereTag() {
        JContext context = new JContext();
        context.put("name", "John");
        context.put("age", 25);
        context.put("city", "Beijing");

        String content = "curl -X GET \"http://localhost:8080/api/users?" +
                "<where>" +
                "<if test=\"name != null\">name=#{name}</if>" +
                "<if test=\"age != null\">AND age=#{age}</if>" +
                "<if test=\"city != null\">AND city=#{city}</if>" +
                "</where>" +
                "\"";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET \"http://localhost:8080/api/users?WHERE name=John AND age=25 AND city=Beijing\"", result);
    }

    @Test
    public void testWhereTagSingleCondition() {
        JContext context = new JContext();
        context.put("name", "John");

        String content = "curl -X GET \"http://localhost:8080/api/users?" +
                "<where>" +
                "<if test=\"name != null\">name=#{name}</if>" +
                "</where>" +
                "\"";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET \"http://localhost:8080/api/users?WHERE name=John\"", result);
    }

    @Test
    public void testWhereTagNoCondition() {
        JContext context = new JContext();

        String content = "curl -X GET \"http://localhost:8080/api/users?" +
                "<where>" +
                "<if test=\"name != null\">name=#{name}</if>" +
                "</where>" +
                "\"";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET \"http://localhost:8080/api/users?\"", result);
    }

    @Test
    public void testWhereTagRemovesLeadingAnd() {
        JContext context = new JContext();
        context.put("age", 25);
        context.put("city", "Beijing");

        String content = "curl -X GET \"http://localhost:8080/api/users?" +
                "<where>" +
                "<if test=\"name != null\">name=#{name}</if>" +
                "<if test=\"age != null\">AND age=#{age}</if>" +
                "<if test=\"city != null\">AND city=#{city}</if>" +
                "</where>" +
                "\"";
        String result = JQuickEvaluateProcessor.parse(content, context);
        // 第一个条件不存在，应该移除第一个 AND
        assertEquals("curl -X GET \"http://localhost:8080/api/users?WHERE age=25 AND city=Beijing\"", result);
    }

    // ==================== set 标签测试 ====================

    @Test
    public void testSetTag() {
        JContext context = new JContext();
        context.put("name", "John Doe");
        context.put("age", 25);
        context.put("email", "john@example.com");

        String content = "curl -X PUT http://localhost:8080/api/users/1 -d '{" +
                "<set>" +
                "<if test=\"name != null\">\"name\":\"#{name}\",</if>" +
                "<if test=\"age != null\">\"age\":#{age},</if>" +
                "<if test=\"email != null\">\"email\":\"#{email}\",</if>" +
                "</set>" +
                "}'";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertTrue(result.contains("\"name\":\"John Doe\""));
        assertTrue(result.contains("\"age\":25"));
        assertTrue(result.contains("\"email\":\"john@example.com\""));
        assertFalse(result.endsWith(","));
    }

    @Test
    public void testSetTagRemovesTrailingComma() {
        JContext context = new JContext();
        context.put("name", "John Doe");

        String content = "curl -X PUT http://localhost:8080/api/users/1 -d '{" +
                "<set>" +
                "<if test=\"name != null\">\"name\":\"#{name}\",</if>" +
                "<if test=\"age != null\">\"age\":#{age},</if>" +
                "</set>" +
                "}'";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X PUT http://localhost:8080/api/users/1 -d '{\"name\":\"John Doe\"}'", result);
    }

    @Test
    public void testSetTagAllConditionsFalse() {
        JContext context = new JContext();

        String content = "curl -X PUT http://localhost:8080/api/users/1 -d '{" +
                "<set>" +
                "<if test=\"name != null\">\"name\":\"#{name}\",</if>" +
                "<if test=\"age != null\">\"age\":#{age},</if>" +
                "</set>" +
                "}'";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X PUT http://localhost:8080/api/users/1 -d '{}'", result);
    }

    // ==================== trim 标签测试 ====================

    @Test
    public void testTrimTagBasic() {
        JContext context = new JContext();
        context.put("name", "John Doe");
        context.put("age", 25);

        String content = "curl -X POST http://localhost:8080/api/users -d '{" +
                "<trim prefix=\"{\" suffix=\"}\">" +
                "\"name\":\"#{name}\"," +
                "\"age\":#{age}" +
                "</trim>" +
                "}'";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X POST http://localhost:8080/api/users -d '{\"name\":\"John Doe\",\"age\":25}'", result);
    }

    @Test
    public void testTrimTagPrefixOverrides() {
        JContext context = new JContext();
        context.put("age", 25);

        String content = "curl -X POST http://localhost:8080/api/users -d '{" +
                "<trim prefix=\"{\" prefixOverrides=\"AND|OR\">" +
                "<if test=\"name != null\">AND \"name\":\"#{name}\",</if>" +
                "<if test=\"age != null\">\"age\":#{age}</if>" +
                "</trim>" +
                "}'";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X POST http://localhost:8080/api/users -d '{\"age\":25}'", result);
    }

    @Test
    public void testTrimTagSuffixOverrides() {
        JContext context = new JContext();
        context.put("name", "John Doe");

        String content = "curl -X POST http://localhost:8080/api/users -d '{" +
                "<trim prefix=\"{\" suffix=\"}\" suffixOverrides=\",\">" +
                "\"name\":\"#{name}\"," +
                "</trim>" +
                "}'";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X POST http://localhost:8080/api/users -d '{\"name\":\"John Doe\"}'", result);
    }

    // ==================== 嵌套标签测试 ====================

    @Test
    public void testNestedIfAndForeach() {
        JContext context = new JContext();
        context.put("userIds", Arrays.asList(1, 2, 3, 4, 5));

        String content = "curl -X POST http://localhost:8080/api/users/search -d '{" +
                "<if test=\"userIds != null &amp;&amp; userIds.size() > 0\">" +
                "\"userIds\": [" +
                "<foreach collection=\"userIds\" item=\"id\" separator=\",\">#{id}</foreach>" +
                "]" +
                "</if>" +
                "}'";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X POST http://localhost:8080/api/users/search -d '{\"userIds\": [1,2,3,4,5]}'", result);
    }

    @Test
    public void testNestedForeachAndIf() {
        JContext context = new JContext();
        List<Map<String, Object>> users = new ArrayList<>();
        Map<String, Object> user1 = new HashMap<>();
        user1.put("name", "Alice");
        user1.put("age", 20);
        users.add(user1);
        Map<String, Object> user2 = new HashMap<>();
        user2.put("name", "Bob");
        user2.put("age", 25);
        users.add(user2);
        context.put("users", users);

        String content = "curl -X POST http://localhost:8080/api/users/batch -d '{\"users\":[" +
                "<foreach collection=\"users\" item=\"user\" separator=\",\">" +
                "{\"name\":\"#{user.name}\"," +
                "<if test=\"user.age >= 25\">\"status\":\"adult\"</if>" +
                "<if test=\"user.age < 25\">\"status\":\"young\"</if>" +
                "}" +
                "</foreach>" +
                "]}'";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertTrue(result.contains("\"status\":\"young\""));
        assertTrue(result.contains("\"status\":\"adult\""));
    }

    @Test
    public void testComplexNestedChooseAndForeach() {
        JContext context = new JContext();
        context.put("type", "advanced");
        List<String> users = Arrays.asList("User1", "User2", "User3");
        context.put("users", users);

        String content = "curl -X POST http://localhost:8080/api/users/process -d '{" +
                "<choose>" +
                "<when test=\"type == 'advanced'\">" +
                "\"advanced\": {\"users\": [" +
                "<foreach collection=\"users\" item=\"user\" separator=\",\">\"#{user}\"</foreach>" +
                "]}" +
                "</when>" +
                "<otherwise>\"simple\": true</otherwise>" +
                "</choose>" +
                "}'";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X POST http://localhost:8080/api/users/process -d '{\"advanced\": {\"users\": [\"User1\",\"User2\",\"User3\"]}}'", result);
    }

    @Test
    public void testDeepNestedTags() {
        JContext context = new JContext();
        context.put("enabled", true);
        context.put("items", Arrays.asList(1, 2, 3));

        String content =
                "<if test=\"enabled\">" +
                        "curl -X GET http://localhost:8080/api/items?id=" +
                        "<foreach collection=\"items\" item=\"id\" separator=\",\">" +
                        "<if test=\"id > 1\">#{id}</if>" +
                        "</foreach>" +
                        "</if>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/items?id=2,3", result);
    }

    // ==================== 真实场景测试 ====================

    @Test
    public void testRealCurlGetScenario() {
        JContext context = new JContext();
        context.put("name", "John");
        context.put("age", 25);
        context.put("city", "Beijing");

        String content = "curl -X GET \"http://localhost:8080/api/users?" +
                "<where>" +
                "<if test=\"name != null &amp;&amp; !name.isEmpty()\">name=#{name}</if>" +
                "<if test=\"age != null &amp;&amp; age > 0\">AND age=#{age}</if>" +
                "<if test=\"city != null &amp;&amp; !city.isEmpty()\">AND city=#{city}</if>" +
                "</where>" +
                "\"";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET \"http://localhost:8080/api/users?WHERE name=John AND age=25 AND city=Beijing\"", result);
    }

    @Test
    public void testRealCurlPostJsonScenario() {
        JContext context = new JContext();
        context.put("name", "John Doe");
        context.put("age", 25);
        context.put("city", "Beijing");
        context.put("roles", Arrays.asList("admin", "user"));

        String content = "curl -X POST http://localhost:8080/api/users/search \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '{\n" +
                "<trim prefix=\"{\" suffix=\"}\" prefixOverrides=\",\">\n" +
                "<if test=\"name != null\">\"name\": \"#{name}\",\n</if>\n" +
                "<if test=\"age != null\">\"age\": #{age},\n</if>\n" +
                "<if test=\"city != null\">\"city\": \"#{city}\",\n</if>\n" +
                "<if test=\"roles != null &amp;&amp; roles.size() > 0\">\n" +
                "\"roles\": [\n" +
                "<foreach collection=\"roles\" item=\"role\" separator=\",\">\n" +
                "\"#{role}\"\n" +
                "</foreach>\n" +
                "]\n" +
                "</if>\n" +
                "</trim>\n" +
                "}'";

        String result = JQuickEvaluateProcessor.parse(content, context);
        assertTrue(result.contains("\"name\": \"John Doe\""));
        assertTrue(result.contains("\"age\": 25"));
        assertTrue(result.contains("\"city\": \"Beijing\""));
        assertTrue(result.contains("\"roles\": [\"admin\",\"user\"]"));
    }

    @Test
    public void testRealCurlBatchScenario() {
        JContext context = new JContext();
        context.put("operation", "batchCreate");

        List<Map<String, Object>> userList = new ArrayList<>();
        Map<String, Object> user1 = new HashMap<>();
        user1.put("name", "User1");
        user1.put("email", "user1@example.com");
        userList.add(user1);
        Map<String, Object> user2 = new HashMap<>();
        user2.put("name", "User2");
        user2.put("email", "user2@example.com");
        userList.add(user2);
        context.put("userList", userList);

        String content = "curl -X POST http://localhost:8080/api/users/batch \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '{\n" +
                "\"operation\": \"#{operation}\",\n" +
                "\"users\": [\n" +
                "<foreach collection=\"userList\" item=\"user\" separator=\",\">\n" +
                "{\"name\": \"#{user.name}\", \"email\": \"#{user.email}\"}\n" +
                "</foreach>\n" +
                "]\n" +
                "}'";

        String result = JQuickEvaluateProcessor.parse(content, context);
        assertTrue(result.contains("\"operation\": \"batchCreate\""));
        assertTrue(result.contains("\"name\": \"User1\""));
        assertTrue(result.contains("\"name\": \"User2\""));
        assertTrue(result.contains("\"email\": \"user1@example.com\""));
        assertTrue(result.contains("\"email\": \"user2@example.com\""));
    }

    @Test
    public void testRealCurlUploadScenario() {
        JContext context = new JContext();
        context.put("userId", 123);
        context.put("filePath", "/path/to/file.txt");

        String content = "curl -X POST http://localhost:8080/api/users/upload \\\n" +
                "-F \"userId=#{userId}\" \\\n" +
                "<if test=\"filePath != null\">-F \"file=@#{filePath}\" \\</if>\n" +
                "-F \"timestamp=$(date +%s)\"";

        String result = JQuickEvaluateProcessor.parse(content, context);
        assertTrue(result.contains("userId=123"));
        assertTrue(result.contains("file=@/path/to/file.txt"));
        assertTrue(result.contains("timestamp=$(date +%s)"));
    }

    @Test
    public void testRealCurlUpdateScenario() {
        JContext context = new JContext();
        context.put("id", 100);
        context.put("name", "Updated Name");
        context.put("email", "updated@example.com");

        String content = "curl -X PUT http://localhost:8080/api/users/#{id} \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '{\n" +
                "<set>\n" +
                "<if test=\"name != null\">\"name\": \"#{name}\",\n</if>\n" +
                "<if test=\"email != null\">\"email\": \"#{email}\"\n</if>\n" +
                "</set>\n" +
                "}'";

        String result = JQuickEvaluateProcessor.parse(content, context);
        assertTrue(result.contains("http://localhost:8080/api/users/100"));
        assertTrue(result.contains("\"name\": \"Updated Name\""));
        assertTrue(result.contains("\"email\": \"updated@example.com\""));
    }

    // ==================== 边界条件测试 ====================

    @Test
    public void testNullContext() {
        String content = "curl -X GET http://localhost:8080/api/users";
        String result = JQuickEvaluateProcessor.parse(content, null);
        assertEquals(content, result);
    }

    @Test
    public void testSpecialCharactersInVariables() {
        JContext context = new JContext();
        context.put("message", "Hello \"World\"");

        String content = "curl -X POST http://localhost:8080/api/message -d '#{message}'";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X POST http://localhost:8080/api/message -d 'Hello \"World\"'", result);
    }

    @Test
    public void testMultipleDynamicTagsInOneLine() {
        JContext context = new JContext();
        context.put("name", "John");
        context.put("age", 25);

        String content = "curl -X GET http://localhost:8080/api/users<if test=\"name != null\">/name/#{name}</if><if test=\"age != null\">/age/#{age}</if>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users/name/John/age/25", result);
    }

    @Test
    public void testWhitespaceHandling() {
        JContext context = new JContext();
        context.put("name", "John");

        String content = "curl -X GET http://localhost:8080/api/users\n" +
                "<if test=\"name != null\">\n  /named\n</if>";
        String result = JQuickEvaluateProcessor.parse(content, context);
        assertEquals("curl -X GET http://localhost:8080/api/users\n  /named\n", result);
    }

    // ==================== 运行所有测试 ====================

    public static void main(String[] args) {
        JQuickXmlDynamicParserTest test = new JQuickXmlDynamicParserTest();

        System.out.println("==================== 基础变量替换测试 ====================");
        test.testPlainText();
        test.testVariableSubstitution();
        test.testMultipleVariableSubstitution();
        test.testOgnlVariableSubstitution();
        test.testVariableNotFound();
        test.testEmptyContent();

        System.out.println("\n==================== if 标签测试 ====================");
        test.testIfTagTrue();
        test.testIfTagFalse();
        test.testIfTagStringEquals();
        test.testIfTagStringNotEquals();
        test.testIfTagNullCheck();
        test.testIfTagNotNullCheck();
        test.testIfTagComplexOgnl();
        test.testIfTagBooleanTrue();
        test.testIfTagBooleanFalse();
        test.testIfElseSimulation();

        System.out.println("\n==================== foreach 标签测试 ====================");
        test.testForeachTagBasic();
        test.testForeachTagWithOpenClose();
        test.testForeachTagStringList();
        test.testForeachTagArray();
        test.testForeachTagWithIndex();
        test.testForeachTagEmptyCollection();
        test.testForeachTagNullCollection();

        System.out.println("\n==================== choose-when-otherwise 标签测试 ====================");
        test.testChooseWhenOtherwise();
        test.testChooseWhenFirstMatch();
        test.testChooseOtherwiseNoMatch();
        test.testChooseNoOtherwise();

        System.out.println("\n==================== where 标签测试 ====================");
        test.testWhereTag();
        test.testWhereTagSingleCondition();
        test.testWhereTagNoCondition();
        test.testWhereTagRemovesLeadingAnd();

        System.out.println("\n==================== set 标签测试 ====================");
        test.testSetTag();
        test.testSetTagRemovesTrailingComma();
        test.testSetTagAllConditionsFalse();

        System.out.println("\n==================== trim 标签测试 ====================");
        test.testTrimTagBasic();
        test.testTrimTagPrefixOverrides();
        test.testTrimTagSuffixOverrides();

        System.out.println("\n==================== 嵌套标签测试 ====================");
        test.testNestedIfAndForeach();
        test.testNestedForeachAndIf();
        test.testComplexNestedChooseAndForeach();
        test.testDeepNestedTags();

        System.out.println("\n==================== 真实场景测试 ====================");
        test.testRealCurlGetScenario();
        test.testRealCurlPostJsonScenario();
        test.testRealCurlBatchScenario();
        test.testRealCurlUploadScenario();
        test.testRealCurlUpdateScenario();

        System.out.println("\n==================== 边界条件测试 ====================");
        test.testNullContext();
        test.testSpecialCharactersInVariables();
        test.testMultipleDynamicTagsInOneLine();
        test.testWhitespaceHandling();

    }
}