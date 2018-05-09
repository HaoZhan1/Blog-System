package com.cmu.edu.enterprisewebdevelopment.service;

import com.cmu.edu.enterprisewebdevelopment.domain.Blog;
import com.cmu.edu.enterprisewebdevelopment.domain.User;
import com.cmu.edu.enterprisewebdevelopment.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BlogService {

    private String STOP_WORD = "able\n" +
            "about\n" +
            "above\n" +
            "according\n" +
            "accordingly\n" +
            "across\n" +
            "actually\n" +
            "after\n" +
            "afterwards\n" +
            "again\n" +
            "against\n" +
            "ain't\n" +
            "all\n" +
            "allow\n" +
            "allows\n" +
            "almost\n" +
            "alone\n" +
            "along\n" +
            "already\n" +
            "also\n" +
            "although\n" +
            "always\n" +
            "am\n" +
            "among\n" +
            "amongst\n" +
            "an\n" +
            "and\n" +
            "another\n" +
            "any\n" +
            "anybody\n" +
            "anyhow\n" +
            "anyone\n" +
            "anything\n" +
            "anyway\n" +
            "anyways\n" +
            "anywhere\n" +
            "apart\n" +
            "appear\n" +
            "appreciate\n" +
            "appropriate\n" +
            "are\n" +
            "aren't\n" +
            "around\n" +
            "as\n" +
            "a's\n" +
            "aside\n" +
            "ask\n" +
            "asking\n" +
            "associated\n" +
            "at\n" +
            "available\n" +
            "away\n" +
            "awfully\n" +
            "be\n" +
            "became\n" +
            "because\n" +
            "become\n" +
            "becomes\n" +
            "becoming\n" +
            "been\n" +
            "before\n" +
            "beforehand\n" +
            "behind\n" +
            "being\n" +
            "believe\n" +
            "below\n" +
            "beside\n" +
            "besides\n" +
            "best\n" +
            "better\n" +
            "between\n" +
            "beyond\n" +
            "both\n" +
            "brief\n" +
            "but\n" +
            "by\n" +
            "came\n" +
            "can\n" +
            "cannot\n" +
            "cant\n" +
            "can't\n" +
            "cause\n" +
            "causes\n" +
            "certain\n" +
            "certainly\n" +
            "changes\n" +
            "clearly\n" +
            "c'mon\n" +
            "co\n" +
            "com\n" +
            "come\n" +
            "comes\n" +
            "concerning\n" +
            "consequently\n" +
            "consider\n" +
            "considering\n" +
            "contain\n" +
            "containing\n" +
            "contains\n" +
            "corresponding\n" +
            "could\n" +
            "couldn't\n" +
            "course\n" +
            "c's\n" +
            "currently\n" +
            "definitely\n" +
            "described\n" +
            "despite\n" +
            "did\n" +
            "didn't\n" +
            "different\n" +
            "do\n" +
            "does\n" +
            "doesn't\n" +
            "doing\n" +
            "done\n" +
            "don't\n" +
            "down\n" +
            "downwards\n" +
            "during\n" +
            "each\n" +
            "edu\n" +
            "eg\n" +
            "eight\n" +
            "either\n" +
            "else\n" +
            "elsewhere\n" +
            "enough\n" +
            "entirely\n" +
            "especially\n" +
            "et\n" +
            "etc\n" +
            "even\n" +
            "ever\n" +
            "every\n" +
            "everybody\n" +
            "everyone\n" +
            "everything\n" +
            "everywhere\n" +
            "ex\n" +
            "exactly\n" +
            "example\n" +
            "except\n" +
            "far\n" +
            "few\n" +
            "fifth\n" +
            "first\n" +
            "five\n" +
            "followed\n" +
            "following\n" +
            "follows\n" +
            "for\n" +
            "former\n" +
            "formerly\n" +
            "forth\n" +
            "four\n" +
            "from\n" +
            "further\n" +
            "furthermore\n" +
            "get\n" +
            "gets\n" +
            "getting\n" +
            "given\n" +
            "gives\n" +
            "go\n" +
            "goes\n" +
            "going\n" +
            "gone\n" +
            "got\n" +
            "gotten\n" +
            "greetings\n" +
            "had\n" +
            "hadn't\n" +
            "happens\n" +
            "hardly\n" +
            "has\n" +
            "hasn't\n" +
            "have\n" +
            "haven't\n" +
            "having\n" +
            "he\n" +
            "hello\n" +
            "help\n" +
            "hence\n" +
            "her\n" +
            "here\n" +
            "hereafter\n" +
            "hereby\n" +
            "herein\n" +
            "here's\n" +
            "hereupon\n" +
            "hers\n" +
            "herself\n" +
            "he's\n" +
            "hi\n" +
            "him\n" +
            "himself\n" +
            "his\n" +
            "hither\n" +
            "hopefully\n" +
            "how\n" +
            "howbeit\n" +
            "however\n" +
            "i'd\n" +
            "ie\n" +
            "if\n" +
            "ignored\n" +
            "i'll\n" +
            "i'm\n" +
            "immediate\n" +
            "in\n" +
            "inasmuch\n" +
            "inc\n" +
            "indeed\n" +
            "indicate\n" +
            "indicated\n" +
            "indicates\n" +
            "inner\n" +
            "insofar\n" +
            "instead\n" +
            "into\n" +
            "inward\n" +
            "is\n" +
            "isn't\n" +
            "it\n" +
            "it'd\n" +
            "it'll\n" +
            "its\n" +
            "it's\n" +
            "itself\n" +
            "i've\n" +
            "just\n" +
            "keep\n" +
            "keeps\n" +
            "kept\n" +
            "know\n" +
            "known\n" +
            "knows\n" +
            "last\n" +
            "lately\n" +
            "later\n" +
            "latter\n" +
            "latterly\n" +
            "least\n" +
            "less\n" +
            "lest\n" +
            "let\n" +
            "let's\n" +
            "like\n" +
            "liked\n" +
            "likely\n" +
            "little\n" +
            "look\n" +
            "looking\n" +
            "looks\n" +
            "ltd\n" +
            "mainly\n" +
            "many\n" +
            "may\n" +
            "maybe\n" +
            "me\n" +
            "mean\n" +
            "meanwhile\n" +
            "merely\n" +
            "might\n" +
            "more\n" +
            "moreover\n" +
            "most\n" +
            "mostly\n" +
            "much\n" +
            "must\n" +
            "my\n" +
            "myself\n" +
            "name\n" +
            "namely\n" +
            "nd\n" +
            "near\n" +
            "nearly\n" +
            "necessary\n" +
            "need\n" +
            "needs\n" +
            "neither\n" +
            "never\n" +
            "nevertheless\n" +
            "new\n" +
            "next\n" +
            "nine\n" +
            "no\n" +
            "nobody\n" +
            "non\n" +
            "none\n" +
            "noone\n" +
            "nor\n" +
            "normally\n" +
            "not\n" +
            "nothing\n" +
            "novel\n" +
            "now\n" +
            "nowhere\n" +
            "obviously\n" +
            "of\n" +
            "off\n" +
            "often\n" +
            "oh\n" +
            "ok\n" +
            "okay\n" +
            "old\n" +
            "on\n" +
            "once\n" +
            "one\n" +
            "ones\n" +
            "only\n" +
            "onto\n" +
            "or\n" +
            "other\n" +
            "others\n" +
            "otherwise\n" +
            "ought\n" +
            "our\n" +
            "ours\n" +
            "ourselves\n" +
            "out\n" +
            "outside\n" +
            "over\n" +
            "overall\n" +
            "own\n" +
            "particular\n" +
            "particularly\n" +
            "per\n" +
            "perhaps\n" +
            "placed\n" +
            "please\n" +
            "plus\n" +
            "possible\n" +
            "presumably\n" +
            "probably\n" +
            "provides\n" +
            "que\n" +
            "quite\n" +
            "qv\n" +
            "rather\n" +
            "rd\n" +
            "re\n" +
            "really\n" +
            "reasonably\n" +
            "regarding\n" +
            "regardless\n" +
            "regards\n" +
            "relatively\n" +
            "respectively\n" +
            "right\n" +
            "said\n" +
            "same\n" +
            "saw\n" +
            "say\n" +
            "saying\n" +
            "says\n" +
            "second\n" +
            "secondly\n" +
            "see\n" +
            "seeing\n" +
            "seem\n" +
            "seemed\n" +
            "seeming\n" +
            "seems\n" +
            "seen\n" +
            "self\n" +
            "selves\n" +
            "sensible\n" +
            "sent\n" +
            "serious\n" +
            "seriously\n" +
            "seven\n" +
            "several\n" +
            "shall\n" +
            "she\n" +
            "should\n" +
            "shouldn't\n" +
            "since\n" +
            "six\n" +
            "so\n" +
            "some\n" +
            "somebody\n" +
            "somehow\n" +
            "someone\n" +
            "something\n" +
            "sometime\n" +
            "sometimes\n" +
            "somewhat\n" +
            "somewhere\n" +
            "soon\n" +
            "sorry\n" +
            "specified\n" +
            "specify\n" +
            "specifying\n" +
            "still\n" +
            "sub\n" +
            "such\n" +
            "sup\n" +
            "sure\n" +
            "take\n" +
            "taken\n" +
            "tell\n" +
            "tends\n" +
            "th\n" +
            "than\n" +
            "thank\n" +
            "thanks\n" +
            "thanx\n" +
            "that\n" +
            "thats\n" +
            "that's\n" +
            "the\n" +
            "their\n" +
            "theirs\n" +
            "them\n" +
            "themselves\n" +
            "then\n" +
            "thence\n" +
            "there\n" +
            "thereafter\n" +
            "thereby\n" +
            "therefore\n" +
            "therein\n" +
            "theres\n" +
            "there's\n" +
            "thereupon\n" +
            "these\n" +
            "they\n" +
            "they'd\n" +
            "they'll\n" +
            "they're\n" +
            "they've\n" +
            "think\n" +
            "third\n" +
            "this\n" +
            "thorough\n" +
            "thoroughly\n" +
            "those\n" +
            "though\n" +
            "three\n" +
            "through\n" +
            "throughout\n" +
            "thru\n" +
            "thus\n" +
            "to\n" +
            "together\n" +
            "too\n" +
            "took\n" +
            "toward\n" +
            "towards\n" +
            "tried\n" +
            "tries\n" +
            "truly\n" +
            "try\n" +
            "trying\n" +
            "t's\n" +
            "twice\n" +
            "two\n" +
            "un\n" +
            "under\n" +
            "unfortunately\n" +
            "unless\n" +
            "unlikely\n" +
            "until\n" +
            "unto\n" +
            "up\n" +
            "upon\n" +
            "us\n" +
            "use\n" +
            "used\n" +
            "useful\n" +
            "uses\n" +
            "using\n" +
            "usually\n" +
            "value\n" +
            "various\n" +
            "very\n" +
            "via\n" +
            "viz\n" +
            "vs\n" +
            "want\n" +
            "wants\n" +
            "was\n" +
            "wasn't\n" +
            "way\n" +
            "we\n" +
            "we'd\n" +
            "welcome\n" +
            "well\n" +
            "we'll\n" +
            "went\n" +
            "were\n" +
            "we're\n" +
            "weren't\n" +
            "we've\n" +
            "what\n" +
            "whatever\n" +
            "what's\n" +
            "when\n" +
            "whence\n" +
            "whenever\n" +
            "where\n" +
            "whereafter\n" +
            "whereas\n" +
            "whereby\n" +
            "wherein\n" +
            "where's\n" +
            "whereupon\n" +
            "wherever\n" +
            "whether\n" +
            "which\n" +
            "while\n" +
            "whither\n" +
            "who\n" +
            "whoever\n" +
            "whole\n" +
            "whom\n" +
            "who's\n" +
            "whose\n" +
            "why\n" +
            "will\n" +
            "willing\n" +
            "wish\n" +
            "with\n" +
            "within\n" +
            "without\n" +
            "wonder\n" +
            "won't\n" +
            "would\n" +
            "wouldn't\n" +
            "yes\n" +
            "yet\n" +
            "you\n" +
            "you'd\n" +
            "you'll\n" +
            "your\n" +
            "you're\n" +
            "yours\n" +
            "yourself\n" +
            "yourselves\n" +
            "you've\n" +
            "zero\n" +
            "zt\n" +
            "ZT\n" +
            "zz\n" +
            "ZZ";
    @Autowired
    private BlogRepository blogRepository;

    public void updateBlog(Blog blog) {
        blogRepository.save(blog);
    }

    //add content
    public void addBlog(Blog blog) {
        blogRepository.save(blog);
    }


    public List<Blog> listBlogs() {
        return blogRepository.findAll();
    }
    //search by query
    public List<Blog> listBlogByContentLike(String query) {
        //split into tokens
        String[] phrases = query.split("\\s+");
        //delete common words
        String[] stopWords = STOP_WORD.split("\\n");
        Set<String> stopWordsSet = new HashSet<>();
        for (int i = 0; i < stopWords.length; i++) {
            stopWordsSet.add(stopWords[i]);
        }
        List<String> phrasesList = new ArrayList<>();
        for (String phrase : phrases) {
            if (!stopWordsSet.contains(phrase)) {
                phrasesList.add(phrase);
            }
        }
        //calculate how many tokens each content can match
        Map<Blog, Integer> map = new HashMap<>();
        for (String phrase : phrasesList) {
            List<Blog> blogs = blogRepository.findAllByContentLike("%"+ phrase + "%");
            for (Blog blog : blogs) {
                map.put(blog, map.getOrDefault(blog, 0) + 1);
            }
        }
        List<Blog> result = new ArrayList<>();
        for (Blog blog : map.keySet()) {
            int count = map.get(blog);
            //consider the token frequency percent in the content
            double percentCount = count;
            if (blog.getContent().split("\\s+").length != 0) {
                percentCount = count / blog.getContent().split("\\s+").length;
            }
            //calculate the totalScore combines the tag Count, favorite Count, the vote count, and the match count
            double total_score = scoreHelper(percentCount, blog);
            blog.setTotalScore(total_score);
            result.add(blog);
        }
        //sort by totalScore
        Collections.sort(result, (o1, o2) -> o1.getTotalScore() > o2.getTotalScore() ? -1 : 1);
        return result;
    }

    //list content by user
    public List<Blog> listBlogByUser(User user) {
        return blogRepository.findAllByUser(user);
    }

    //get sepcific blog
    public Blog findBlogById(long blogId) {
        return blogRepository.findBlogById(blogId);
    }

    private double scoreHelper(double count, Blog blog) {
        double result = blog.getTagCount() * 0.2 + blog.getFavoriteCount() * 0.2 + blog.getVoteCount() * 0.1 + count * 0.5;
        return result;
    }
}
