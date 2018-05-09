package com.cmu.edu.enterprisewebdevelopment.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "userId")
    private User user;

    public Vote(User user) {
        this.user = user;
    }

}
