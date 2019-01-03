package com.pcitc.htmltopdf.dto;


public class PrintTempDto  {
	 private long pageNo = 1L;
	 private long pageSize;
	 private long perPage = 10L;
	 private long pageCount;
	 private String orderBy;
	 private String queryStr;
	
	/**  */
	private String  id;

	/**  */
	private String  content;

	/**  */
	private String  createUser;

	/**  */
	private String  createTime;

	/**  */
	private String  updateUser;

	/**  */
	private String  updateTime;

	/**  */
	private String  name;


	// =========getter/setter begin=========
	
	public long getPageNo() {
		return pageNo;
	}

	public void setPageNo(long pageNo) {
		this.pageNo = pageNo;
	}

	public long getPageSize() {
		return pageSize;
	}

	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}

	public long getPerPage() {
		return perPage;
	}

	public void setPerPage(long perPage) {
		this.perPage = perPage;
	}

	public long getPageCount() {
		return pageCount;
	}

	public void setPageCount(long pageCount) {
		this.pageCount = pageCount;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getQueryStr() {
		return queryStr;
	}

	public void setQueryStr(String queryStr) {
		this.queryStr = queryStr;
	}

			
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	
	// =========getter/setter end=========

	public void init(long pageCount) {
		setPageCount(pageCount);
		boolean flag = pageCount % getPerPage() != 0L;
		long iPageSize = (flag) ? pageCount / getPerPage() + 1L : pageCount / getPerPage();
		if (getPageNo() > iPageSize) {
			setPageNo(1L);
		}
		setPageSize(iPageSize);
	}
}

