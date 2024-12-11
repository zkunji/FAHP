package com.example.ahp.config.filter;

import com.example.ahp.common.constant.AhpConstants;
import com.example.ahp.common.constant.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class TokenFilter implements HandlerInterceptor {
    private StringRedisTemplate redisTemplate;

    public TokenFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果是OPTIONS预处理请求，返回允许跨域
        if (request.getMethod().equals("OPTIONS")) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setStatus(HttpServletResponse.SC_OK);
            return false; // 不继续执行后续的拦截器和处理器
        }
        //过滤{"/**/register","/**/login"}请求
        System.out.println("token:"+request.getHeader("token"));
        //获取请求head中的token+(uid||adminId)
        String token = request.getHeader("token");
        String redisTokenKey = "";
        String redisTokenValue = "";
        if(request.getHeader("uid")!=null){
            redisTokenKey = RedisConstants.UidToken + request.getHeader("uid");
        }else if(request.getHeader("adminId")!=null){
            redisTokenKey = RedisConstants.AdminIdToken + request.getHeader("adminId");
        }else{
            return false;
        }
        redisTokenValue = redisTemplate.opsForValue().get(redisTokenKey);
        if(redisTokenValue == null || token == null){
            //token不存在
//            log.error("token 不存在");
//            response.setContentType("application/json;charset=UTF-8");
//            response.setCharacterEncoding("UTF-8");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置状态码为 401
//            PrintWriter out = response.getWriter();
//            out.println("{\"message\":\"Token 过期\"}"); // 返回一个简单的 JSON 响应体
//            out.flush();
//            out.close();
            return false;
        }
        //查询redis，判断token值是否存在且相符
        if(!redisTokenValue.isEmpty() && token.equals(redisTokenValue)){
            //token正确，更新过期时间
            log.info("token验证通过");
            redisTemplate.expire(redisTokenKey, AhpConstants.UidTokenExpire, TimeUnit.HOURS);
            return true;
        }else{
            //token不正确
            log.error("token验证失败");
//            response.setContentType("application/json;charset=UTF-8");
//            response.setCharacterEncoding("UTF-8");
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 设置状态码为 403
//            PrintWriter out = response.getWriter();
//            out.println("{\"message\":\"Token 不符合\"}"); // 返回一个简单的 JSON 响应体
//            out.flush();
//            out.close();
            return false;
        }
    }
}
