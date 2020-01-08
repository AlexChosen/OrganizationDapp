package com.pingan.chain.domain;


public class ChainResponse<T> {

    private String code= Constant.Y;

    private T body;


    public ChainResponse(){

    }
    public ChainResponse(String code){
        this.code= code;
    }
    public ChainResponse(T body) {
        this.body = body;
    }

    public ChainResponse(String code,T body) {
        this.code= code;
        this.body = body;
    }

    public ChainResponse(String code, T body, String  message) {
        this.code= code;
        this.body = body;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

}