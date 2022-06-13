package com.example.hld.springbootdemo.controller;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Controller
@RequestMapping("/file")
public class FileOpController {
    public final static String IMG_PATH_PREFIX="static/upload";

    @RequestMapping("/upload")
    public String uploadPage(HttpServletRequest request) {
        return "fileUploadPage";
    }

    @ResponseBody
    @RequestMapping(value="receive", method= RequestMethod.POST)
    public String receiveFile(@RequestParam("file")MultipartFile file, HttpServletRequest request) throws Exception {
        if (file.isEmpty()) {
            return "There's No File.";
        }

        //Use Directory Relative Path
        String directRelativePath = "src/main/resources" + File.separator + IMG_PATH_PREFIX;
        File directRelativeDir = new File(directRelativePath);

        if (!directRelativeDir.exists()) {
            if (!directRelativeDir.mkdirs())
                throw new Exception("Unknown Exception when creating Directory.");
        }
        String filename = file.getOriginalFilename();
        File uploadFile = new File(directRelativeDir.getAbsolutePath() + File.separator + filename);
        file.transferTo(uploadFile);
        return "File uploaded successfully!";
    }

    @RequestMapping("/download")
    public void download(@RequestParam("filename") String filename, HttpServletResponse response) {
        try {
            String directRelativePath = "src/main/resources" + File.separator + IMG_PATH_PREFIX + File.separator + filename;
            File file = new File(directRelativePath);
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

            FileInputStream fis = new FileInputStream(file);
            InputStream is = new BufferedInputStream(fis);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
