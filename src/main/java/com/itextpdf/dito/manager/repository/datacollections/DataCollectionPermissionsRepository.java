package com.itextpdf.dito.manager.repository.datacollections;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionPermissionsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataCollectionPermissionsRepository extends JpaRepository<DataCollectionEntity, Long> {
    List<String> SUPPORTED_SORT_PERMISSION_FIELDS = List.of("name",
            "E6_US34_EDIT_DATA_COLLECTION_METADATA",
            "E6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON",
            "E6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION",
            "E6_US38_DELETE_DATA_COLLECTION",
            "E7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE",
            "E7_US47_EDIT_SAMPLE_METADATA",
            "E7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE",
            "E7_US50_DELETE_DATA_SAMPLE"
    );

    String PERMISSION_COUNT_CLAUSE = "select count(*) from ";

    String PERMISSION_SELECT_CLAUSE = "select name, type,"
            + " E6_US34_EDIT_DATA_COLLECTION_METADATA, "
            + " E6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON, "
            + " E6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION, "
            + " E6_US38_DELETE_DATA_COLLECTION, "
            + " E7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE, "
            + " E7_US47_EDIT_SAMPLE_METADATA, "
            + " E7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE, "
            + " E7_US50_DELETE_DATA_SAMPLE "
            + "  from ";

    String PERMISSION_TABLE_CLAUSE = "(select r.name, max(r.type) as type,"
            + " max(case when p.name = 'E6_US34_EDIT_DATA_COLLECTION_METADATA' then 'true' else 'false' end) as E6_US34_EDIT_DATA_COLLECTION_METADATA, "
            + " max(case when p.name = 'E6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON' then 'true' else 'false' end) as E6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON, "
            + " max(case when p.name = 'E6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION' then 'true' else 'false' end) as E6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION, "
            + " max(case when p.name = 'E6_US38_DELETE_DATA_COLLECTION' then 'true' else 'false' end) as E6_US38_DELETE_DATA_COLLECTION, "
            + " max(case when p.name = 'E7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE' then 'true' else 'false' end) as E7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE, "
            + " max(case when p.name = 'E7_US47_EDIT_SAMPLE_METADATA' then 'true' else 'false' end) as E7_US47_EDIT_SAMPLE_METADATA, "
            + " max(case when p.name = 'E7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE' then 'true' else 'false' end) as E7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE, "
            + " max(case when p.name = 'E7_US50_DELETE_DATA_SAMPLE' then 'true' else 'false' end) as E7_US50_DELETE_DATA_SAMPLE "
            + "  from {h-schema}data_collection "
            + "  join {h-schema}data_collection_role dcr on data_collection.id = dcr.data_collection_id "
            + "  join {h-schema}role r on r.id = dcr.role_id and r.master = false "
            + "  join {h-schema}role_permission rp on rp.role_id = r.id "
            + "  join {h-schema}permission p on p.id = rp.permission_id "
            + " where data_collection.name = :name "
            + " group by r.name) as rolesTable "
            + "where ";

    String PERMISSION_FILTER_CONDITION = "(COALESCE(:role_names) is null or name in (CAST(:role_names AS text)))"
            + "  and (:editDataCollectionMetadata='' or :editDataCollectionMetadata=E6_US34_EDIT_DATA_COLLECTION_METADATA) "
            + "  and (:createNewVersionOfDataCollection='' or :createNewVersionOfDataCollection=E6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON) "
            + "  and (:rollbackOfTheDataCollection='' or :rollbackOfTheDataCollection=E6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION) "
            + "  and (:deleteDataCollection='' or :deleteDataCollection=E6_US38_DELETE_DATA_COLLECTION) "
            + "  and (:createNewDataSample='' or :createNewDataSample=E7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE) "
            + "  and (:editSampleMetadata='' or :editSampleMetadata=E7_US47_EDIT_SAMPLE_METADATA) "
            + "  and (:createNewVersionOfDataSample='' or :createNewVersionOfDataSample=E7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE) "
            + "  and (:deleteDataSample='' or :deleteDataSample=E7_US50_DELETE_DATA_SAMPLE) ";

    String PERMISSION_SEARCH_CONDITION = " and LOWER(name) like CONCAT('%',:search,'%')";

    @Query(value = PERMISSION_SELECT_CLAUSE + PERMISSION_TABLE_CLAUSE + PERMISSION_FILTER_CONDITION,
            nativeQuery = true)
    Page<DataCollectionPermissionsModel> filterPermissions(Pageable pageable,
                                                           @Param("name") String dataCollectionName,
                                                           @Param("role_names") List<String> roleNames,
                                                           @Param("editDataCollectionMetadata") String editDataCollectionMetadata,
                                                           @Param("createNewVersionOfDataCollection") String createNewVersionOfDataCollection,
                                                           @Param("rollbackOfTheDataCollection") String rollbackOfTheDataCollection,
                                                           @Param("deleteDataCollection") String deleteDataCollection,
                                                           @Param("createNewDataSample") String createNewDataSample,
                                                           @Param("editSampleMetadata") String editSampleMetadata,
                                                           @Param("createNewVersionOfDataSample") String createNewVersionOfDataSample,
                                                           @Param("deleteDataSample") String deleteDataSample);

    @Query(value = PERMISSION_SELECT_CLAUSE + PERMISSION_TABLE_CLAUSE + PERMISSION_FILTER_CONDITION + PERMISSION_SEARCH_CONDITION,
            countQuery = PERMISSION_COUNT_CLAUSE + PERMISSION_TABLE_CLAUSE + PERMISSION_FILTER_CONDITION + PERMISSION_SEARCH_CONDITION,
            nativeQuery = true)
    Page<DataCollectionPermissionsModel> searchPermissions(Pageable pageable,
                                                           @Param("name") String templateName,
                                                           @Param("role_names") List<String> roleNames,
                                                           @Param("editDataCollectionMetadata") String editDataCollectionMetadata,
                                                           @Param("createNewVersionOfDataCollection") String createNewVersionOfDataCollection,
                                                           @Param("rollbackOfTheDataCollection") String rollbackOfTheDataCollection,
                                                           @Param("deleteDataCollection") String deleteDataCollection,
                                                           @Param("createNewDataSample") String createNewDataSample,
                                                           @Param("editSampleMetadata") String editSampleMetadata,
                                                           @Param("createNewVersionOfDataSample") String createNewVersionOfDataSample,
                                                           @Param("deleteDataSample") String deleteDataSample,
                                                           @Param("search") @Nullable String search);

}
