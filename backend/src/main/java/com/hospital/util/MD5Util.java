package com.hospital.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * MD5加密工具类
 */
public class MD5Util {

    /**
     * 生成盐值
     */
    public static String generateSalt() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * MD5加密（不带盐）
     */
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    /**
     * MD5加密（带盐）
     *
     * @param input 原始字符串
     * @param salt  盐值
     */
    public static String md5WithSalt(String input, String salt) {
        return md5(input + salt);
    }

    /**
     * 验证密码是否正确
     *
     * @param rawPassword     原始密码
     * @param salt            盐值
     * @param encodedPassword 加密后的密码
     */
    public static boolean verifyPassword(String rawPassword, String salt, String encodedPassword) {
        String encoded = md5WithSalt(rawPassword, salt);
        return encoded.equals(encodedPassword);
    }

    /**
     * 生成随机密码
     */
    public static String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
