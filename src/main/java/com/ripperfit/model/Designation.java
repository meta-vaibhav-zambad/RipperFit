package com.ripperfit.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="designation")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Designation implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="designation_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int designationId;
	
	@Column(name="designation_name")
	private String designationName;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="parent_designation_id")
	private Designation designation;
	
	/**
	 * @return the designationId
	 */
	public int getDesignationId() {
		return designationId;
	}

	/**
	 * @param designationId the designationId to set
	 */
	public void setDesignationId(int designationId) {
		this.designationId = designationId;
	}

	/**
	 * @return the designationName
	 */
	public String getDesignationName() {
		return designationName;
	}

	/**
	 * @param designationName the designationName to set
	 */
	public void setDesignationName(String designationName) {
		this.designationName = designationName;
	}

	/**
	 * @return the designation
	 */
	public Designation getDesignation() {
		return designation;
	}

	/**
	 * @param designation the designation to set
	 */
	public void setDesignation(Designation designation) {
		this.designation = designation;
	}
}