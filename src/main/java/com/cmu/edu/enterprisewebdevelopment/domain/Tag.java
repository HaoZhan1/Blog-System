package com.cmu.edu.enterprisewebdevelopment.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String tagName;

    private int count;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "tagId")
    private List<User> userList;

    public Tag(String tagName) {
        this.userList = new ArrayList<>();
        this.tagName = tagName;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Tag) {
            return this.tagName.equals(((Tag) o).tagName);
        }
        return false;
    }

}
