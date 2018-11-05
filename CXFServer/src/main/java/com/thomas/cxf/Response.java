package com.thomas.cxf;

public class Response {
    private String code;
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Response(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Response() {
    }
}
