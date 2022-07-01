package com.example.hld.springbootdemo.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Map;

import static com.example.hld.springbootdemo.controller.FileOpController.DATA_PATH;

@Controller
public class OpenAS2CommandController {

    public final static String url = "https://localhost:8443/api/partnership/view/";


    private static final String AS2_SERVER_API_URL = "https://localhost:8443/api";
    private static final String OPENAS2_AHTHORIZATION = "Basic YWRtaW46MTIzNDU2";

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
        if (action.equalsIgnoreCase("importByFile")) {
            return importByFileName(urlName,itemId,params);
        }
        try {
            URL url = new URL(urlName);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(getDefaultSSLFactory());
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization",OPENAS2_AHTHORIZATION);
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
    public String getAs2Command(@PathVariable("resource") String resource, @PathVariable("action") String action, @PathVariable(value = "id",required = false) String itemId){
        try {
            String urlName;
            if (itemId == null) {
                urlName = AS2_SERVER_API_URL + "/" + resource + "/" + action;
            }
            else {
                urlName = AS2_SERVER_API_URL + "/" + resource + "/" + action + "/" + itemId;
            }
            URL url = new URL(urlName);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(getDefaultSSLFactory());
            conn.setSSLSocketFactory(getDefaultSSLFactory());
            conn.setRequestProperty("Authorization",OPENAS2_AHTHORIZATION);
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

    private String importByFileName(String urlName, String alias, Map<String, String> params) {
        try {
            URL url = new URL(urlName);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(getDefaultSSLFactory());
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", OPENAS2_AHTHORIZATION);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            Iterator<Map.Entry<String, String>> entries = params.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                System.out.println(entry.getKey());
                if (entry.getKey().equalsIgnoreCase("fileName")) {
                    String fileName = DATA_PATH + File.separator + entry.getValue();
                    String s = entry.getKey() + "=" + fileName;
                    dos.writeBytes(s);
                }
                else {
                    String s = entry.getKey() + "=" + entry.getValue();
                    dos.writeBytes(s);
                }
                if (entries.hasNext()) {
                    dos.writeBytes("&");
                }
            }
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
    /**
     *
     * @param partnership the name of partnership(connection)
     * @param name sender/receiver
     * @return AS2 ID
     */
    public static String getAS2IDByConnectionName(String partnership, String name) {
        try {
            URL requestURL = new URL(url+partnership);
            HttpsURLConnection conn = (HttpsURLConnection) requestURL.openConnection();
            conn.setSSLSocketFactory(getDefaultSSLFactory());
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization",OPENAS2_AHTHORIZATION);
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
    private static SSLSocketFactory getDefaultSSLFactory() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] tm = {new MyX509TrustManager()};
        SSLContext sslContext = SSLContext.getDefault();
        sslContext.init(null,tm,new SecureRandom());
        return sslContext.getSocketFactory();
    }
}
