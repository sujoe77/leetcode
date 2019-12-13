package com.pineapple.java.zip;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ZipUtil {

    public static String compress2(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes("UTF-8"));
        gzip.close();
        return out.toString("UTF-8");
    }

    public static byte[] compress(String str) throws Exception {
        if (str == null || str.length() == 0) {
            return new byte[]{};
        }
        System.out.println("String length : " + str.length());
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(str.getBytes("UTF-8"));
        gzip.close();
        return obj.toByteArray();
    }

    public static String decompress(byte[] bytes) throws Exception {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
//        System.out.println("Input String length : " + str.length());
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        String outStr = "";
        String line;
        while ((line = bf.readLine()) != null) {
            outStr += line;
        }
        System.out.println("Output String lenght : " + outStr.length());
        return outStr;
    }

    public static void main(String[] args) throws Exception {
        String string = "admin";
        System.out.println("after compress:");
        System.out.println(ZipUtil.compress(string));
        byte[] ret = ZipUtil.compress(string);
//        System.out.println(zip2);
        System.out.println(ZipUtil.decompress(ret));
    }
}
