package com.pcitc.htmltopdf.util.print;

import java.lang.annotation.*;

/**
 * @author baitao
 * @date 2018/12/27 11:08
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {

	/**
	 * 数据库的名字
	 * @return
	 */
	String value();

}
