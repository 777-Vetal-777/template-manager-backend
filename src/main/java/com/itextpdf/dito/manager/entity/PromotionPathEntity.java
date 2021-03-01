package com.itextpdf.dito.manager.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;

@Entity(name = "promotion_path")
public class PromotionPathEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "promotion_path_gen")
    @SequenceGenerator(name = "promotion_path_gen", sequenceName = "promotion_path_sequence", allocationSize = 1)
    private Long id;
    @OneToOne
    @JoinColumn(name = "workspace_id", referencedColumnName = "id")
    private WorkspaceEntity workspace;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "promotionPath", orphanRemoval = true)
    @OrderBy("sequenceOrder")
    private List<StageEntity> stages = new ArrayList<>();

    public void addStage(final StageEntity stageEntity) {
        stageEntity.setPromotionPath(this);
        stages.add(stageEntity);
    }

    public void addStages(final List<StageEntity> stageEntities) {
        for (final StageEntity stageEntity : stageEntities) {
            addStage(stageEntity);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkspaceEntity getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceEntity workspace) {
        this.workspace = workspace;
    }

    public List<StageEntity> getStages() {
        return stages;
    }

    public void setStages(List<StageEntity> stages) {
        this.stages = stages;
    }

    @Override
    public String toString() {
        return "PromotionPathEntity{" +
                "id=" + id +
                '}';
    }
}
