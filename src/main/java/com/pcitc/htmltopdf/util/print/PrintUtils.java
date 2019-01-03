package com.pcitc.htmltopdf.util.print;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import com.pcitc.htmltopdf.dto.PrintTempDto;
import com.pcitc.htmltopdf.entity.PrintTempEntity;
import com.pcitc.htmltopdf.util.pdf.StringUtils;

import org.springframework.jdbc.core.RowMapper;

public class PrintUtils {

	public static String getQueryString(HttpServletRequest request, PrintTempDto pagerDto) {
		String str = request.getQueryString();

		StringBuffer querystr = new StringBuffer();
		if (StringUtils.isNotBlank(str)) {
			String[] querystrs = str.split("&");
			if ((querystrs != null) && (querystrs.length > 0)) {
				for (String s : querystrs) {
					if ((!(s.startsWith("pageNo")))
									&& (!(s.startsWith("pageCount")))) {
						querystr.append(s).append("&");
					}
				}
			}
		}
		return querystr.toString();
	}

	public static RowMapper<PrintTempEntity> getRowMapper() {
		return new RowMapper<PrintTempEntity>() {
			@Override
			public PrintTempEntity mapRow(ResultSet rs, int value)
							throws SQLException {
				PrintTempEntity entity = new PrintTempEntity();
				entity.setId(rs.getString("id"));
				entity.setName(rs.getString("name"));
				entity.setContent(getStringByClob(rs.getClob("content")));
				entity.setUpdateTime(rs.getString("update_time"));
				entity.setCreateTime(rs.getString("create_time"));
				entity.setCreateUser(rs.getString("CREATE_USER"));
				entity.setUpdateUser(rs.getString("UPDATE_USER"));
				return entity;
			}
		};
	}

	public static String getStringByClob(Clob clob) throws SQLException {
		String detailinfo = "";
		if (clob != null) {
			detailinfo = clob.getSubString(1, (int) clob.length());
		}
		return detailinfo;
	}

	public static String getChinese(String sprStr) {
		if (StringUtils.isBlank(sprStr)) {
			return null;
		}
		try {
			sprStr = new String(sprStr.getBytes("ISO8859-1"), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sprStr;
	}

}
