package com.pcitc.htmltopdf.util.print;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date Utility Class used to convert Strings to Dates and Timestamps
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a> Modified by
 *         <a href="mailto:dan@getrolling.com">Dan Kibler </a> to correct time
 *         pattern. Minutes should be mm not MM (MM is month).
 */
public class DateUtil {

	public static final String DATE_PATTERN = "yyyy-MM-dd";
	private static Log logger = LogFactory.getLog(DateUtil.class);
	
	/**
	 * Checkstyle rule: utility classes should not have public constructor
	 */
	private DateUtil() {
	}

	/**
	 * Return default datePattern (MM/dd/yyyy)
	 * 
	 * @return a string representing the date pattern on the UI
	 */
	public static String getDatePattern() {
		return DATE_PATTERN;
	}


	/**
	 * This method generates a string representation of a date's date/time in
	 * the format you specify on input
	 * 
	 * @param aMask
	 *            the date pattern the string is in
	 * @param aDate
	 *            a date object
	 * @return a formatted string representation of the date
	 * @see SimpleDateFormat
	 */
	public static String getDateTime(String aMask, Date aDate) {
		SimpleDateFormat df;
		String returnValue = "";

		if (aDate == null) {
			logger.error("aDate is null!");
		} else {
			df = new SimpleDateFormat(aMask);
			returnValue = df.format(aDate);
		}

		return (returnValue);
	}

	/**
	 * This method generates a string representation of a date based on the
	 * System Property 'dateFormat' in the format you specify on input
	 * 
	 * @param aDate
	 *            A date to convert
	 * @return a string representation of the date
	 */
	public static String convertDateToString(Date aDate) {
		return getDateTime(getDatePattern(), aDate);
	}

	/**
	 * 根据指定的日期格式，对日期进行格式化
	 * 
	 * @param date
	 * @param formatStr
	 * @return
	 */
	public static String formatDate(Date date, String formatStr) {
		String returnValue = "";
		if (date != null) {
			SimpleDateFormat format = new SimpleDateFormat(formatStr);
			returnValue = format.format(date);
		}
		return returnValue;
	}

	public static Date formatDateWithOutHHMMSS(Date date)
	{
		if(date==null)
		{
			return null;
		}
		SimpleDateFormat myFormat=new SimpleDateFormat("yyyy-MM-dd");   
        String temp = myFormat.format(date);
        Date tempDate = new Date();
        try{
         tempDate= myFormat.parse(temp);
          
        }catch(Exception e){
            e.printStackTrace();
        }
        return tempDate;
	}
	public static void main(String[] arg) {
		System.out
				.println(DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS"));
		System.out
				.println(DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS"));
		System.out
				.println(DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS"));
		System.out.println(formatDateWithOutHHMMSS(new Date()));
	}

}