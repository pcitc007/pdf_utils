package com.pcitc.htmltopdf.entity;
import java.io.Serializable;

public class ServerFilterExpression implements Serializable
{
   private String columnName;
   private String filterOperation;
   private Object expression;
   private String filterType;
   private String filterSortPrefix;
   private String filterOriginalField;
   public String getColumnName()
   {
      return columnName;
   }
   public void setColumnName(String columnName)
   {
      this.columnName = columnName;
   }
   public String getFilterOperation()
   {
      return filterOperation;
   }
   public void setFilterOperation(String filterOperation)
   {
      this.filterOperation = filterOperation;
   }
   public Object getExpression()
   {
      return expression;
   }
   public void setExpression(Object expression)
   {
      this.expression = expression;
   }
   public String getFilterType()
   {
      return filterType;
   }
   public void setFilterType(String filterType)
   {
      this.filterType = filterType;
   }
   public String getFilterSortPrefix()
   {
      return filterSortPrefix;
   }
   public void setFilterSortPrefix(String filterSortPrefix)
   {
      this.filterSortPrefix = filterSortPrefix;
   }
   public void setFilterOriginalField(String filterOriginalField) {
	   this.filterOriginalField = filterOriginalField;
   }
   public String getFilterOriginalField() {
	   return filterOriginalField;
   }
 
}
