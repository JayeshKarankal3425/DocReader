package com.spring.DocReader.dto;


import lombok.*;

import java.time.LocalDateTime;

@Builder
public class ApiResponse<T>{
    private Boolean success;
    private String message;
    private T data ;
    private LocalDateTime timeStamp;

    public ApiResponse() {
    }

    public ApiResponse(Boolean success, String message, T data, LocalDateTime timeStamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timeStamp = timeStamp;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }
}
