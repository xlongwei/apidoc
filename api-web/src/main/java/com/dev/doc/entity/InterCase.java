package com.dev.doc.entity;

import com.dev.base.mybatis.BaseMybatisEntity;

@SuppressWarnings("serial")
public class InterCase extends BaseMybatisEntity {
	/** 文档id*/
	private Long docId;
	
	/** 接口id */
	private Long interId;
	
	/** 名称*/
	private String name;
	
	/** 请求报文 */
	private String reqSchema;	
	
	/** 响应报文 */
	private String respSchema;	
	
	/** 排序权重，值越小，排序越靠前，默认步长为50*/
	private int sortWeight;

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public Long getInterId() {
		return interId;
	}

	public void setInterId(Long interId) {
		this.interId = interId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReqSchema() {
		return reqSchema;
	}

	public void setReqSchema(String reqSchema) {
		this.reqSchema = reqSchema;
	}

	public String getRespSchema() {
		return respSchema;
	}

	public void setRespSchema(String respSchema) {
		this.respSchema = respSchema;
	}

	public int getSortWeight() {
		return sortWeight;
	}

	public void setSortWeight(int sortWeight) {
		this.sortWeight = sortWeight;
	}
}
