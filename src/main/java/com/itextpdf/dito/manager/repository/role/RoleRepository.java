package com.itextpdf.dito.manager.repository.role;

import com.itextpdf.dito.manager.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("id", "name", "type");

    Optional<RoleEntity> findByName(String name);

    Page<RoleEntity> findAll(Specification<RoleEntity> specification, Pageable pageable);

    /**
     * @deprecated use {@link RoleSpecifications}.
     */
    @Deprecated
    @Query(value = "select r from RoleEntity r "
            + "join r.users user "
            + "where  LOWER(user.email) like  LOWER(CONCAT('%',:value,'%')) "
            + "or  LOWER(r.name) like  LOWER(CONCAT('%',:value,'%'))")
    Page<RoleEntity> search(Pageable pageable, @Param("value") String searchParam);
}
