package com.sso.login.controller;

import com.sso.login.pojo.User;
import com.sso.login.utils.LoginCacheUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

/**
 * 页面跳转逻辑
 */
@Controller
@RequestMapping("/view")
public class ViewController {

    /**
     * 跳转到登陆页面，设置重定向的地址，可以携带cookie-TOKEN，如果有cookie，不用跳转到登陆页面，直接重定向
     * @return
     */
    @GetMapping("/login")
    public String toLogin(@RequestParam(required = false, defaultValue = "") String target,
                          HttpSession session, @CookieValue(required = false, value = "TOKEN") Cookie cookie){

        //如果传入参数为空，则默认跳转到首页
        if (StringUtils.isEmpty(target)){
            target = "http://127.0.0.1:9010";
        }
        if (cookie != null){
            //如果已经登陆的用户登陆系统时，直接重定向到target
            String value = cookie.getValue();
            User user = LoginCacheUtil.loginMap.get(value);
            if (user != null){
                //这里不能有空格
                return "redirect:" + target;
            }
        }
        //TODO：要做target地址是否合法的检验
        //重定向地址，将地址保存起来
        session.setAttribute("target",target);
        return "login";
    }
}
