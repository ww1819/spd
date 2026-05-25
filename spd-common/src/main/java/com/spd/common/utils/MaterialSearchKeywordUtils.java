package com.spd.common.utils;

/**
 * 耗材检索关键词规范化（前后端口径一致，减少全角空格、不可见字符导致搜不到）。
 */
public final class MaterialSearchKeywordUtils {

    private MaterialSearchKeywordUtils() {
    }

    /**
     * 规范化检索词：trim、全角转半角、连续空白折叠为单个空格。
     */
    public static String normalize(String raw) {
        if (raw == null) {
            return null;
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return null;
        }
        s = toHalfWidth(s);
        s = s.replaceAll("\\s+", " ");
        return s.isEmpty() ? null : s;
    }

    /**
     * 转义 MySQL LIKE 通配符，避免用户输入 %、_ 导致匹配异常。
     */
    public static String escapeLike(String keyword) {
        if (keyword == null) {
            return null;
        }
        return keyword.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    public static String normalizeAndEscapeLike(String raw) {
        String n = normalize(raw);
        return n == null ? null : escapeLike(n);
    }

    private static String toHalfWidth(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\u3000') {
                sb.append(' ');
            } else if (c >= '\uFF01' && c <= '\uFF5E') {
                sb.append((char) (c - 0xFEE0));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
