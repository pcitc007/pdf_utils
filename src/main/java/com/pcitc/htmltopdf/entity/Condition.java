package com.pcitc.htmltopdf.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author Jason Liu
 *         <p/>
 *         this class is a search condition
 */
public class Condition implements Serializable {
	/**
	 * the search condition match value
	 */
	
	private int dataType;
	
	private Object value;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * if the search condition match value interval, the second condtion value
	 */
	private Object value2;

	public Object getValue2() {
		return value2;
	}

	public void setValue2(Object value2) {
		this.value2 = value2;
	}

	/**
	 * the property name in model class
	 */
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * the search operator, such as equal, not equal
	 */
	private String operator;

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	private List<Condition> orConditions;

	public List<Condition> getOrConditions() {
		return orConditions;
	}

	public void setOrConditions(List<Condition> orConditions) {
		this.orConditions = orConditions;
	}

	/**
	 * 条件List中是否已经存在名字为key值的条件
	 *
	 * @param conditions
	 * @param key
	 * @return
	 */
	public static Condition exist(List<Condition> conditions, String key) {
		Condition exist = null;

		for (Condition condition : conditions) {
			if (condition.getName().equals(key)) {
				exist = condition;
				break;
			}
		}

		return exist;
	}

	public Condition() {
	}

	public Condition(String name) {
		this.name = name;
	}

	public Condition(String name, String operator) {
		this.name = name;
		this.operator = operator;
	}

	public Condition(String name, String operator, Object value) {
		this.name = name;
		this.operator = operator;
		this.value = value;
	}

	public Condition(String name, String operator, Object value, Object value2) {
		this.name = name;
		this.operator = operator;
		this.value = value;
		this.value2 = value2;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Condition");
		sb.append("{name='").append(name).append('\'');
		sb.append(", operator='").append(operator).append('\'');
		sb.append(", value=").append(value);
		sb.append(", orConditions=").append(orConditions);
		sb.append('}');
		return sb.toString();
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	
	@Override
	public Condition clone(){
		Condition newCondition=new Condition();
		newCondition.setDataType(this.dataType);
		newCondition.setName(this.name+"");
		newCondition.setOperator(operator+"");
		newCondition.setValue(value);
		newCondition.setValue2(value2);
		return newCondition;
	}
}
