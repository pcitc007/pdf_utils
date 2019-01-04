package com.pcitc.htmltopdf.entity;

import java.io.Serializable;
import java.util.List;

public class LookupJsonModel implements Serializable {
	private String key;
	private String groupBy;

	public LookupJsonModel() {
	}

	public LookupJsonModel(List<Condition> conditions) {
		this.conditions = conditions;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	private String orderBy;

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	private String having;

	public String getHaving() {
		return having;
	}

	public void setHaving(String having) {
		this.having = having;
	}

	private Object[] parameters;

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	private List inputs;

	public List getInputs() {
		return inputs;
	}

	public void setInputs(List inputs) {
		this.inputs = inputs;
	}

	private List<Condition> conditions;

	public List<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

	private String sqlField;

	public String getSqlField() {
		return sqlField;
	}

	public void setSqlField(String sqlField) {
		this.sqlField = sqlField;
	}

	private boolean paging;
	private int pageStart;
	private int pageSize;

	public boolean isPaging() {
		return paging;
	}

	public void setPaging(boolean paging) {
		this.paging = paging;
	}

	public int getPageStart() {
		return pageStart;
	}

	public void setPageStart(int pageStart) {
		this.pageStart = pageStart;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Condition getConditionByName(String name) {
		for (int i = 0; i < conditions.size(); i++) {
			Condition condition = conditions.get(i);
			if (name.equals(condition.getName())) {
				return condition;
			}
		}
		return null;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public String getGroupBy() {
		return groupBy;
	}

	private List<ServerFilterExpression> filters;

	public List<ServerFilterExpression> getFilters() {
		return filters;
	}

	public void setFilters(List<ServerFilterExpression> filters) {
		this.filters = filters;
	}

	private boolean useUserPrincipal = false;
	private String productCd;
	private String programCd;
	private String functionCd;
	private String workFlowDefinitionKey;
	private String workFlowBusinessKey;

	public boolean isUseUserPrincipal() {
		return useUserPrincipal;
	}

	public void setUseUserPrincipal(boolean useUserPrincipal) {
		this.useUserPrincipal = useUserPrincipal;
	}

	public String getProductCd() {
		return productCd;
	}

	public void setProductCd(String productCd) {
		this.productCd = productCd;
	}

	public String getProgramCd() {
		return programCd;
	}

	public void setProgramCd(String programCd) {
		this.programCd = programCd;
	}

	public String getFunctionCd() {
		return functionCd;
	}

	public void setFunctionCd(String functionCd) {
		this.functionCd = functionCd;
	}

	public String getWorkFlowDefinitionKey() {
		return workFlowDefinitionKey;
	}

	public void setWorkFlowDefinitionKey(String workFlowDefinitionKey) {
		this.workFlowDefinitionKey = workFlowDefinitionKey;
	}

	public String getWorkFlowBusinessKey() {
		return workFlowBusinessKey;
	}

	public void setWorkFlowBusinessKey(String workFlowBusinessKey) {
		this.workFlowBusinessKey = workFlowBusinessKey;
	}

}
