package com.zzq.utils;

import java.net.URLEncoder;

/**
 * @author zhangzhiqiang
 * @date 2018-11-05 12:42
 * &Desc ac
 */
public class ToolBox {
    public static String URLEncode(String paramString) {
        if (paramString == null) {
            paramString = "";
        }
        try {
            paramString = URLEncoder.encode(paramString, "UTF-8").replaceAll("\\+", "%20");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paramString;

    }
}
