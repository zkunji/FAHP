package com.example.ahp.common.result;

import lombok.Data;

@Data
public class ResultInfo {
    private int code;
    private String msg;
    private int total;
    private Object data;

    public static ResultInfo success(int total,Object data) {
        return success(200,"操作成功",total,data);
    }

    public static ResultInfo success(int code, String msg, int total,Object data) {
        ResultInfo result = new ResultInfo();
        result.setCode(code);
        result.setMsg(msg);
        result.setTotal(total);
        result.setData(data);
        return result;
    }

    public static ResultInfo fail(String msg) {
        return fail(400,msg,null);
    }

    public static ResultInfo fail(String msg, Object data) {
        return fail(400,msg,data);
    }

    public static ResultInfo fail(int code, String msg, Object data) {
        ResultInfo result = new ResultInfo();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
