package com.sso.login.utils;

import com.sso.login.pojo.User;

import java.util.HashMap;
import java.util.Map;

public class LoginCacheUtil {
    //存储的是token-User
    public static Map<String, User> loginMap = new HashMap<>();
}
