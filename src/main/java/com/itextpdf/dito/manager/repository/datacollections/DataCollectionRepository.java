package com.itextpdf.dito.manager.repository.datacollections;

import com.itextpdf.dito.manager.entity.DataCollectionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface DataCollectionRepository extends JpaRepository<DataCollectionEntity, Long> {
    DataCollectionEntity findByName(String name);

    @Query(value = "select dc from DataCollectionEntity dc "
            + "where dc.name like '%'||:value||'%' "
            + "or dc.type like '%'||:value||'%' "
            + "or dc.author.email like '%'||:value||'%'")
    Page<DataCollectionEntity> search(Pageable pageable, @Param("value") String search);
}
