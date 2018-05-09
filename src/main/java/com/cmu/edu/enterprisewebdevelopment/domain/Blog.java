package com.cmu.edu.enterprisewebdevelopment.domain;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String content;

    private int voteCount;

    private int favoriteCount;

    private int tagCount;

    @Transient
    private double totalScore;

    @Transient
    private int tagSize;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "blogId")
    private List<Tag> tagList;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "blogId")
    private List<Vote> voteList;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name="userId")
    private User user;


    public Blog (String content) {
        this.content = content;
        this.tagList = new ArrayList<>();
        this.voteList = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof  Blog) {
            return this.getId() == ((Blog) o).getId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        String hashValue = this.getId()+"";
        return hashValue.hashCode();
    }
}

