package com.pcitc.htmltopdf.entity;

/**
 * @author baitao
 * @date 2018/11/13 14:46
 */
public class ResultData<T> {

	/*错误码*/
	private Integer code;
	/*提示信息*/
	private String msg;
	/*具体的内容*/
	private T data;

	public ResultData(T data) {
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
