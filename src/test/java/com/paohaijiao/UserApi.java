package com.paohaijiao;

import java.util.List;
import java.util.Map;

public interface UserApi {
    List<Map<String, Object>> all();

    Map<String, Object> findByName(String name);

    List<Map<String, Object>> search(Map<String, Object> params);
}
