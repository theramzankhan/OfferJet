package com.sparkproject.OfferJet.model;

public class JoinDefinition {

	private String tableName;
	private String joinColumn;
	
	public JoinDefinition() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public JoinDefinition(String tableName, String joinColumn) {
		super();
		this.tableName = tableName;
		this.joinColumn = joinColumn;
	}
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getJoinColumn() {
		return joinColumn;
	}
	public void setJoinColumn(String joinColumn) {
		this.joinColumn = joinColumn;
	}
	
}
