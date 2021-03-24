package com.itextpdf.dito.manager.repository.datasample;

import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DataSampleFileRepository extends JpaRepository<DataSampleFileEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("version", "modifiedBy", "modifiedOn", "comment", "stage");

    String SELECT_CLAUSE = "select file.version as version, CONCAT(max(author.first_name), ' ',max(author.last_name)) as modifiedBy, max(file.created_on) as modifiedOn, " +
            "max(file.comment) as comment, max(stages.name) as stage " +
            ", LOWER(CONCAT(max(author.first_name), ' ',max(author.last_name))) as lower_modifiedBy, LOWER(max(file.comment)) as lower_comment, LOWER(max(stages.name)) as lower_stage " +
            "from {h-schema}data_sample_file file " +
            "join {h-schema}data_sample dataSample on dataSample.id = file.data_sample_id " +
            "join {h-schema}data_collection dataCollection on dataSample.data_collection_id = dataCollection.id " +
            "join {h-schema}data_collection_file latestVersion on latestVersion.id = (SELECT file.id FROM {h-schema}data_collection_file file WHERE file.data_collection_id = dataCollection.id and file.version=(select max(file.version) from {h-schema}data_collection_file file where file.data_collection_id = dataCollection.id)) " +
            "left join {h-schema}template_file templateFiles on templateFiles.data_collection_file_id = latestVersion.id " +
            "left join {h-schema}template_file_instance on template_file_instance.template_file_id = templateFiles.id " +
            "left join {h-schema}instance instance on instance.id = template_file_instance.instance_id " +
            "left join {h-schema}stage stages on stages.id = instance.stage_id " +
            "join {h-schema}user author on author.id = file.author_id " +
            "where dataSample.name = :name and ";

    String FILTER_CONDITION = "(:version = '' or file.version = CAST(:version as bigint)) " +
            "and (:createdBy='' or  LOWER(CONCAT(author.first_name, ' ',author.last_name)) like CONCAT('%',:createdBy,'%')) " +
            "and (cast(:startDate as date) is null or file.created_on between cast(:startDate as date) and cast(:endDate as date)) " +
            "and (:comment='' or LOWER(file.comment) like CONCAT('%',:comment,'%')) " +
            "and (COALESCE(:stages) is null or LOWER(stages.name) in (:stages)) ";

    String SEARCH_CONDITION = "and ( CAST(file.version as text) like CONCAT('%',:search,'%') " +
            "or LOWER(file.comment) like CONCAT('%',:search,'%') " +
            "or LOWER(CONCAT(author.first_name, ' ', author.last_name)) like LOWER(CONCAT('%',:search,'%')) " +
            "or LOWER(CAST(CAST(file.created_on as date) as text)) like CONCAT('%',:search,'%') " +
            "or LOWER(stages.name) like CONCAT('%',:search,'%') )";

    String GROUP_BY_VERSION = "group by file.version";

    @Query(value = SELECT_CLAUSE + FILTER_CONDITION + GROUP_BY_VERSION, nativeQuery = true)
    Page<FileVersionModel> filter(Pageable pageable,
                                  @Param("name") @Nullable String name,
                                  @Param("version") @Nullable String version,
                                  @Param("createdBy") @Nullable String createdBy,
                                  @Param("startDate") @Nullable @Temporal Date startDate,
                                  @Param("endDate") @Nullable @Temporal Date endDate,
                                  @Param("stages") @Nullable List<String> stage,
                                  @Param("comment") @Nullable String comment);

    @Query(value = SELECT_CLAUSE + FILTER_CONDITION + SEARCH_CONDITION + GROUP_BY_VERSION, nativeQuery = true)
    Page<FileVersionModel> search(Pageable pageable,
                                  @Param("name") @Nullable String name,
                                  @Param("version") @Nullable String version,
                                  @Param("createdBy") @Nullable String createdBy,
                                  @Param("startDate") @Nullable @Temporal Date startDate,
                                  @Param("endDate") @Nullable @Temporal Date endDate,
                                  @Param("stages") @Nullable List<String> stage,
                                  @Param("comment") @Nullable String comment,
                                  @Param("search") @Nullable String search);
}