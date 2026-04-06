package com.github.paohaijiao.xml.enums;

public enum JQuickXmlEscapeEnums {

    AND("&", "&amp;"),
    LT("<", "&lt;"),
    GT(">", "&gt;"),
    QUOTE("\"", "&quot;"),
    APOS("'", "&apos;");

    private final String original;
    private final String escaped;

    JQuickXmlEscapeEnums(String original, String escaped) {
        this.original = original;
        this.escaped = escaped;
    }

    public String getOriginal() {
        return original;
    }

    public String getEscaped() {
        return escaped;
    }

    /**
     * 转义单个字符
     */
    public String escape(String expression) {
        return expression.replace(original, escaped);
    }

    /**
     * 反转义
     */
    public String unescape(String expression) {
        return expression.replace(escaped, original);
    }

    /**
     * 检查是否包含原始字符
     */
    public boolean contains(String expression) {
        return expression.contains(original);
    }
}
