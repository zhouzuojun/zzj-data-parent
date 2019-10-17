package com.zzj.data.params;

import org.apache.commons.lang.StringUtils;

public class TableObj {

	/**
	 * 数据实例名+表名
	 */
	private String ownerAndObjectName;

	/**
	 * 用户、实例、索引等数据库里的主体名称
	 */
	private String userDbName;


	/**
	 * mpp表所对应的dbuser
	 */
	private String schemaname;

	/**
	 * 表、视图、type（ES里的）等数据库里的表对象名称
	 */
	private String tableName;

	/**
	 * 表对象的类型，编码参照字典表DR_BUSINESS_DICT(业务数据字典表)，关联TYPE_CODE=RESOURCE_TABLE_TYPE。
	 */
	private String type;

	/**
	 * 注解
	 */
	private String comment;

	public String getUserDbName() {
		return userDbName;
	}

	public void setUserDbName(String userDbName) {
		this.userDbName = userDbName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOwnerAndObjectName() {
		if(StringUtils.isEmpty(ownerAndObjectName)){
			return userDbName+tableName;
		}
		return ownerAndObjectName;
	}

	public void setOwnerAndObjectName(String ownerAndObjectName) {
		this.ownerAndObjectName = ownerAndObjectName;
	}

	/**
	 * getter 方法
	 * @return schemaname
	 */


	public String getSchemaname() {
		return schemaname;
	}

	/**
	 * setter 方法
	 * @param schemaname 属性
	 */


	public void setSchemaname(String schemaname) {
		this.schemaname = schemaname;
	}

}
