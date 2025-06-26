package com.linkedIn.backend.features.search.service;

import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import jakarta.persistence.EntityManager;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {
    private final EntityManager entityManager;

    public SearchService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<AuthenticationUser> searchUsers(String query) {
        SearchSession searchSession = Search.session(entityManager);

        return searchSession.search(AuthenticationUser.class)
                .where(f -> f.match()
                        .fields("firstName", "lastName", "position", "company")
                        .matching(query)
                        .fuzzy(2)
                )
                .fetchAllHits();
    }
}
