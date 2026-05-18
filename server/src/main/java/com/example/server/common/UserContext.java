package com.example.server.common;

public class UserContext {

    private static final ThreadLocal<Integer> USER_ID = new ThreadLocal<>();

    public static void setUserId(Integer userId) {
        USER_ID.set(userId);
    }

    public static Integer getUserId() {
        return USER_ID.get();
    }

    public static void remove() {
        USER_ID.remove();
    }
}
