package com.cmu.edu.enterprisewebdevelopment.controller.validation;

import com.cmu.edu.enterprisewebdevelopment.controller.Form.RegisterForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class RegisterFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aclass) {
        return RegisterForm.class.equals(aclass);
    }

    //will execute from start to the end
    @Override
    public void validate(Object target, Errors errors) {
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.password.empty");
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "checkPassword", "error.checkPassword.empty");
        RegisterForm registerForm = (RegisterForm)target;
        String password = registerForm.getPassword();
        String checkPassword = registerForm.getCheckPassword();
        if (password != null && checkPassword != null) {
            if (!registerForm.getPassword().equals(registerForm.getCheckPassword())) {
                errors.reject("error.password.mismatch", "password don't match");
            }
        }
    }
}
