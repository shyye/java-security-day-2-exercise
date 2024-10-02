package com.booleanuk.api.payload.responses;

import lombok.Getter;

@Getter
public class Response<T> {
    protected String status;
    protected T data;

    public void set(T data) {
        this.status = "success";
        this.data = data;
    }
}