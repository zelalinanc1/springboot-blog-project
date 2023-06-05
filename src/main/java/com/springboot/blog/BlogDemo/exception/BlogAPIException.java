package com.springboot.blog.BlogDemo.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

@Builder
@Getter
@Setter
public class BlogAPIException extends RuntimeException{
    private HttpStatus status;
    private String message;

    public BlogAPIException(HttpStatus status,String message){
        super(message);
        this.status = status;
        this.message = message;
    }


    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
