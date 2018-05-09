package com.cmu.edu.enterprisewebdevelopment.controller;


import com.cmu.edu.enterprisewebdevelopment.domain.Blog;
import com.cmu.edu.enterprisewebdevelopment.domain.Favorite;
import com.cmu.edu.enterprisewebdevelopment.domain.User;
import com.cmu.edu.enterprisewebdevelopment.service.BlogService;
import com.cmu.edu.enterprisewebdevelopment.service.FavoriteService;
import com.cmu.edu.enterprisewebdevelopment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller

@RequestMapping(value = "/home")
public class HomeController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @Autowired
    private FavoriteService favoriteService;


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

    //list own Content Page
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Principal principal, Model model) {
        if (principal == null) {
            return "/login";
        }
        String userName = principal.getName();
        User user = userService.findUserByUserName(userName);
        List<Blog> blogList =  blogService.listBlogByUser(user);

        //sort by totalCount
        Collections.sort(blogList, (Blog b1, Blog b2) -> {
            int b1Score = b1.getTagCount() + b1.getFavoriteCount() + b1.getVoteCount();
            int b2Score = b2.getTagCount() + b2.getFavoriteCount() + b2.getVoteCount();
            return b2Score - b1Score;
        });

        model.addAttribute("blogList", blogList);
        return "dashboard";
    }

    //list Favorite Content Page
    @RequestMapping(value = "/favorite", method = RequestMethod.GET)
    public String favorateBlog(Principal principal, Model model) {
        if (principal == null) {
            return "/login";
        }
        String userName = principal.getName();
        User user = userService.findUserByUserName(userName);
        Favorite favorite = favoriteService.findFavoriteByUser(user);
        List<Blog> blogList = favorite.getBlogList();

        //sort by totalCount
        Collections.sort(blogList, (Blog b1, Blog b2) -> {
            int b1Score = b1.getTagCount() + b1.getFavoriteCount() + b1.getVoteCount();
            int b2Score = b2.getTagCount() + b2.getFavoriteCount() + b2.getVoteCount();
            return b2Score - b1Score;
        });


        model.addAttribute("favoriteBlogList", blogList);
        return "favorite";
    }

    //add Content Page
    @RequestMapping(value = "/addBlog", method = RequestMethod.GET)
    private String addBlogPage(Principal principal) {
        if (principal == null) {
            return "/login";
        }
        return "addBlog";
    }

    //add Content action
    @RequestMapping(value = "/addBlog", method = RequestMethod.POST)
    public String addBlog(@RequestParam("content") String content,
                        Principal principal) {
        if (content == null || content.isEmpty()) {
            System.out.println("content is empty");
            return "redirect:/home/addBlog";
        }
        String userName = principal.getName();
        User user = userService.findUserByUserName(userName);
        Blog blog = new Blog(content);
        blog.setUser(user);
        blogService.addBlog(blog);
        return "redirect:/home/index";
    }

}
