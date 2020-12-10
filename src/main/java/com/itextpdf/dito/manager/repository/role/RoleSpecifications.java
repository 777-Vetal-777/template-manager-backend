package com.itextpdf.dito.manager.repository.role;

import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.RoleType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Join;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

public class RoleSpecifications {
    private static final String LIKE_WILDCARD = "%%%s%%";

    public static Specification<RoleEntity> nameIsLike(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(root.get("name"), format(LIKE_WILDCARD, name));
    }

    public static Specification<RoleEntity> typeIs(RoleType type) {
        return (Specification<RoleEntity>) (root, query, criteriaBuilder) -> type == null ? null : criteriaBuilder.equal(root.join("type").get("name"), type);
    }

    public static Specification<RoleEntity> usersIn(List<String> users) {
        return (root, query, criteriaBuilder) -> {
            Join<Object, Object> userJoin = root.join("users");
            return CollectionUtils.isEmpty(users)
                    ? null
                    : users.stream()
                    .map(u -> criteriaBuilder.like(userJoin.get("email"), format(LIKE_WILDCARD, u)))
                    .reduce(criteriaBuilder::or).orElse(null);
        };
    }

    public static Specification<RoleEntity> search(String search) {
        return nameIsLike(search).or(usersIn(Collections.singletonList(search)));
    }
}
