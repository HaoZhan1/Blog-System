package com.cmu.edu.enterprisewebdevelopment.service;

import com.cmu.edu.enterprisewebdevelopment.domain.Favorite;
import com.cmu.edu.enterprisewebdevelopment.domain.User;
import com.cmu.edu.enterprisewebdevelopment.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    //find favorite
    public Favorite findFavoriteByUser(User user) {
        return favoriteRepository.findByUser(user);
    }

    //update favorite
    public void updateFavorite(Favorite favorite) {
        favoriteRepository.save(favorite);
    }
}
