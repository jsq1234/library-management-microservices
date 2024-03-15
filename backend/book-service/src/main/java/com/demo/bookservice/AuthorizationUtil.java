package com.demo.bookservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthorizationUtil {
    static void userCheck(String role){
        if(role == null || (!role.equals("user") && !role.equals("admin"))){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access.");
        }
    }

    static void adminCheck(String role){
        if(role == null || !role.equals("admin")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access.");
        }
    }
}
