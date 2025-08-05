package com.spd.common.utils.uuid;

import com.github.f4b6a3.uuid.UuidCreator;

/**
 * UUID7 实现类
 * 使用UuidCreator.getTimeOrdered()生成36位有序的UUID
 * 
 * @author spd
 */
public class UUID7 {
    
    /**
     * 生成36位有序的UUID
     * 使用UuidCreator.getTimeOrdered()方法
     * 
     * @return 36位有序的UUID字符串
     */
    public static String generateUUID7() {
        return UuidCreator.getTimeOrdered().toString();
    }
    
    /**
     * 生成36位有序的UUID（不带连字符）
     * 
     * @return 32位有序的UUID字符串
     */
    public static String generateUUID7Simple() {
        return UuidCreator.getTimeOrdered().toString().replace("-", "");
    }
    
    /**
     * 批量生成UUID7
     * 
     * @param count 生成数量
     * @return UUID7字符串数组
     */
    public static String[] generateBatchUUID7(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
        
        String[] uuids = new String[count];
        for (int i = 0; i < count; i++) {
            uuids[i] = generateUUID7();
        }
        return uuids;
    }
    
    /**
     * 批量生成UUID7（不带连字符）
     * 
     * @param count 生成数量
     * @return UUID7字符串数组
     */
    public static String[] generateBatchUUID7Simple(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
        
        String[] uuids = new String[count];
        for (int i = 0; i < count; i++) {
            uuids[i] = generateUUID7Simple();
        }
        return uuids;
    }
} 