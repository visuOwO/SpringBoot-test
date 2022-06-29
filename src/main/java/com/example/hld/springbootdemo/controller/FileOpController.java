package com.example.hld.springbootdemo.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

@Controller
public class FileOpController {
    public final static String IMG_PATH_PREFIX="static/upload";
    public final static String url = "http://172.17.252.124:8443/api/partnership/view/";

    private String AS2_SERVER_API_URL = "http://43.155.104.116:8443/api";

    @RequestMapping("/file/upload")
    public String uploadPage(HttpServletRequest request) {
        return "fileUploadPage";
    }

    @ResponseBody
    @RequestMapping(value="/file/receive", method= RequestMethod.POST)
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

    @RequestMapping(value = "/file/download", method = RequestMethod.GET)
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

    @ResponseBody
    @RequestMapping("/file/send/{connector}")
    public String send(@PathVariable("connector") String connector, @RequestParam("filename") String filename, HttpServletRequest request) {
        //TODO move the file to the folder under the sender's AS2 ID
        String sender = getAS2IDByConnectionName(connector,"senderIDs");
        String receiver = getAS2IDByConnectionName(connector,"receiverIDs");
        String baseUploadPath = "src/main/resources" + File.separator + IMG_PATH_PREFIX;
        File f = new File(baseUploadPath + File.separator + filename);
        try (FileInputStream fis = new FileInputStream(f)) {
            InputStream is = new BufferedInputStream(fis);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            f.delete();
            String outputPath = "src/main/resources" + File.separator + sender + File.separator + receiver;
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

    @RequestMapping("/file/download/{connector}")
    public void download(@PathVariable("connector") String connector, HttpServletRequest response) {
        //TODO download the file to the folder under the sender's AS2 ID
    }

    private String getAS2IDByConnectionName(String partnership, String name) {
        try {
            URL requestURL = new URL(url+partnership);
            HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization","Basic YWRtaW46MTIzNDU2");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str = br.readLine();
            if (str == null) return null;
            is.close();
            conn.disconnect();
            JSONObject j = JSON.parseObject(str);
            JSONArray results = j.getJSONArray("results");
            JSONObject array = results.getJSONObject(0);
            JSONObject recvID = array.getJSONObject(name);
            return recvID.getString("as2_id");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = {"/api/{resource}/{action}/{id}","/api/{resource}/{action}"}, method = RequestMethod.POST)
    public String postAs2Command(@PathVariable("resource") String resource, @PathVariable("action") String action, @PathVariable(value = "id",required = false) String itemId,
                                @RequestParam Map<String, String> params) {
        String urlName;
        if (itemId == null) {
            urlName = AS2_SERVER_API_URL + "/" + resource + "/" + action;
        }
        else {
            urlName = AS2_SERVER_API_URL + "/" + resource + "/" + action + "/" + itemId;
        }
        try {
            URL url = new URL(urlName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization","Basic dXNlcklEOnBXZA==");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.connect();
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            Iterator<Map.Entry<String, String>> entries = params.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                String s = entry.getKey() + "=" + entry.getValue();
                dos.writeBytes(s);
                if (entries.hasNext()) {
                    dos.writeBytes("&");
                }
            }
            dos.flush();
            dos.close();

            int resultCode = conn.getResponseCode();
            if (HttpURLConnection.HTTP_OK == resultCode) {
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String str = br.readLine();
                if (str == null) return null;
                is.close();
                conn.disconnect();
                return str;
            }
            else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @ResponseBody
    @RequestMapping(value = {"/api/{resource}/{action}/{id}","/api/{resource}/{action}"}, method = RequestMethod.GET)
    public String getAs2Command(@PathVariable("resource") String resource, @PathVariable("action") String action, @PathVariable(value = "id",required = false) String itemId, HttpServletRequest request){
        try {
            String urlName;
            if (itemId == null) {
                urlName = AS2_SERVER_API_URL + "/" + resource + "/" + action;
            }
            else {
                urlName = AS2_SERVER_API_URL + "/" + resource + "/" + action + "/" + itemId;
            }
            URL url = new URL(urlName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization","Basic dXNlcklEOnBXZA==");
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str = br.readLine();
            if (str == null) return null;
            is.close();
            conn.disconnect();

            return str;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
