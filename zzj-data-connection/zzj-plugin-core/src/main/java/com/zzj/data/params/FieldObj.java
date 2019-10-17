package com.zzj.data.params;

import java.util.List;

public class FieldObj {

	/**
	 * 字段名称
	 */
	private String columnName;

	/**
	 * 类型
	 */
	private String type;

	/**
	 * 长度
	 */
	private int length;

	/**
	 * 注解
	 */
	private String comment;

	/**
	 * 主键标识，如果是则为Y
	 */
	private String dbKeyFlg;

	/**
	 * 索引标识，如果是则为Y
	 */
	private String colIndex;

	/**
	 * 分词类型
	 */
	private String indexType;

	/**
	 * 字段分区信息
	 */
	private List<String> partitions;

	public String getIndexType() {
		return indexType;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	public String getDbKeyFlg() {
		return dbKeyFlg;
	}

	public void setDbKeyFlg(String dbKeyFlg) {
		this.dbKeyFlg = dbKeyFlg;
	}

	public String getColIndex() {
		return colIndex;
	}

	public void setColIndex(String colIndex) {
		this.colIndex = colIndex;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public List<String> getPartitions() {
		return partitions;
	}

	public void setPartitions(List<String> partitions) {
		this.partitions = partitions;
	}

}
