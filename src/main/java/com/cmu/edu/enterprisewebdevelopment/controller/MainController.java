package com.cmu.edu.enterprisewebdevelopment.controller;

import com.cmu.edu.enterprisewebdevelopment.controller.Form.AddFavoriteForm;
import com.cmu.edu.enterprisewebdevelopment.controller.Form.AddTagsForm;
import com.cmu.edu.enterprisewebdevelopment.domain.*;
import com.cmu.edu.enterprisewebdevelopment.service.BlogService;
import com.cmu.edu.enterprisewebdevelopment.service.FavoriteService;
import com.cmu.edu.enterprisewebdevelopment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.text.Bidi;
import java.util.*;

@Controller
@RequestMapping("/main")
public class MainController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @Autowired
    private FavoriteService favoriteService;

    private static final int TAG_COUNT = 10;

    private static final int RELATED_COUNT = 10;


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

    //get All Contents
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String listAllBlogs(Model model) {
        List<Blog> blogList = blogService.listBlogs();

        //sort by totalCount
        Collections.sort(blogList, (Blog b1, Blog b2) -> {
            int b1Score = b1.getTagCount() + b1.getFavoriteCount() + b1.getVoteCount();
            int b2Score = b2.getTagCount() + b2.getFavoriteCount() + b2.getVoteCount();
            return b2Score - b1Score;
        });

        model.addAttribute("blogList", blogList);
        return "index";
    }

    //get One Content + related Content + judege whether this content belongs to the user
    @RequestMapping(value = "/blog/{blogId}", method = RequestMethod.GET)
    public String findOneBlog(@PathVariable("blogId") long blogId, Model model, Principal principal) {
        Blog currentBlog = blogService.findBlogById(blogId);
        //judge whether this content belongs to the user
        boolean ifContains = false;
        if (principal != null) {
            String userName = principal.getName();
            User user = userService.findUserByUserName(userName);
            List<Blog> userBlogs = blogService.listBlogByUser(user);
            if (userBlogs.contains(currentBlog)) {
                ifContains = true;
            }
        }
        //sort the tags
        List<Tag> tagList = currentBlog.getTagList();
        Queue<Tag> pq = new PriorityQueue<>((Tag o1, Tag o2) -> {
            if (o2.getCount() != o1.getCount()) {
                return o2.getCount() - o1.getCount();
            } else {
                return o1.getTagName().compareTo(o2.getTagName());
            }
        });
        pq.addAll(tagList);
        List<Tag> orderTagList = new ArrayList<>();
        int totalTagCount = TAG_COUNT;
        while (!pq.isEmpty() && totalTagCount > 0) {
            orderTagList.add(pq.poll());
            totalTagCount--;
        }
        currentBlog.setTagList(orderTagList);

        //get related blogs has the same tag
        List<Blog> blogs = blogService.listBlogs();
        List<Blog> res = new ArrayList<>();
        Map<Blog, Integer> tagCountMap = new HashMap<>();
        for (Tag tag : tagList) {
            for (Blog tempBlog : blogs) {
                if (!tempBlog.equals(currentBlog)) {
                    List<Tag> tempTagList = tempBlog.getTagList();
                    if (tempTagList.contains(tag)) {
                        //add tagCount into consideration
                        tagCountMap.put(tempBlog, tagCountMap.getOrDefault(tempBlog, 0) + tag.getCount());
                    }
                }
            }
        }
        //update everyTime
        for (Blog blog : tagCountMap.keySet()) {
            int count = tagCountMap.get(blog);
            blog.setTagSize(count);
            res.add(blog);
        }
        Queue<Blog> blogPq = new PriorityQueue<>((Blog b1, Blog b2) -> b2.getTagSize() - b1.getTagSize());
        blogPq.addAll(res);
        int totalRelatedBlogCount = RELATED_COUNT;
        List<Blog> orderRes = new ArrayList<>();
        while (!blogPq.isEmpty() && totalRelatedBlogCount > 0) {
            orderRes.add(blogPq.poll());
            totalRelatedBlogCount--;
        }

        model.addAttribute("ifContains", ifContains);
        model.addAttribute("blog", currentBlog);
        model.addAttribute("relatedContent", orderRes);
        AddFavoriteForm addFavoriteForm = new AddFavoriteForm();
        addFavoriteForm.setBlogId(blogId);
        model.addAttribute("addFavoriteForm",addFavoriteForm);
        model.addAttribute("addTagsForm", new AddTagsForm());
        model.addAttribute("voteExists", false);
        model.addAttribute("tagExists", false);

        return "blog";
    }

    //add to favorite + judge whether the user has already added this blog to his favorite
    @RequestMapping(value = "/blog/{blogId}/addFavorite", method = RequestMethod.POST)
    public String addFavorite(@ModelAttribute @Valid AddFavoriteForm addFavoriteForm,
                              BindingResult result, Principal principal,
                              Model model) {
        if (principal == null) {
            return "login";
        }
        long blogId = addFavoriteForm.getBlogId();
        //update the Blog
        Blog blog = blogService.findBlogById(blogId);
        //find User
        String userName = principal.getName();
        User user = userService.findUserByUserName(userName);
        //find Favorite
        Favorite favorite = favoriteService.findFavoriteByUser(user);
        List<Blog> blogList = favorite.getBlogList();
        //Judge whether the user has already added this blog to his favorite
        if (blogList.contains(blog)) {

            List<Tag> tagList = blog.getTagList();
            Queue<Tag> pq = new PriorityQueue<>((Tag o1, Tag o2) -> {
                if (o2.getCount() != o1.getCount()) {
                    return o2.getCount() - o1.getCount();
                } else {
                    return o1.getTagName().compareTo(o2.getTagName());
                }
            });
            pq.addAll(tagList);
            List<Tag> orderTagList = new ArrayList<>();
            int totalTagCount = TAG_COUNT;
            while (!pq.isEmpty() && totalTagCount > 0) {
                orderTagList.add(pq.poll());
                totalTagCount--;
            }
            blog.setTagList(orderTagList);


            //get related blogs has similar tags
            List<Blog> blogs = blogService.listBlogs();
            List<Blog> res = new ArrayList<>();
            Map<Blog, Integer> tagCountMap = new HashMap<>();
            for (Tag tag : tagList) {
                for (Blog tempBlog : blogs) {
                    if (!tempBlog.equals(blog)) {
                        List<Tag> tempTagList = tempBlog.getTagList();
                        if (tempTagList.contains(tag)) {
                            //add tagCount into consideration
                            tagCountMap.put(tempBlog, tagCountMap.getOrDefault(tempBlog, 0) + tag.getCount());
                        }
                    }
                }
            }
            //update everyTime
            for (Blog tempblog : tagCountMap.keySet()) {
                int count = tagCountMap.get(tempblog);
                tempblog.setTagSize(count);
                res.add(tempblog);
            }
            Queue<Blog> blogPq = new PriorityQueue<>((Blog b1, Blog b2) -> b2.getTagSize() - b1.getTagSize());
            blogPq.addAll(res);
            int totalRelatedBlogCount = RELATED_COUNT;
            List<Blog> orderRes = new ArrayList<>();
            while (!blogPq.isEmpty() && totalRelatedBlogCount > 0) {
                orderRes.add(blogPq.poll());
                totalRelatedBlogCount--;
            }

            model.addAttribute("blog", blog);
            model.addAttribute("ifContains", false);
            model.addAttribute("relatedContent", orderRes);
            model.addAttribute("addTagsForm", new AddTagsForm());
            model.addAttribute("voteExists", false);
            model.addAttribute("tagExists", false);
            result.reject("error.favorite.duplicate", "this content has already been added to your favorite");
            return "blog";
        } else {
            blogList.add(blog);
        }
        //update the favorite & blog
        blog.setFavoriteCount(blog.getFavoriteCount() + 1);
        favorite.setBlogList(blogList);
        favoriteService.updateFavorite(favorite);
        return "redirect:/main/blog/" + blogId;
    }

    //add Votes + judge whether the user has already vote for the content
    @RequestMapping(value = "/blog/{blogId}/addVote", method = RequestMethod.POST)
    public String addVote(@PathVariable("blogId") long blogId,
                          Principal principal,
                          Model model) {
        if (principal == null) {
            return "login";
        }
        Blog blog = blogService.findBlogById(blogId);
        String userName = principal.getName();
        User user = userService.findUserByUserName(userName);
        Vote vote = new Vote(user);
        List<Vote> voteList = blog.getVoteList();
        for (Vote tempVote : voteList) {
            if (tempVote.getUser().getId().equals(user.getId())) {
                //this user has already vote for this content
                model.addAttribute("voteExists", true);

                List<Tag> tagList = blog.getTagList();
                Queue<Tag> pq = new PriorityQueue<>((Tag o1, Tag o2) -> {
                    if (o2.getCount() != o1.getCount()) {
                        return o2.getCount() - o1.getCount();
                    } else {
                        return o1.getTagName().compareTo(o2.getTagName());
                    }
                });
                pq.addAll(tagList);
                List<Tag> orderTagList = new ArrayList<>();
                int totalTagCount = TAG_COUNT;
                while (!pq.isEmpty() && totalTagCount > 0) {
                    orderTagList.add(pq.poll());
                    totalTagCount--;
                }
                blog.setTagList(orderTagList);


                //get related blogs has similar tags
                List<Blog> blogs = blogService.listBlogs();
                List<Blog> res = new ArrayList<>();
                Map<Blog, Integer> tagCountMap = new HashMap<>();
                for (Tag tag : tagList) {
                    for (Blog tempBlog : blogs) {
                        if (!tempBlog.equals(blog)) {
                            List<Tag> tempTagList = tempBlog.getTagList();
                            if (tempTagList.contains(tag)) {
                                //add tagCount into consideration
                                tagCountMap.put(tempBlog, tagCountMap.getOrDefault(tempBlog, 0) + tag.getCount());
                            }
                        }
                    }
                }
                //update everyTime
                for (Blog tempblog : tagCountMap.keySet()) {
                    int count = tagCountMap.get(tempblog);
                    tempblog.setTagSize(count);
                    res.add(tempblog);
                }
                Queue<Blog> blogPq = new PriorityQueue<>((Blog b1, Blog b2) -> b2.getTagSize() - b1.getTagSize());
                blogPq.addAll(res);
                int totalRelatedBlogCount = RELATED_COUNT;
                List<Blog> orderRes = new ArrayList<>();
                while (!blogPq.isEmpty() && totalRelatedBlogCount > 0) {
                    orderRes.add(blogPq.poll());
                    totalRelatedBlogCount--;
                }

                model.addAttribute("blog", blog);
                model.addAttribute("ifContains", false);
                model.addAttribute("relatedContent", orderRes);
                model.addAttribute("addTagsForm", new AddTagsForm());
                AddFavoriteForm addFavoriteForm = new AddFavoriteForm();
                addFavoriteForm.setBlogId(blogId);
                model.addAttribute("addFavoriteForm",addFavoriteForm);
                model.addAttribute("tagExists", false);
                return "blog";
            }
        }
        voteList.add(vote);
        blog.setVoteCount(blog.getVoteCount() + 1);
        blogService.updateBlog(blog);
        return "redirect:/main/blog/" + blogId;
    }

    //add Tags &  judge whether the content has this tag
    @RequestMapping(value = "/blog/{blogId}/addTags", method = RequestMethod.POST)
    public String addTags(@ModelAttribute @Valid AddTagsForm addTagsForm,
                          BindingResult result,
                          Principal principal,
                          @PathVariable("blogId") long blogId,
                          Model model) {
        if (principal == null) {
            return "login";
        }
        String userName = principal.getName();
        String tagName = addTagsForm.getTagName();
        Blog blog = blogService.findBlogById(blogId);
        //judge whether the content has this tag
        List<Tag> tagList = blog.getTagList();
        for (Tag tag : tagList) {
            if (tag.getTagName().equals(tagName)) {
                //get related blogs has similar tags

                 Queue<Tag> pq = new PriorityQueue<>((Tag o1, Tag o2) -> {
                    if (o2.getCount() != o1.getCount()) {
                        return o2.getCount() - o1.getCount();
                    } else {
                        return o1.getTagName().compareTo(o2.getTagName());
                    }
                });
                pq.addAll(tagList);
                List<Tag> orderTagList = new ArrayList<>();
                int totalTagCount = TAG_COUNT;
                while (!pq.isEmpty() && totalTagCount > 0) {
                    orderTagList.add(pq.poll());
                    totalTagCount--;
                }
                blog.setTagList(orderTagList);


                //get related blogs has similar tags
                List<Blog> blogs = blogService.listBlogs();
                List<Blog> res = new ArrayList<>();
                Map<Blog, Integer> tagCountMap = new HashMap<>();
                for (Tag tempTag : tagList) {
                    for (Blog tempBlog : blogs) {
                        if (!tempBlog.equals(blog)) {
                            List<Tag> tempTagList = tempBlog.getTagList();
                            if (tempTagList.contains(tempTag)) {
                                //add tagCount into consideration
                                tagCountMap.put(tempBlog, tagCountMap.getOrDefault(tempBlog, 0) + tempTag.getCount());
                            }
                        }
                    }
                }
                //update everyTime
                for (Blog tempblog : tagCountMap.keySet()) {
                    int count = tagCountMap.get(tempblog);
                    tempblog.setTagSize(count);
                    res.add(tempblog);
                }
                Queue<Blog> blogPq = new PriorityQueue<>((Blog b1, Blog b2) -> b2.getTagSize() - b1.getTagSize());
                blogPq.addAll(res);
                int totalRelatedBlogCount = RELATED_COUNT;
                List<Blog> orderRes = new ArrayList<>();
                while (!blogPq.isEmpty() && totalRelatedBlogCount > 0) {
                    orderRes.add(blogPq.poll());
                    totalRelatedBlogCount--;
                }


                model.addAttribute("blog", blog);
                model.addAttribute("ifContains", false);
                model.addAttribute("relatedContent", orderRes);
                AddFavoriteForm addFavoriteForm = new AddFavoriteForm();
                addFavoriteForm.setBlogId(blogId);
                model.addAttribute("addFavoriteForm",addFavoriteForm);
                model.addAttribute("voteExists", false);
                model.addAttribute("tagExists", false);
                result.reject("error.tag.exists", "this tags already exists in the content");
                return "blog";
            }
        }
        //add newTag
        Tag newTag = new Tag(tagName);
        newTag.setCount(1);
        User user = userService.findUserByUserName(userName);
        List<User> userList = newTag.getUserList();
        userList.add(user);
        tagList.add(newTag);
        //add the tagCount
        blog.setTagCount(blog.getTagCount() + 1);
        blogService.updateBlog(blog);
        return "redirect:/main/blog/" + blogId;
    }

    //add tag  + judge whether the user has already
    @RequestMapping(value = "/blog/{blogId}/addTagCount", method = RequestMethod.POST)
    public String addTagCount(@RequestParam String tagName,
                              @PathVariable("blogId") long blogId,
                              Model model,
                              Principal principal) {
        String userName = principal.getName();
        User user = userService.findUserByUserName(userName);
        Blog blog = blogService.findBlogById(blogId);
        List<Tag> tagList = blog.getTagList();
        Tag targetTag = new Tag();
        for (Tag tempTag : tagList) {
            if (tempTag.getTagName().equals(tagName)) {
                targetTag = tempTag;
            }
        }
        //judge whether the user has already add this tag
        List<User> userList = targetTag.getUserList();
        for (User tempUser : userList) {
            if (tempUser.getId().equals(user.getId())) {
                model.addAttribute("tagExists", true);

                Queue<Tag> pq = new PriorityQueue<>((Tag o1, Tag o2) -> {
                    if (o2.getCount() != o1.getCount()) {
                        return o2.getCount() - o1.getCount();
                    } else {
                        return o1.getTagName().compareTo(o2.getTagName());
                    }
                });
                pq.addAll(tagList);
                List<Tag> orderTagList = new ArrayList<>();
                int totalTagCount = TAG_COUNT;
                while (!pq.isEmpty() && totalTagCount > 0) {
                    orderTagList.add(pq.poll());
                    totalTagCount--;
                }
                blog.setTagList(orderTagList);

                //get related blogs has similar tags
                List<Blog> blogs = blogService.listBlogs();
                List<Blog> res = new ArrayList<>();
                Map<Blog, Integer> tagCountMap = new HashMap<>();
                for (Tag tempTag : tagList) {
                    for (Blog tempBlog : blogs) {
                        if (!tempBlog.equals(blog)) {
                            List<Tag> tempTagList = tempBlog.getTagList();
                            if (tempTagList.contains(tempTag)) {
                                //add tagCount into consideration
                                tagCountMap.put(tempBlog, tagCountMap.getOrDefault(tempBlog, 0) + tempTag.getCount());
                            }
                        }
                    }
                }
                //update everyTime
                for (Blog tempblog : tagCountMap.keySet()) {
                    int count = tagCountMap.get(tempblog);
                    tempblog.setTagSize(count);
                    res.add(tempblog);
                }
                Queue<Blog> blogPq = new PriorityQueue<>((Blog b1, Blog b2) -> b2.getTagSize() - b1.getTagSize());
                blogPq.addAll(res);
                int totalRelatedBlogCount = RELATED_COUNT;
                List<Blog> orderRes = new ArrayList<>();
                while (!blogPq.isEmpty() && totalRelatedBlogCount > 0) {
                    orderRes.add(blogPq.poll());
                    totalRelatedBlogCount--;
                }


                model.addAttribute("blog", blog);
                model.addAttribute("ifContains", false);
                model.addAttribute("relatedContent", orderRes);
                AddFavoriteForm addFavoriteForm = new AddFavoriteForm();
                addFavoriteForm.setBlogId(blogId);
                model.addAttribute("addFavoriteForm",addFavoriteForm);
                model.addAttribute("voteExists", false);
                model.addAttribute("addTagsForm", new AddTagsForm());
                return "blog";
            }
        }
        userList.add(user);
        targetTag.setUserList(userList);
        targetTag.setCount(targetTag.getCount() + 1);
        blog.setTagCount(blog.getTagCount() + 1);
        blogService.updateBlog(blog);
        return "redirect:/main/blog/" + blogId;
    }
    //search the result
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String search(@RequestParam("query") String query, Model model) {
        List<Blog> blogList = blogService.listBlogByContentLike(query);
        model.addAttribute("blogList", blogList);
        return "index";
    }

}
