package com.pcitc.htmltopdf.util.print;

import com.alibaba.fastjson.JSON;
import com.pcitc.htmltopdf.entity.ImageEntity;
import com.pcitc.htmltopdf.entity.PrintTempEntity;
import org.springframework.jdbc.core.RowMapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


/**
 * @author baitao
 * @date 2018/12/27 11:06
 * ReflectUtil
 */

public class ReflectUtil {

	/**
	 * 将jdbcTemplate查询的map结果集 反射生成对应的bean
	 *
	 * @param clazz         意向反射的实体.clazz
	 * @param jdbcMapResult 查询结果集  key is UpperCase
	 * @return
	 * @see
	 */
	public static <T> T reflect(Map<String, Object> jdbcMapResult, Class<T> clazz) {
		//获得
		Field[] fields = clazz.getDeclaredFields();

		//存放field和column对应关系，该关系来自于实体类的 @Column配置
		Map<String/*field name in modelBean*/, String/*column in db*/> fieldHasColumnAnnoMap = new LinkedHashMap<String, String>();
		Annotation[] annotations = null;
		for (Field field : fields) {
			annotations = field.getAnnotations();
			for (Annotation an : annotations) {
				if (an instanceof Column) {
					Column column = (Column) an;
					fieldHasColumnAnnoMap.put(field.getName(), column.value());
				}
			}
		}
		//存放field name 和 对应的来自map的该field的属性值，用于后续reflect成ModelBean
		Map<String, Object> conCurrent = new LinkedHashMap<String, Object>();
		for (Map.Entry<String, String> en : fieldHasColumnAnnoMap.entrySet()) {
			//将column大写。因为jdbcMapResult key is UpperCase
			String key = en.getValue().toUpperCase();

			//获得map的该field的属性值
			Object value = jdbcMapResult.get(key);

			//确保value有效性，防止JSON reflect时异常
			if (value != null) {
				conCurrent.put(en.getKey(), jdbcMapResult.get(key));
			}
		}
		//fastjson reflect to modelbean
		return JSON.parseObject(JSON.toJSONString(conCurrent), clazz);
	}

	/**
	 * 将jdbcTemplate查询的map结果集 反射生成对应的bean
	 *
	 * @param clazz         意向反射的实体.clazz
	 * @param jdbcMapResult 查询结果集  key is UpperCase
	 * @return
	 * @see
	 */
	public static <T> T autoReflect(Map<String, Object> jdbcMapResult, Class<T> clazz) {
		//获得
		Field[] fields = clazz.getDeclaredFields();

		//存放field name 和 对应的来自map的该field的属性值，用于后续reflect成ModelBean
		Map<String, Object> conCurrent = new LinkedHashMap<String, Object>();
		for (Field field : fields) {
			//将column大写。因为jdbcMapResult key is UpperCase
			String key = CamelAndUnderline.camelToUnderline(field.getName()).toUpperCase();
			//获得map的该field的属性值
			Object value = jdbcMapResult.get(key);
			//确保value有效性，防止JSON reflect时异常
			if (value != null) {
				conCurrent.put(field.getName(), value);
			}
		}
		//fastjson reflect to modelbean
		return JSON.parseObject(JSON.toJSONString(conCurrent), clazz);
	}
	
	/**
	 * 将jdbcTemplate查询的map结果集 反射生成对应的bean
	 *
	 * @param clazz         意向反射的实体.clazz
	 * @param jdbcMapResult 查询结果集  key is UpperCase
	 * @param camelAndUnderline 是否进行驼峰转换并大写（这是为了oracle查出的结果，全是大写下划线）
	 * @return
	 * @see
	 */
	public static <T> T autoReflect(Map<String, Object> jdbcMapResult, Class<T> clazz, boolean camelAndUnderline) {
		//获得
		Field[] fields = clazz.getDeclaredFields();

		//存放field name 和 对应的来自map的该field的属性值，用于后续reflect成ModelBean
		Map<String, Object> conCurrent = new LinkedHashMap<String, Object>();
		for (Field field : fields) {
			//将column大写。因为jdbcMapResult key is UpperCase
			String key = null;
			if(camelAndUnderline) {
				key = CamelAndUnderline.camelToUnderline(field.getName()).toUpperCase();
			} else {
				key = field.getName();
			}
			//获得map的该field的属性值
			Object value = jdbcMapResult.get(key);
			//确保value有效性，防止JSON reflect时异常
			if (value != null) {
				System.out.println(field.getType());
				System.out.println(List.class);
				System.out.println(value.getClass());
				System.out.println(String[].class);
				
				if(value.getClass().equals(field.getType())) {
					conCurrent.put(field.getName(), value);
				} else {
					if(value.getClass().equals(String[].class)) {
						String[] array = (String[]) value;
						if(array.length > 0) {
							conCurrent.put(field.getName(), array[0]);
						}	
					}
				}
			}
		}
		//fastjson reflect to modelbean
		return JSON.parseObject(JSON.toJSONString(conCurrent), clazz);
	}

	public static <T> Map<String, Object> autoReflectToMap(T entity) {
		Map<String, Object> conCurrent = new LinkedHashMap<String, Object>();
		try {
			//获得
			Field[] fields = entity.getClass().getDeclaredFields();

			//存放field name 和 对应的来自map的该field的属性值，用于后续reflect成ModelBean
			for (Field field : fields) {
				//将column大写。因为jdbcMapResult key is UpperCase
				String key = CamelAndUnderline.camelToUnderline(field.getName()).toUpperCase();
				field.setAccessible(true);
				//获得map的该field的属性值
				Object value = field.get(entity);
				//确保value有效性，防止JSON reflect时异常
				if (value != null) {
					conCurrent.put(key, value);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return conCurrent;
	}

	public static <T> List<T> listAutoReflect(List<Map<String, Object>> jdbcListMapResult, Class<T> clazz) {
		List<T> list = new ArrayList<T>();
		for (Map<String, Object> map : jdbcListMapResult) {
			list.add(autoReflect(map, clazz));
		}
		return list;
	}

	/**
	 * test example
	 *
	 * @param args
	 * @throws Exception
	 * @see
	 */
	public static void main(String[] args)
					throws Exception {
		//call reflect testing
		Map<String, Object> jdbcMapResult = new HashMap<String, Object>();
		jdbcMapResult.put("X", "1");
		jdbcMapResult.put("Y", "2");
		System.out.println(ReflectUtil.autoReflect(jdbcMapResult, ImageEntity.class));
	}
}
