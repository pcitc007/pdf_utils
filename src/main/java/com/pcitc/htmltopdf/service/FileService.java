package com.pcitc.htmltopdf.service;

import com.pcitc.htmltopdf.entity.Html2PdfEntity;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author baitao
 * @date 2018/11/13 14:02
 */
public interface FileService {

//    ResponseEntity<FileSystemResource> export(File file);

    String upload(MultipartFile file, String rootPath);

    String html2pdf(Html2PdfEntity html2PdfEntity, String pdfPath, String ttfPath, String imgPath) ;
}
