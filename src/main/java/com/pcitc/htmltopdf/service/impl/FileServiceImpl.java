package com.pcitc.htmltopdf.service.impl;

import com.pcitc.htmltopdf.entity.Html2PdfEntity;
import com.pcitc.htmltopdf.service.FileService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class FileServiceImpl implements FileService {

//    public ResponseEntity<FileSystemResource> export(File file) {
//        HttpHeaders headers = new HttpHeaders();
//        /*文件直接打开
//               .header("Content-disposition","attachment;filename=" +fileId)
//        文件在浏览器打开
//              .header("Content-disposition","filename=" +fileId)*/
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Content-Disposition", "attachment; filename=" + file.getName());
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");
//        return ResponseEntity.EMPTY
//                .ok()
//                .headers(headers)
//                .contentLength(file.length())
//                .contentType(MediaType.parseMediaType("application/octet-stream"))
//                .body(new FileSystemResource(file));
//    }

    public String upload(MultipartFile file, String rootPath) {
        if (!file.isEmpty()) {
            try {
                // 文件存放服务端的位置
                if(null == rootPath || "".equals(rootPath)) {
                    rootPath = "." + File.separator + "tmp";
                }
                File dir = new File(rootPath);
                if (!dir.exists())
                    dir.mkdirs();
                // 写文件到服务器
                String fileName = createFileName(file.getOriginalFilename());
                File serverFile = new File(dir.getAbsolutePath() + File.separator + fileName);
                if(fileName.isEmpty()) {
                    fileName = createFileName(file.getOriginalFilename());
                    serverFile = new File(dir.getAbsolutePath() + File.separator + fileName);
                }
                file.transferTo(serverFile);
                return fileName;
            } catch (Exception e) {
                return "You failed to upload " +  file.getOriginalFilename() + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " +  file.getOriginalFilename() + " because the file was empty.";
        }
    }

    public String html2pdf(Html2PdfEntity html2PdfEntity, String pdfPath, String ttfPath, String imgPath) {

        if(StringUtils.isBlank(imgPath)) {
            imgPath = "./src/main/resources/img/";
        }
        if(StringUtils.isNotBlank(html2PdfEntity.getImgName())) {
            html2PdfEntity.setImgName(imgPath + html2PdfEntity.getImgName());
        }
        String tempHtmlStr = html2PdfEntity.getHtmlStr();

//        return HtmlToPDF.htmlStrToPdf(tempHtmlStr, html2PdfEntity.getOriginal(), html2PdfEntity.getPageSize(), html2PdfEntity.getDirection(), html2PdfEntity.getImgName(), html2PdfEntity.getImgX(), html2PdfEntity.getImgY(), pdfPath, ttfPath);
        return "";

    }

    private String createFileName(String originalFilename) {
        return System.currentTimeMillis()/1000 + originalFilename;
    }
}