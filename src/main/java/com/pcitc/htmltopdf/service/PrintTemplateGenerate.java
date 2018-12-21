package com.pcitc.htmltopdf.service;

import java.util.List;
import java.util.Map;

public interface PrintTemplateGenerate {

	Map<String, Object> getSinglePrintData(Map<String, Object> paramMap);

	List<Map<String, Object>> getMorePrintData(List<Map<String, Object>> paramMap) ;

}
