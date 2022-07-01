package com.example.hld.springbootdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.hld.springbootdemo.controller.OpenAS2CommandController.*;

@Controller
public class FileOpController {
    public final static String OPENAS2_DATA_BASE_PATH="/home/visu/OpenAS2-data";
    public final static String DATA_PATH ="files";
    public final static String OUTBOX_PATH = "outbox";
    public final static String INBOX_PATH = "inbox";
    public final static String CERT_DATA_PATH = "certs";
    public final static String OPENAS2_SENDER = "senderIDs";
    public final static String OPENAS2_RECEIVER = "receiverIDs";

    @RequestMapping("/file/upload")
    public String uploadPage() {
        return "fileUploadPage";
    }

    @ResponseBody
    @RequestMapping(value="/file/receive", method= RequestMethod.POST)
    public String receiveFile(@RequestParam("file")MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return "There's No File.";
        }

        //Use Directory Relative Path
        String filename = file.getOriginalFilename();
        String absolutePath;
        if (filename.endsWith(".p12") || filename.endsWith(".p7b") || filename.endsWith(".pfx") || filename.endsWith(".cer")) {
            absolutePath = OPENAS2_DATA_BASE_PATH + File.separator + CERT_DATA_PATH;
        }
        else {
            absolutePath = OPENAS2_DATA_BASE_PATH + File.separator + DATA_PATH;
        }
        File absoluteDir = new File(absolutePath);

        if (!absoluteDir.exists()) {
            if (!absoluteDir.mkdirs())
                throw new Exception("Unknown Exception when creating Directory.");
        }
        
        File uploadFile = new File(absoluteDir.getAbsolutePath() + File.separator + filename);
        file.transferTo(uploadFile);
        return "File uploaded successfully!";
    }

    @ResponseBody
    @RequestMapping(value = "/file/download/{resource}", method = RequestMethod.GET)
    public String download(@PathVariable("resource") String resource,@RequestParam("filename") String filename, HttpServletResponse response) {
        try {
            String absolutePath;
            if (Objects.equals(resource, DATA_PATH)) {
                absolutePath = OPENAS2_DATA_BASE_PATH + File.separator + DATA_PATH + File.separator + filename;
            } else if (Objects.equals(resource, CERT_DATA_PATH)) {
                absolutePath = OPENAS2_DATA_BASE_PATH + File.separator + CERT_DATA_PATH + File.separator + filename;
            } else {
                return "Unknown resource";
            }
            File file = new File(absolutePath);
            FileInputStream fis = new FileInputStream(file);
            downloadFile(filename, response, file, fis);
            return "Success!";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @ResponseBody
    @RequestMapping("/file/send/{connector}")
    public String send(@PathVariable("connector") String connector, @RequestParam("filename") String filename) {
        //TODO move the file to the folder under the sender's AS2 ID
        String sender = getAS2IDByConnectionName(connector,OPENAS2_SENDER);
        String receiver = getAS2IDByConnectionName(connector,OPENAS2_RECEIVER);
        String baseUploadPath = OPENAS2_DATA_BASE_PATH + File.separator + DATA_PATH;
        File f = new File(baseUploadPath + File.separator + filename);
        try (FileInputStream fis = new FileInputStream(f)) {
            InputStream is = new BufferedInputStream(fis);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            f.delete();
            String outputPath = OPENAS2_DATA_BASE_PATH + File.separator + OUTBOX_PATH + File.separator + sender + "-" + receiver;
            File op = new File(outputPath);
            if (!op.exists()) {
                op.mkdirs();
            }
            File of = new File(outputPath + File.separator + filename);
            FileOutputStream fos = new FileOutputStream(of);
            fos.write(buffer);
            fos.close();
            return "Success";
        } catch (IOException e) {
            return "File Not Found.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown Error.";
        }
    }

    /**
     * Download the file sent by a connection
     * @param connection connection(partnership) name
     * @param filename filename
     */
    @ResponseBody
    @RequestMapping(value = "/file/downloadSentFile/{connection}", method = RequestMethod.GET)
    public String downloadSentFile(@PathVariable("connection") String connection, @RequestParam("filename") String filename, HttpServletResponse response) {
        String senderID = getAS2IDByConnectionName(connection, OPENAS2_SENDER);
        String receiverID = getAS2IDByConnectionName(connection, OPENAS2_RECEIVER);
        String absoluteSentPath = OPENAS2_DATA_BASE_PATH + File.separator + DATA_PATH + File.separator + senderID + "-" + receiverID + File.separator + "sent";
        File f = new File(absoluteSentPath + File.separator + filename);
        try (FileInputStream fis = new FileInputStream(f)) {
            downloadFile(filename, response, f, fis);
        } catch (IOException e) {
            return "File not found" + filename;
        }
        return "Successfully download file: " + filename;
    }


    @ResponseBody
    @RequestMapping(value="/file/downloadReceivedFile/{connection}", method = RequestMethod.GET)
    public String downloadReceivedFile(@PathVariable("connection") String connection, @RequestParam("filename") String filename, HttpServletResponse response) {
        String senderID = getAS2IDByConnectionName(connection, OPENAS2_SENDER);
        String receiverID = getAS2IDByConnectionName(connection, OPENAS2_RECEIVER);
        String absoluteSentPath = OPENAS2_DATA_BASE_PATH + File.separator + INBOX_PATH + File.separator + senderID + "-" + receiverID + File.separator + "received";
        File f = new File(absoluteSentPath + File.separator + filename);
        try (FileInputStream fis = new FileInputStream(f)) {
            downloadFile(filename, response, f, fis);
        } catch (IOException e) {
            return "File not found" + filename;
        }
        return "Successfully download file: " + filename;
    }

    private void downloadFile(@RequestParam("filename") String filename, HttpServletResponse response, File f, FileInputStream fis) throws IOException {
        InputStream is = new BufferedInputStream(fis);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        is.close();
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "utf-8"));
        response.addHeader("Content-Length", "" + f.length());
        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/octet-stream");
        outputStream.write(buffer);
        outputStream.flush();
    }

    @ResponseBody
    @RequestMapping(value = "/file/listSentFile/{connection}", method = RequestMethod.GET)
    public List<String> listSentFile(@PathVariable("connection") String connection) {
        String senderID = getAS2IDByConnectionName(connection, OPENAS2_SENDER);
        String receiverID = getAS2IDByConnectionName(connection, OPENAS2_RECEIVER);
        String absoluteSentPath = OPENAS2_DATA_BASE_PATH + File.separator + DATA_PATH + File.separator + senderID + "-" + receiverID + File.separator + "sent";
        return readFiles(absoluteSentPath);
    }

    @ResponseBody
    @RequestMapping(value = "/file/listReceivedFile/{connection}", method = RequestMethod.GET)
    public List<String> listReceivedFile(@PathVariable("connection") String connection) {
        String senderID = getAS2IDByConnectionName(connection, OPENAS2_SENDER);
        String receiverID = getAS2IDByConnectionName(connection, OPENAS2_RECEIVER);
        String absoluteSentPath = OPENAS2_DATA_BASE_PATH + File.separator + DATA_PATH + File.separator + senderID + "-" + receiverID + File.separator + "received";
        return readFiles(absoluteSentPath);
    }

    public List<String> readFiles(String path) {
        List<String> fileList = new ArrayList<>();
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File value : files) {
                if (!value.isDirectory()) {
                    String path1 = value.getPath();
                    String filename = path1.substring(path1.lastIndexOf(File.separator) + 1);
                    fileList.add(filename);
                }
            }
        }
        else {
            String path1 = file.getPath();
            String filename = path1.substring(path1.lastIndexOf(File.separator)+1);
            fileList.add(filename);
        }
        return fileList;
    }
}
