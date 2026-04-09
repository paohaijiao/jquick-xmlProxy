package com.github.paohaijiao.xml.wrapper;

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.spi.ServiceLoader;
import com.github.paohaijiao.xml.wrapper.data.JQuickXmlWrapperData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class WrapperManager {

    public static JConsole console=new JConsole();

    private static volatile List<Wrapper> wrappers;

    private static final Map<Class<?>, Wrapper> WRAPPER_CACHE = new ConcurrentHashMap<>();

    private static final Object LOCK = new Object();

    static {
        loadWrappers();
    }

    private static void loadWrappers() {
        synchronized (LOCK) {
            wrappers = ServiceLoader.loadServicesByPriority(Wrapper.class);
            ServiceLoader.printServicePriorities(Wrapper.class);
        }
    }

    /**
     * 包装数据
     * @param wrapperData 包装数据对象
     * @return 包装后的值，如果数据为空则返回 null
     */
    public static Object wrap(JQuickXmlWrapperData wrapperData) {
        if (wrapperData == null || wrapperData.getData() == null) {
            return null;
        }
        if (wrappers == null || wrappers.isEmpty()) {
            return wrapperData.getData();
        }
        Object originalData = wrapperData.getData();
        Class<?> valueClass = originalData.getClass();
        Wrapper cachedWrapper = WRAPPER_CACHE.get(valueClass);
        if (cachedWrapper != null && cachedWrapper.support(wrapperData)) {
            return cachedWrapper.wrap(wrapperData);
        }
        for (Wrapper wrapper : wrappers) {
            try {
                if (wrapper.support(wrapperData)) {
                    WRAPPER_CACHE.put(valueClass, wrapper);
                    return wrapper.wrap(wrapperData);
                }
            } catch (Exception e) {
                console.error("Wrapper support check failed for: " + wrapper.getClass().getName() + ", error: " + e.getMessage());
            }
        }
        return originalData;
    }

    /**
     * 包装数据并指定特定的 Wrapper
     * @param wrapperData 包装数据对象
     * @param wrapperClass 指定的 Wrapper 类
     * @return 包装后的值
     */
    public static Object wrapWithSpecific(JQuickXmlWrapperData wrapperData, Class<? extends Wrapper> wrapperClass) {
        if (wrapperData == null || wrapperData.getData() == null) {
            return null;
        }
        Optional<? extends Wrapper> specificWrapper = getWrapper(wrapperClass);
        if (specificWrapper.isPresent()) {
            Wrapper wrapper = specificWrapper.get();
            if (wrapper.support(wrapperData)) {
                return wrapper.wrap(wrapperData);
            }
        }
        return wrapperData.getData();
    }

    /**
     * 批量包装多个数据
     * @param wrapperDataList 包装数据列表
     * @return 包装后的值列表
     */
    public static List<Object> wrapBatch(List<JQuickXmlWrapperData> wrapperDataList) {
        if (wrapperDataList == null || wrapperDataList.isEmpty()) {
            return new ArrayList<>();
        }
        return wrapperDataList.stream().map(WrapperManager::wrap).collect(Collectors.toList());
    }

    /**
     * 检查是否存在支持该数据的 Wrapper
     * @param wrapperData 包装数据对象
     * @return 是否存在支持的 Wrapper
     */
    public static boolean isSupported(JQuickXmlWrapperData wrapperData) {
        if (wrapperData == null || wrapperData.getData() == null) {
            return false;
        }
        Class<?> valueClass = wrapperData.getData().getClass();
        Wrapper cachedWrapper = WRAPPER_CACHE.get(valueClass);
        if (cachedWrapper != null) {
            return cachedWrapper.support(wrapperData);
        }
        for (Wrapper wrapper : wrappers) {
            if (wrapper.support(wrapperData)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取支持该数据的 Wrapper（如果有）
     * @param wrapperData 包装数据对象
     * @return Optional 包装的 Wrapper
     */
    public static Optional<Wrapper> getSupportedWrapper(JQuickXmlWrapperData wrapperData) {
        if (wrapperData == null || wrapperData.getData() == null) {
            return Optional.empty();
        }
        Class<?> valueClass = wrapperData.getData().getClass();
        Wrapper cachedWrapper = WRAPPER_CACHE.get(valueClass);
        if (cachedWrapper != null && cachedWrapper.support(wrapperData)) {
            return Optional.of(cachedWrapper);
        }
        for (Wrapper wrapper : wrappers) {
            if (wrapper.support(wrapperData)) {
                WRAPPER_CACHE.put(valueClass, wrapper);
                return Optional.of(wrapper);
            }
        }
        return Optional.empty();
    }

    /**
     * 获取指定类型的 Wrapper
     */
    @SuppressWarnings("unchecked")
    public static <T extends Wrapper> Optional<T> getWrapper(Class<T> wrapperClass) {
        if (wrappers == null || wrappers.isEmpty()) {
            return Optional.empty();
        }
        return wrappers.stream()
                .filter(w -> wrapperClass.isAssignableFrom(w.getClass()))
                .map(w -> (T) w)
                .findFirst();
    }

    /**
     * 重新加载 Wrapper（用于动态添加）
     */
    public static void reload() {
        synchronized (LOCK) {
            ServiceLoader.reload(Wrapper.class);
            WRAPPER_CACHE.clear();
            loadWrappers();
        }
    }

    /**
     * 获取所有 Wrapper 的优先级信息
     */
    public static void printWrapperInfo() {
        if (wrappers == null || wrappers.isEmpty()) {
            console.info("No wrappers loaded.");
            return;
        }
        ServiceLoader.printServicePriorities(Wrapper.class);
    }

    /**
     * 清空缓存
     */
    public static void clearCache() {
        WRAPPER_CACHE.clear();
    }

    /**
     * 获取已加载的 Wrapper 数量
     */
    public static int getWrapperCount() {
        return wrappers == null ? 0 : wrappers.size();
    }

    /**
     * 获取缓存大小
     */
    public static int getCacheSize() {
        return WRAPPER_CACHE.size();
    }
}