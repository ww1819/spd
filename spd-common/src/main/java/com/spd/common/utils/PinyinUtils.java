package com.spd.common.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音工具类：根据名称生成首字母拼音简码（中文取拼音首字母，英文取首字母大写，数字保留）
 *
 * @author spd
 */
public class PinyinUtils {

    private static final HanyuPinyinOutputFormat FORMAT = new HanyuPinyinOutputFormat();

    static {
        FORMAT.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    /**
     * 根据名称生成首字母拼音简码。
     * 中文：取每个汉字拼音的首字母（大写）；
     * 英文字母：取首字母并转大写，连续字母取首字母；
     * 数字：保留；
     * 其他字符：忽略。
     *
     * @param name 名称（可为中文、英文、数字等）
     * @return 首字母拼音简码，若 name 为空则返回空字符串
     */
    public static String getPinyinInitials(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        name = name.trim();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (c >= 0x4E00 && c <= 0x9FA5) {
                try {
                    String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c, FORMAT);
                    if (pinyins != null && pinyins.length > 0) {
                        sb.append(pinyins[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    // 无法转换时跳过该字符
                }
            } else if (Character.isLetter(c)) {
                sb.append(Character.toUpperCase(c));
            } else if (Character.isDigit(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
