package com.sso.login.controller;

import com.sso.login.pojo.User;
import com.sso.login.utils.LoginCacheUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static Set<User> dbUsers;

    static {
        dbUsers = new HashSet<>();
        dbUsers.add(new User(0,"zhangsan","12345"));
        dbUsers.add(new User(1,"lisi","123456"));
        dbUsers.add(new User(2,"wangwu","1234567"));
    }

    @PostMapping
    public String doLogin(User user, HttpSession session, HttpServletResponse response){
        String target = (String) session.getAttribute("target");
        //模拟根据username和password从数据库中去查用户
//        Optional<User> first = dbUsers.stream().filter(dbUser -> dbUser.getUsername().equals(user.getUsername()) &&
//                dbUser.getPassword().equals(user.getPassword()))
//                .findFirst();

        User res = null;
        for (User dbUser : dbUsers){
            if(dbUser.getUsername().equals(user.getUsername()) && dbUser.getPassword().equals(user.getPassword())){
                res = dbUser;
            }
        }
        //用户登陆成功，保存(TOKEN,用户)
        if (res != null){
            //保存用户登陆信息
            String token = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("TOKEN",token);
            cookie.setDomain("127.0.0.1");
            response.addCookie(cookie);
            LoginCacheUtil.loginMap.put(token,user);
        } else {
            session.setAttribute("msg","用户名或密码错误");
            return "login";
        }

        //重定向到target地址
        return "redirect:" + target;
    }

    /**
     * 给其他子系统开发一个接口，根据token获取登陆的用户信息
     * @param token
     * @return
     */
    @GetMapping("/info")
    public ResponseEntity<User> getUserInfo(String token){
        if (!StringUtils.isEmpty(token)){
            User user = LoginCacheUtil.loginMap.get(token);
            return ResponseEntity.ok(user);
        }else {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/logout")
    public String logout(@CookieValue(value = "TOKEN")Cookie cookie, @RequestParam("target") String target,
                         HttpSession session,HttpServletResponse response){
        //删除用户的登陆信息
        LoginCacheUtil.loginMap.remove(cookie.getValue());
        //删除session.loginUser
        session.removeAttribute("loginUser");
        //设置cookie过期
        Cookie newCookie = new Cookie("TOKEN",null);
        newCookie.setMaxAge(0);
        newCookie.setPath("/");
        response.addCookie(newCookie);

        return "redirect:"+target;
    }
}
