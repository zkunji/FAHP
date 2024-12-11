package com.example.ahp.aop;

import com.google.gson.Gson;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;

@Component
@Aspect
public class InsertAop {
    public final Logger userInsertLogger = LoggerFactory.getLogger("userInsert");
    public final Logger adminInsertLogger = LoggerFactory.getLogger("adminInsert");

    /**
     *@Author keith
     *@Date 2024/4/1 14:36
     *@Description 新增方法切入点
     */
    @Pointcut("execution(* com..controller.*Controller.insert*(..))")
    public void insert(){}

    /**
     *@Author keith
     *@Date 2024/4/1 14:36
     *@Description 新增方法的前置处理
     */
    @Before("insert()")
    public void beforeInsert(JoinPoint jp){
        // 获取HttpServletRequest对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //获得请求路径
        StringBuffer url = request.getRequestURL();
        // 从HttpServletRequest对象中获取请求头信息
        String uid = request.getHeader("uid");
        String adminId = request.getHeader("adminId");
        // 在这里可以根据请求头信息进行判断或执行其他逻辑
        if (uid != null) {
            userBeforeInsert(Long.parseLong(uid),url.toString(),jp);
        } else if (adminId != null) {
            adminBeforeInsert(Long.parseLong(adminId),url.toString(),jp);
        }
    }

    /**
     *@Author keith
     *@Date 2024/4/1 14:36
     *@Description
     * 当方法成功执行并返回才会执行，如果方法出现异常，则不执行
     * 可以获取到目标方法的返回值
     */
    @AfterReturning(returning = "result", pointcut = "insert()")
    public void afterReturningInsert(JoinPoint jp,Object result){
        // 获取HttpServletRequest对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //获得请求路径
        StringBuffer url = request.getRequestURL();
        // 从HttpServletRequest对象中获取请求头信息
        String uid = request.getHeader("uid");
        String adminId = request.getHeader("adminId");
        // 在这里可以根据请求头信息进行判断或执行其他逻辑
        if (uid != null) {
            userAfterReturningInsert(Long.parseLong(uid),url.toString(),jp,result);
        } else if (adminId != null) {
            adminAfterReturningInsert(Long.parseLong(adminId),url.toString(),jp,result);
        }
    }

    /**
     *@Author keith
     *@Date 2024/4/1 18:52
     *@Description 在方法抛出异常之后执行
     */
    @AfterThrowing(throwing = "e", pointcut = "insert()")
    public void afterInsert(JoinPoint jp,Exception e){
        // 获取HttpServletRequest对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //获得请求路径
        StringBuffer url = request.getRequestURL();
        // 从HttpServletRequest对象中获取请求头信息
        String uid = request.getHeader("uid");
        String adminId = request.getHeader("adminId");
        // 在这里可以根据请求头信息进行判断或执行其他逻辑
        if (uid != null) {
            userAfterThrowingInsert(Long.parseLong(uid),url.toString(),jp,e);
        } else if (adminId != null) {
            adminAfterThrowingInsert(Long.parseLong(adminId),url.toString(),jp,e);
        }
    }

    /**
     *@Author keith
     *@Date 2024/4/1 14:37
     *@Description 专家新增方法的前置处理
     */
    private void userBeforeInsert(long uid, String url,JoinPoint jp) {
        String userInvoke = getUserInsertString(uid, url, jp);
        userInsertLogger.info(userInvoke + "\n" + "\n");
    }

    /**
     *@Author keith
     *@Date 2024/4/1 18:57
     *@Description 管理员新增方法的前置处理
     */
    private void adminBeforeInsert(long adminId, String url,JoinPoint jp) {
        String adminInvoke = getAdminInsertString(adminId, url, jp);
        adminInsertLogger.info(adminInvoke + "\n" + "\n");
    }

    /**
     *@Author keith
     *@Date 2024/4/1 14:37
     *@Description 专家新增方法的后置处理(新增方法成功执行)
     */
    private void userAfterReturningInsert(long uid, String url,JoinPoint jp,Object result) {
        String userInvoke = getUserInsertString(uid, url, jp);
        // 你可以根据实际情况对返回值进行操作，比如记录日志或者进行其他后续处理
        userInsertLogger.info(userInvoke + "\n"+"获得的返回值是:{}", new Gson().toJson(result) + "\n" + "\n");
    }

    /**
     *@Author keith
     *@Date 2024/4/1 19:03
     *@Description 管理员新增方法的后置处理(新增方法成功执行)
     */
    private void adminAfterReturningInsert(long adminId, String url,JoinPoint jp,Object result) {
        String adminInvoke = getAdminInsertString(adminId, url, jp);
        // 你可以根据实际情况对返回值进行操作，比如记录日志或者进行其他后续处理
        adminInsertLogger.info(adminInvoke + "\n"+"获得的返回值是:{}", new Gson().toJson(result) + "\n" + "\n");
    }

    /**
     *@Author keith
     *@Date 2024/4/1 18:55
     *@Description 专家新增方法的异常处理
     */
    private void userAfterThrowingInsert(long uid, String url,JoinPoint jp, Exception e) {
        String userInvoke = getUserInsertString(uid, url, jp);
        userInsertLogger.error(userInvoke + "\n" + "发生异常:{}",e.toString() + "\n" + "\n");
    }

    /**
     *@Author keith
     *@Date 2024/4/1 19:03
     *@Description 管理员新增方法的异常处理
     */
    private void adminAfterThrowingInsert(long adminId, String url,JoinPoint jp, Exception e) {
        String adminInvoke = getAdminInsertString(adminId, url, jp);
        adminInsertLogger.error(adminInvoke + "\n" + "发生异常:{}",e.toString() + "\n" + "\n");
    }

    /**
     *@Author keith
     *@Date 2024/4/1 15:23
     *@Description 用户调用了x方法字符串
     */
    public String getUserInsertString(long uid, String url,JoinPoint jp){
        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        // 获取方法名
        String methodName = methodSignature.getName();
        // 获取方法参数
        StringBuilder methodArgs = new StringBuilder();
        Object[] args = jp.getArgs();
        // 获取参数类型和名字
        Parameter[] parameters = methodSignature.getMethod().getParameters();
        for (int i = 0; i < args.length; i++) {
            String parameterName = parameters[i].getName();
            String parameterType = parameters[i].getType().getName();
            methodArgs.append("Parameter:" + i + ",Name:" + parameterName + ",Type:" + parameterType + ",Value:" + new Gson().toJson(args[i]) + ";");
        }
        methodArgs.insert(0,"{");
        methodArgs.append("}");
        String userInvoke = "用户:" + uid + " 使用了方法:" + methodName + " 请求路径是:" + url + " 请求的参数是:" + methodArgs;
        return userInvoke;
    }

    /**
     *@Author keith
     *@Date 2024/4/1 18:58
     *@Description 管理员调用了x方法字符串
     */
    public String getAdminInsertString(long adminId, String url,JoinPoint jp){
        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        // 获取方法名
        String methodName = methodSignature.getName();
        // 获取方法参数
        StringBuilder methodArgs = new StringBuilder();
        Object[] args = jp.getArgs();
        // 获取参数类型和名字
        Parameter[] parameters = methodSignature.getMethod().getParameters();
        for (int i = 0; i < args.length; i++) {
            String parameterName = parameters[i].getName();
            String parameterType = parameters[i].getType().getName();
            methodArgs.append("Parameter:" + i + ",Name:" + parameterName + ",Type:" + parameterType + ",Value:" + new Gson().toJson(args[i]) + ";");
        }
        methodArgs.insert(0,"{");
        methodArgs.append("}");
        String adminInvoke = "管理员:" + adminId + " 使用了方法:" + methodName + " 请求路径是:" + url + " 请求的参数是:" + methodArgs;
        return adminInvoke;
    }
}
