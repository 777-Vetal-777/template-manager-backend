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
                name == null ? null : criteriaBuilder
                        .like(criteriaBuilder.lower(root.get("name")), format(LIKE_WILDCARD, name.toLowerCase()));
    }

    public static Specification<RoleEntity> typeIn(List<RoleType> types) {
        return (root, query, criteriaBuilder) -> {
            Join<Object, Object> typeJoin = root.join("type");
            query.distinct(true);
            return CollectionUtils.isEmpty(types)
                    ? null
                    : types.stream()
                    .map(u -> criteriaBuilder.equal(typeJoin.get("name"), u))
                    .reduce(criteriaBuilder::or).orElse(null);
        };

    }

    public static Specification<RoleEntity> usersIn(List<String> users) {
        return (root, query, criteriaBuilder) -> {
            if (CollectionUtils.isEmpty(users)) {
                return null;
            } else {
                Join<Object, Object> userJoin = root.join("users");
                query.distinct(true);
                return users.stream()
                        .map(u -> criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("email")), format(LIKE_WILDCARD, u.toLowerCase())))
                        .reduce(criteriaBuilder::or).orElse(null);
            }
        };
    }

    public static Specification<RoleEntity> search(String search) {
        return nameIsLike(search).or(usersIn(Collections.singletonList(search)));
    }
}
