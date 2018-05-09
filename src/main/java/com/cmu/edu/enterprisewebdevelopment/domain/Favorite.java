package com.cmu.edu.enterprisewebdevelopment.domain;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "favoriteId")
    private List<Blog> blogList;


    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "userId")
    private User user;


    public Favorite (User user) {
        this.blogList = new ArrayList<>();
        this.user = user;
    }

}
