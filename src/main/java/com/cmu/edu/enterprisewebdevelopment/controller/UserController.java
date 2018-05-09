package com.cmu.edu.enterprisewebdevelopment.controller;


import com.cmu.edu.enterprisewebdevelopment.controller.Form.RegisterForm;
import com.cmu.edu.enterprisewebdevelopment.controller.validation.RegisterFormValidator;
import com.cmu.edu.enterprisewebdevelopment.domain.Favorite;
import com.cmu.edu.enterprisewebdevelopment.domain.User;
import com.cmu.edu.enterprisewebdevelopment.service.FavoriteService;
import com.cmu.edu.enterprisewebdevelopment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class UserController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserService userService;

    @Autowired
    private RegisterFormValidator registerFormValidator;


    //add user into the request
    @ModelAttribute
    public void getUser(Principal principal, Model model) {
        if (principal == null) {
            return;
        }
        //find the User
        User user = userService.findUserByUserName(principal.getName());
        model.addAttribute("user", user);
    }

    //bind the validator with registerForm
    @InitBinder("registerForm")
    public void bindRegisterForm(WebDataBinder binder) {
        binder.setValidator(registerFormValidator);
    }

    //login page
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(Principal principal) {
        //login user cannot login again
        if (principal != null) {
            return "redirect:/home/index";
        }
        return "login";
    }

    //register page
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerPage(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "register";
    }


    //register action
    @RequestMapping(value="/register", method = RequestMethod.POST)
    public String register(@ModelAttribute(value = "registerForm") @Valid RegisterForm registerForm,
                           BindingResult result) {
        //validate errors
        if (result.hasErrors()) {
            return "register";
        }
        //create newUser
        User newUser = new User(registerForm.getUsername(),
                registerForm.getPassword(),
                registerForm.getEmail());
        try {
            userService.createUser(newUser);
        } catch (RuntimeException e) {
            //already has this user
            System.out.println(e.getMessage() + "message ----------------------------");
            String errorCode = e.getMessage();
            result.reject(errorCode, "username already exists");
            return "register";
        }
        //update the favorite
        Favorite favorite = new Favorite(newUser);
        favoriteService.updateFavorite(favorite);
        //return to login
        return "login";
    }


}

