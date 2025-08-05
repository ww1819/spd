package com.spd.common.utils.uuid;

import com.github.f4b6a3.uuid.UuidCreator;

/**
 * UUID生成工具类
 * 使用UuidCreator.getTimeOrdered()生成36位的有序UUID
 * 
 * @author spd
 */
public class UuidGenerator {
    
    /**
     * 生成36位的有序UUID
     * 使用UuidCreator.getTimeOrdered()方法
     * 
     * @return 36位的有序UUID字符串
     */
    public static String generateTimeOrderedUuid() {
        return UuidCreator.getTimeOrdered().toString();
    }
    
    /**
     * 生成36位的有序UUID（不带连字符）
     * 
     * @return 32位的有序UUID字符串
     */
    public static String generateTimeOrderedUuidWithoutHyphens() {
        return UuidCreator.getTimeOrdered().toString().replace("-", "");
    }
    
    /**
     * 批量生成有序UUID
     * 
     * @param count 生成数量
     * @return UUID字符串数组
     */
    public static String[] generateBatchTimeOrderedUuid(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
        
        String[] uuids = new String[count];
        for (int i = 0; i < count; i++) {
            uuids[i] = generateTimeOrderedUuid();
        }
        return uuids;
    }
    
    /**
     * 批量生成有序UUID（不带连字符）
     * 
     * @param count 生成数量
     * @return UUID字符串数组
     */
    public static String[] generateBatchTimeOrderedUuidWithoutHyphens(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
        
        String[] uuids = new String[count];
        for (int i = 0; i < count; i++) {
            uuids[i] = generateTimeOrderedUuidWithoutHyphens();
        }
        return uuids;
    }
    
    /**
     * 验证UUID字符串是否有效
     * 
     * @param uuid UUID字符串
     * @return 是否有效
     */
    public static boolean isValidUuid(String uuid) {
        if (uuid == null) {
            return false;
        }
        
        // 检查36位格式（带连字符）
        if (uuid.length() == 36) {
            return uuid.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        }
        
        // 检查32位格式（不带连字符）
        if (uuid.length() == 32) {
            return uuid.matches("^[0-9a-f]{32}$");
        }
        
        return false;
    }
} 