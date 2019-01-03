package com.pcitc.htmltopdf.service;

import com.pcitc.htmltopdf.dto.PrintTempDto;
import com.pcitc.htmltopdf.entity.PrintTempEntity;

import java.util.List;
import java.util.Map;

public interface PrintTemplateGenerate {

	PrintTempEntity getSinglePrintData(Map<String, Object> paramMap);

	Map<String, List<PrintTempEntity>> getMorePrintData(List<Map<String, Object>> paramMap);

}
