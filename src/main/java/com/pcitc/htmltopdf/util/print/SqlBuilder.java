/*
 * Copyright (c) 2010 Financial Sciences Corporation. All Rights Reserved
 */
package com.pcitc.htmltopdf.util.print;

import com.pcitc.htmltopdf.entity.Condition;
import com.pcitc.htmltopdf.entity.LookupJsonModel;
import com.pcitc.htmltopdf.entity.ServerFilterExpression;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * User: $Author$ Time: $Date$ Version: $Revision
 */
public class SqlBuilder {
	public static String buildSql(LookupJsonModel model) {
		String sql;
		StringBuffer sb = new StringBuffer();
		String key = model.getKey();
		if (StringUtils.isNotBlank(key)) {
			sb.append(key);
		}
		if(model.getConditions()!=null){
			for (Condition condition : model.getConditions()) {
				if(condition.getValue()==null) condition.setValue("");
				if (!shouldBuildCondition(condition))
					continue;
				surroundPercent(condition);
				surroundApostrophe(condition);
				if (isMultipleValueCondition(condition)) {
					String multipleValue = (String) condition.getValue();
					String[] codes = multipleValue.split(",");
					for (int i = 0; i < codes.length; i++) {
						String code = codes[i];
						codes[i] = surroundApostrophe(code);
					}
					if ("=".equals(condition.getOperator())) {
						sb.append("\nand ").append(condition.getName())
								.append(" in (")
								.append(StringUtils.join(codes, ',')).append(')');
					} else {
						sb.append("\nand ").append(condition.getName())
								.append(" not in (")
								.append(StringUtils.join(codes, ',')).append(')');
					}
				} else {
					buildSingleValueCondition(sb, condition);
				}
			}
		}
		
		if (model.getFilters() != null) {
			for (ServerFilterExpression filter : model.getFilters()) {
				buildSingleValuefilter(sb, filter);
			}
		}
		if (StringUtils.isNotBlank(model.getGroupBy())) {
			sb.append("\ngroup by ").append(model.getGroupBy());
		}
		if (!model.isPaging() && StringUtils.isNotBlank(model.getHaving())) {
			sb.append("\nhaving ").append(model.getHaving());
		}
		if (!model.isPaging() && StringUtils.isNotBlank(model.getOrderBy())) {
			sb.append("\norder by ").append(model.getOrderBy());
		}
		sql = sb.toString();
		return sql;
	}

	private static void surroundApostrophe(Condition condition) {
		if (shouldSurroundApostrophe(condition)) {
			if (condition.getDataType() == 2) {
				condition.setValue("to_date('" + (String) condition.getValue()
						+ "','YYYY-MM-DD')");
			} else if (condition.getDataType() == 7) {
				condition.setValue("to_timestamp('"
						+ (String) condition.getValue()
						+ "','YYYY-MM-DD HH24:MI:SS.FF')");
			} else {
				condition.setValue(surroundApostrophe((String) condition
						.getValue()));
			}
		} else {
			Object value = condition.getValue();
			Object value2 = condition.getValue2();
			String operator = condition.getOperator();
			if (value instanceof Double) {
				BigDecimal bv = BigDecimal.valueOf((Double) value).setScale(6,
						RoundingMode.HALF_UP);
				condition.setValue(bv);
				if ("between".equalsIgnoreCase(operator)) {
					if (value2 instanceof Double) {
						bv = BigDecimal.valueOf((Double) value2).setScale(6,
								RoundingMode.HALF_UP);
						condition.setValue2(bv);
					}
				}
			}
		}
	}

	private static void surroundPercent(Condition condition) {
		String operator = condition.getOperator();
		if (!operator.equalsIgnoreCase("like")
				&& !operator.equalsIgnoreCase("START_WITH"))
			return;
		String value = (String) condition.getValue();
		if (value.indexOf("%") > 0)
			return;
		if ("like".equalsIgnoreCase(operator)) {
			condition.setValue("%" + value + "%");
		} else if ("START_WITH".equalsIgnoreCase(operator)) {
			condition.setValue(value + "%");
			condition.setOperator("like");
		}
	}

	private static String surroundApostrophe(String value) {
		return "'" + value + "'";
	}

	private static boolean shouldBuildCondition(Condition condition) {
		String operator = condition.getOperator();
		if ("all".equalsIgnoreCase(operator))
			return false;
//		Object value = condition.getValue();
//		if (value == null)
//			return false;
//		if (value instanceof String) {
//			if (StringUtils.isBlank((String) value)) {
//				return false;
//			}
//		}
		return true;
	}

	private static boolean shouldSurroundApostrophe(Condition condition) {
		Object value = condition.getValue();
		String operator = condition.getOperator();
		if (value instanceof String) {
			String valueStr = (String) value;
			if (valueStr.startsWith("'"))
				return false;
		}
		return !(value instanceof Number) && !"in".equalsIgnoreCase(operator)
				&& !(((String) value).indexOf(",") > 0);
	}

	private static void buildSingleValueCondition(StringBuffer sb,
			Condition condition) {
		String operator = condition.getOperator();
		Object value = condition.getValue();
		Object value2 = condition.getValue2();
		if ("not in".equals(operator)) {
			sb.append("\nand ").append(condition.getName()).append(" not in (")
					.append(value).append(')');
		}
		// 修改起始于和包含的处理逻辑。 wjjie add 2016-08-05
		else if("like".equals(operator.toLowerCase())) {
			String tempValue=new String(value.toString());
			// 如果为包含于，则根据value中的空格将查询的词进行分组，并根据分组分别组织查询子句。
			if(tempValue.startsWith("'%")){
				tempValue=tempValue.replace("'", "");
				tempValue=tempValue.replace("%", "");
				String[] values=tempValue.split(" ");
				sb.append("\nand ( 1=1 ");
				for(int i=0;i<values.length;i++){
					if(values[i].trim().equals("")){
						continue;
					}else{
						sb.append("\n  and ").append(condition.getName()).append(" ")
							.append(operator).append(" '%").append(values[i]).append("%' ");
					}
					
				}
				sb.append("\n ) ");
			}
			// 如果为起始于，则将value中的空格替换为%
			else if(tempValue.startsWith("'")){
				tempValue=tempValue.replace(" ", "%");
				sb.append("\nand ").append(condition.getName()).append(' ')
					.append(operator).append(' ').append(tempValue);
			}else{
				sb.append("\nand ").append(condition.getName()).append(' ')
					.append(operator).append(' ').append(value);
			}
		}
		else {
			sb.append("\nand ").append(condition.getName()).append(' ')
					.append(operator).append(' ').append(value);
		}

		if ("between".equalsIgnoreCase(operator)) {
			if (!(value instanceof Number)) {
				if (condition.getDataType() == 2) {
					value2 = "to_date('" + (String) condition.getValue2()
							+ "','YYYY-MM-DD')";
				} else if (condition.getDataType() == 7) {
					value2 = "to_timestamp('" + (String) condition.getValue2()
							+ "','YYYY-MM-DD HH24:MI:SS.FF')";
				} else {
					value2 = surroundApostrophe((String) value2);
				}
			}
			sb.append(" and ").append(value2);
		}
	}

	private static boolean isMultipleValueCondition(Condition condition) {
		Object value = condition.getValue();
		return value instanceof String && ((String) value).indexOf(",") > 0
				&& !"in".equalsIgnoreCase(condition.getOperator())
				&& condition.getDataType() != 2 && condition.getDataType() != 7;
	}

	public static String buildSql(List<Condition> conditions) {
		if (conditions == null || conditions.isEmpty())
			return "";
		LookupJsonModel model = new LookupJsonModel();
		model.setConditions(conditions);
		return buildSql(model);
	}

	public static void buildSingleValuefilter(StringBuffer sb,
			ServerFilterExpression filter) {
		String operator = filter.getFilterOperation();
		Object value = filter.getExpression();
		String prefix = StringUtils.trimToNull(filter.getFilterSortPrefix());
		String type = filter.getFilterType();
		Object value2 = null;
		if (StringUtils.trimToNull(type) != null
				&& StringUtils.trimToNull(operator) != null) {
			if ("Contains".equalsIgnoreCase(operator)) {
				value = "%" + value + "%";
			} else if ("Between".equalsIgnoreCase(operator)
					&& value instanceof Object[]) {
				value2 = ((Object[]) value)[1];
				value = ((Object[]) value)[0];
			}
			if ("String".equalsIgnoreCase(type)) {
				value = surroundApostrophe((String) value);
				value2 = value2 == null ? ""
						: surroundApostrophe((String) value2);
			}else if("Date".equalsIgnoreCase(type)){
				value = "to_date('" + value + "', 'mm/dd/yyyy')";
				value2 = value2 == null ? value
						: "to_date('" + value2 + "', 'mm/dd/yyyy')";
			}
			
			// 如果sql中设置了字段别名，则需要设置该字段，并安装该字段过滤
			if(filter.getFilterOriginalField() != null
					&& filter.getFilterOriginalField().trim().length() > 0){
				sb.append("\nand ").append(filter.getFilterOriginalField()).append(' ');
			}else{
				sb.append("\nand ").append(prefix == null ? "" : prefix + ".")
					.append(filter.getColumnName()).append(' ');
			}
			
			sb.append(transformOperation(operator)).append(' ')
					.append(value);
			if ("Between".equalsIgnoreCase(operator)) {
				sb.append(" and ").append(value2);
			}
		}
	}

	private static String transformOperation(String operator) {
		if ("Contains".equalsIgnoreCase(operator)) {
			return "like";
		} else if ("Equals".equalsIgnoreCase(operator)) {
			return "=";
		}
		return operator;
	}
}
