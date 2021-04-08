package com.itextpdf.dito.manager.repository.datacollections;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionModel;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionRoleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface DataCollectionRepository extends JpaRepository<DataCollectionEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("name", "type", "modifiedOn", "modifiedBy");

    String SELECT_CLAUSE = "select dc from DataCollectionEntity dc "
            + "join dc.lastDataCollectionLog lastLog "
            + "where ";

    String FILTER_CONDITION = "(:name='' or LOWER(dc.name) like CONCAT('%',:name,'%')) "
            + "and (:modifiedBy='' or LOWER(CONCAT(lastLog.author.firstName, ' ',lastLog.author.lastName)) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or dc.modifiedOn between cast(:startDate as date) and cast(:endDate as date)) "
            + "and (COALESCE(:types) is null or dc.type in (:types))";

    String SEARCH_CONDITION = "and (LOWER(dc.name) like LOWER(CONCAT('%',:search,'%')) "
            + "or LOWER(CONCAT(lastLog.author.firstName, ' ', lastLog.author.lastName)) like LOWER(CONCAT('%',:search,'%'))"
            + "or LOWER(dc.type) like LOWER(CONCAT('%',:search,'%'))) "
            + "or CAST(CAST(dc.modifiedOn as date) as string) like CONCAT('%',:search,'%')";

    Optional<DataCollectionEntity> findByName(String name);

    Boolean existsByName(String name);

    @Query(value = SELECT_CLAUSE + FILTER_CONDITION)
    Page<DataCollectionEntity> filter(Pageable pageable,
                                      @Param("name") @Nullable String name,
                                      @Param("modifiedBy") @Nullable String modifiedBy,
                                      @Param("startDate") @Nullable @Temporal Date modificationStartDate,
                                      @Param("endDate") @Nullable @Temporal Date modificationEndDate,
                                      @Param("types") @Nullable List<DataCollectionType> types);

    @Query(value = SELECT_CLAUSE + FILTER_CONDITION + SEARCH_CONDITION)
    Page<DataCollectionEntity> search(Pageable pageable,
                                      @Param("name") @Nullable String name,
                                      @Param("modifiedBy") @Nullable String modifiedBy,
                                      @Param("startDate") @Nullable @Temporal Date modificationStartDate,
                                      @Param("endDate") @Nullable @Temporal Date modificationEndDate,
                                      @Param("types") @Nullable List<DataCollectionType> types,
                                      @Param("search") String searchParam);

    @Query(value = SELECT_CLAUSE + FILTER_CONDITION)
    List<DataCollectionEntity> filter(@Param("name") @Nullable String name,
                                      @Param("modifiedBy") @Nullable String modifiedBy,
                                      @Param("startDate") @Nullable @Temporal Date modificationStartDate,
                                      @Param("endDate") @Nullable @Temporal Date modificationEndDate,
                                      @Param("types") @Nullable List<DataCollectionType> types);

    @Query(value = SELECT_CLAUSE + FILTER_CONDITION + SEARCH_CONDITION)
    List<DataCollectionEntity> search(@Param("name") @Nullable String name,
                                      @Param("modifiedBy") @Nullable String modifiedBy,
                                      @Param("startDate") @Nullable @Temporal Date modificationStartDate,
                                      @Param("endDate") @Nullable @Temporal Date modificationEndDate,
                                      @Param("types") @Nullable List<DataCollectionType> types,
                                      @Param("search") String searchParam);

    @Query("select max(CAST(SUBSTR(name, LENGTH(:pattern) + 2, LENGTH(name) - LENGTH(:pattern) - 2 ) as int)) from DataCollectionEntity where name like CONCAT(:pattern, '(%)')")
    Optional<Integer> findMaxIntegerByNamePattern(@Param("pattern") String pattern);

    @Query("select dc.id as id, dc.name as dataName, dc.type as type, dc.description as description, dc.author.firstName as authorFirstName, dc.author.lastName as authorLastName, dc.createdOn as createdOn, dc.modifiedOn as modifiedOn, CONCAT(dc.lastDataCollectionLog.author.firstName, ' ', dc.lastDataCollectionLog.author.lastName) as modifiedBy from DataCollectionEntity dc" +
            " join dc.lastDataCollectionLog lastLog" +
            " where (:name='' or LOWER(dc.name) like CONCAT('%',:name,'%'))" +
            " and (:modifiedBy='' or LOWER(CONCAT(lastLog.author.firstName, ' ',lastLog.author.lastName)) like CONCAT('%',:modifiedBy,'%'))" +
            " and (cast(:startDate as date) is null or dc.modifiedOn between cast(:startDate as date) and cast(:endDate as date))" +
            " and (COALESCE(:types) is null or dc.type in (:types))")
    Page<DataCollectionModel> filter(Pageable pageable,
                                     @Param("name") @Nullable String name,
                                     @Param("startDate") @Nullable @Temporal Date modificationStartDate,
                                     @Param("endDate") @Nullable @Temporal Date modificationEndDate,
                                     @Param("modifiedBy") @Nullable String modifiedBy,
                                     @Param("types") @Nullable List<DataCollectionType> types);

    @Query("select dc.id as id, dc.name as dataName, dc.type as type, dc.description as description, dc.author.firstName as authorFirstName, dc.author.lastName as authorLastName, dc.createdOn as createdOn, dc.modifiedOn as modifiedOn, CONCAT(dc.latestVersion.author.firstName, ' ', dc.latestVersion.author.lastName) as modifiedBy from DataCollectionEntity dc" +
            " join dc.lastDataCollectionLog lastLog" +
            " where (:name='' or LOWER(dc.name) like CONCAT('%',:name,'%'))" +
            " and (:modifiedBy='' or LOWER(CONCAT(lastLog.author.firstName, ' ',lastLog.author.lastName)) like CONCAT('%',:modifiedBy,'%'))" +
            " and (cast(:startDate as date) is null or dc.modifiedOn between cast(:startDate as date) and cast(:endDate as date))" +
            " and (COALESCE(:types) is null or dc.type in (:types))" +
            " and (LOWER(dc.name) like LOWER(CONCAT('%',:search,'%')) "
            + " or LOWER(CONCAT(lastLog.author.firstName, ' ', lastLog.author.lastName)) like LOWER(CONCAT('%',:search,'%'))"
            + " or LOWER(dc.type) like LOWER(CONCAT('%',:search,'%'))) "
            + " or CAST(CAST(dc.modifiedOn as date) as string) like CONCAT('%',:search,'%')")
    Page<DataCollectionModel> search(Pageable pageable,
                                     @Param("name") @Nullable String name,
                                     @Param("startDate") @Nullable @Temporal Date modificationStartDate,
                                     @Param("endDate") @Nullable @Temporal Date modificationEndDate,
                                     @Param("modifiedBy") @Nullable String modifiedBy,
                                     @Param("types") @Nullable List<DataCollectionType> types,
                                     @Param("search") @Nullable String search);


    @Query(value = "select id, type, dataCollectionId, roleName," +
            " E6_US34_EDIT_DATA_COLLECTION_METADATA," +
            " E6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON," +
            " E6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION," +
            " E6_US38_DELETE_DATA_COLLECTION," +
            " E7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE," +
            " E7_US47_EDIT_SAMPLE_METADATA," +
            " E7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE," +
            " E7_US50_DELETE_DATA_SAMPLE" +
            " from (select r.id as id, r.type as type, max(data_collection.id) as dataCollectionId, max(r.name) as roleName," +
            " max(case when p.name = 'E6_US34_EDIT_DATA_COLLECTION_METADATA' then 'true' else 'false' end) as E6_US34_EDIT_DATA_COLLECTION_METADATA," +
            " max(case when p.name = 'E6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON' then 'true' else 'false' end) as E6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON," +
            " max(case when p.name = 'E6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION' then 'true' else 'false' end) as E6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION," +
            " max(case when p.name = 'E6_US38_DELETE_DATA_COLLECTION' then 'true' else 'false' end) as E6_US38_DELETE_DATA_COLLECTION," +
            " max(case when p.name = 'E7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE' then 'true' else 'false' end) as E7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE," +
            " max(case when p.name = 'E7_US47_EDIT_SAMPLE_METADATA' then 'true' else 'false' end) as E7_US47_EDIT_SAMPLE_METADATA," +
            " max(case when p.name = 'E7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE' then 'true' else 'false' end) as E7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE," +
            " max(case when p.name = 'E7_US50_DELETE_DATA_SAMPLE' then 'true' else 'false' end) as E7_US50_DELETE_DATA_SAMPLE" +
            " from {h-schema}data_collection" +
            " join {h-schema}data_collection_role dcr on data_collection.id = dcr.data_collection_id" +
            " join {h-schema}role r on r.id = dcr.role_id and r.master = false" +
            " left join {h-schema}role_permission rp on rp.role_id = r.id" +
            " left join {h-schema}permission p on p.id = rp.permission_id group by r.id) as rolesTable  where  dataCollectionId in (:listId)", nativeQuery = true)
    List<DataCollectionRoleModel> getListRoleWithPermissions(@Param("listId") List<Long> listId);

    List<DataCollectionEntity> findByUuidNull();

    Optional<DataCollectionEntity> findByUuid(String uuid);

}
