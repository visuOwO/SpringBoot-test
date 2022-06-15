package com.example.hld.springbootdemo.HTTPTest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MultipartTest {
    static String boundary = "abcde123456";
    static String prefix = "--";
    static String newLine = "\r\n";

    public static void main(final String args[]) {
        test();
    }

    private static void test() {
        try {
            URL url = new URL("http://localhost:8080/file/receive");
            HttpURLConnection conn  = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-type", "multipart/form-data; boundary=" + boundary);
            conn.connect();
            ConfigHttpMultipart(conn.getOutputStream());

            StringBuffer strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            String res = "";
            res = strBuf.toString();
            reader.close();
            reader = null;
        } catch (MalformedURLException e) {
            System.out.println("URL ERROR!");
        } catch (IOException e) {
            System.out.println("IO Error!");
        }
    }

    private static void ConfigHttpMultipart(final OutputStream out) {
        StringBuilder params = new StringBuilder();
        params.append(prefix).append(boundary).append(newLine);
        params.append("Content-Disposition: form-data; name=\"file\"; filename=\"test.pem\"");
        params.append(newLine);
        params.append("Content-Type: application/octet-stream");
        params.append(newLine).append(newLine);
        File file = new File(".idea/httpRequests"+File.separator+"test.pem");
        try {
            InputStream in = new FileInputStream(file);
            out.write(readBuffer(in));
            out.write((prefix + boundary + prefix + newLine).getBytes());
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println(" no file! ");
        } catch (IOException e) {
            System.out.println(" io error! ");
        }
    }

    public static byte[] readBuffer(final InputStream ins) throws IOException {
        byte[] b = new byte[1024];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int len = 0;
        while ((len = ins.read(b)) != -1) {
            stream.write(b, 0, len);
        }
        return stream.toByteArray();
    }
 }
