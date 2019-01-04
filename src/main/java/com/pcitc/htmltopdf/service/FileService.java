package com.pcitc.htmltopdf.service;

import com.pcitc.htmltopdf.entity.Html2PdfEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author baitao
 * @date 2018/11/13 14:02
 */
public interface FileService {
    //取消文件下载
//    ResponseEntity<FileSystemResource> export(File file);

    String upload(MultipartFile file, String rootPath);

    String html2pdf(Html2PdfEntity html2PdfEntity, String pdfPath, String ttfPath, String imgPath) ;
}
