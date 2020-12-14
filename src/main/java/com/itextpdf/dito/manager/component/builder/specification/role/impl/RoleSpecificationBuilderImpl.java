package com.itextpdf.dito.manager.component.builder.specification.role.impl;

import com.itextpdf.dito.manager.component.builder.specification.role.RoleSpecificationBuilder;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.RoleType;
import com.itextpdf.dito.manager.filter.role.RoleFilter;

import java.util.Collections;
import java.util.List;
import javax.persistence.FetchType;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import static java.lang.String.format;

@Component
public class RoleSpecificationBuilderImpl implements RoleSpecificationBuilder {
    private static final String LIKE_WILDCARD = "%%%s%%";

    @Override
    public Specification<RoleEntity> build(final RoleFilter roleFilter, final String searchParam) {
        Specification<RoleEntity> specification = Specification.where(
                nameIsLike(roleFilter.getName())
                        .and(typeIn(roleFilter.getType())));
        if (!StringUtils.isEmpty(searchParam)) {
            specification = specification != null
                    ? specification.and(search(searchParam))
                    : search(searchParam);
        }
        return specification;
    }

    private Specification<RoleEntity> nameIsLike(final String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder
                        .like(criteriaBuilder.lower(root.get("name")), format(LIKE_WILDCARD, name.toLowerCase()));
    }

    private Specification<RoleEntity> typeIn(final List<RoleType> types) {
        return (root, query, criteriaBuilder) -> {
            /*query.distinct(true);
            query.groupBy(root.get("type"));*/
            return CollectionUtils.isEmpty(types)
                    ? null
                    : types.stream()
                            .map(u -> criteriaBuilder.equal(root.get("type").get("name"), u))
                            .reduce(criteriaBuilder::or).orElse(null);
        };

    }

    private Specification<RoleEntity> usersIn(final List<String> users) {
        return (root, query, criteriaBuilder) -> {
            if (CollectionUtils.isEmpty(users)) {
                return null;
            } else {
                final Join<Object, Object> userJoin = root.join("users");
                //query.groupBy(root.get("users"));
                query.distinct(true);
                return users.stream()
                        .map(u -> criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("email")),
                                format(LIKE_WILDCARD, u.toLowerCase())))
                        .reduce(criteriaBuilder::or).orElse(null);
            }
        };
    }

    private Specification<RoleEntity> search(final String search) {
        return nameIsLike(search).or(usersIn(Collections.singletonList(search)));
    }
}
