package com.itextpdf.dito.manager.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.SequenceGenerator;

@Entity(name = "stage")
public class StageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "stage_gen")
    @SequenceGenerator(name = "stage_gen", sequenceName = "stage_sequence", allocationSize = 1)
    private Long id;
    private String name;
    @Column(name = "sequence_order")
    private Integer sequenceOrder;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_path_id", referencedColumnName = "id")
    private PromotionPathEntity promotionPath;
    @OneToMany(mappedBy = "stage", cascade = CascadeType.MERGE)
    private List<InstanceEntity> instances = new ArrayList<>();

    public void addInstance(final InstanceEntity instanceEntity) {
        instanceEntity.setStage(this);
        instances.add(instanceEntity);
    }

    @PreRemove
    public void onPreRemove() {
        for (final InstanceEntity instanceEntity : instances) {
            instanceEntity.setStage(null);
        }
        instances.clear();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(Integer sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public PromotionPathEntity getPromotionPath() {
        return promotionPath;
    }

    public void setPromotionPath(PromotionPathEntity promotionPath) {
        this.promotionPath = promotionPath;
    }

    public List<InstanceEntity> getInstances() {
        return instances;
    }

    public void setInstances(List<InstanceEntity> instances) {
        this.instances = instances;
    }
}
