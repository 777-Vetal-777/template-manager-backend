package com.itextpdf.dito.manager.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Arrays;

@Entity
@Table(name = "license")
public class LicenseEntity {
		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "license_gen")
	    @SequenceGenerator(name = "license_gen", sequenceName = "license_sequence", allocationSize = 1)
	    private Long id;
	    
	    @OneToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "workspace_id")
	    private WorkspaceEntity workspace;  
	    @Column(name="file_name")
	    private String fileName; 
	    private byte[] data;
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
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public byte[] getData() {
			return data;
		}
		public void setData(byte[] data) {
			this.data = data;
		}

	@Override
	public String toString() {
		return "LicenseEntity{" +
				"id=" + id +
				", fileName='" + fileName + '\'' +
				'}';
	}
}
