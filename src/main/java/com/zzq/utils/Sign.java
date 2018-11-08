package com.zzq.utils;

import org.apache.http.util.TextUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * @author zhangzhiqiang
 * @date 2018-11-06 10:03
 * &Desc 由map入参获取签名sign参数
 */
public class Sign {
    public static LinkedHashMap<String, String> setSign(LinkedHashMap<String, String> paramLinkedHashMap) {
        Object localObject2 = "?";
        Object localObject1 = localObject2;
        if (paramLinkedHashMap != null) {
            localObject1 = localObject2;
            if (!paramLinkedHashMap.isEmpty()) {
                localObject2 = new StringBuilder();
                Iterator localIterator = paramLinkedHashMap.keySet().iterator();
                while (localIterator.hasNext()) {
                    localObject1 = (String) localIterator.next();
                    String str = (String) paramLinkedHashMap.get(localObject1);
                    if (!TextUtils.isEmpty(str)) {
                        ((StringBuilder) localObject2).append((String) localObject1 + "=");
                        ((StringBuilder) localObject2).append(ToolBox.URLEncode(str));
                        if ((!TextUtils.isEmpty(str)) && (localIterator.hasNext())) {
                            ((StringBuilder) localObject2).append("&");
                        }
                    }
                }
                localObject2 = "?" + ((StringBuilder) localObject2).toString();
                localObject1 = localObject2;
                if (((String) localObject2).endsWith("&")) {
                    localObject1 = ((String) localObject2).substring(0, ((String) localObject2).length() - 1);
                }
            }
        }
//        System.out.println("加密前的字符串:" + localObject1);
        localObject2 = MD5.getMD5((String) localObject1 + "2CB3147B-D93C-964B-47AE-EEE448C84E3C");
        paramLinkedHashMap.put("sign", (String) localObject2);
        return paramLinkedHashMap;
    }
}
