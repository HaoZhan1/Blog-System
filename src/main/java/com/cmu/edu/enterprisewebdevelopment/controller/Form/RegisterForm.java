package com.cmu.edu.enterprisewebdevelopment.controller.Form;

import lombok.Data;

@Data
public class RegisterForm {
    private String username;
    private String password;
    private String checkPassword;
    private String email;
}
