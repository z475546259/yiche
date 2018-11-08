package com.zzq.utils;

/**
 * @author zhangzhiqiang
 * @date 2018-11-06 10:24
 * &Desc 编码加密
 */


import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;

public class RSA {
    private static String exponentString = "AQAB";
    private static String module = "06Aoy+Vuf+0qKpNT8E7dyDT7Mx+rQ1Dbao8sORsrroCH0fOa89k5ODTMoHxaCYC7un3TqjwklImlaMd8aNDBTahWHtyqA/1Zl6lANs7jCAK0rLP/6NVRK/lWOPfx+5jTTH9QIPJPYGshk+nb95rXdz6aeUcRI5b5lcL1IqL/htU=";

    public static byte[] encrypt(String paramString) {
        byte[] paramBytes;
        try {
            Object localObject3 = Base64.decode(module, 0);
            Object localObject2 = Base64.decode(exponentString, 0);
            Object localObject1;
            localObject1 = new BigInteger(1, (byte[]) localObject3);

            localObject3 = new BigInteger(1, (byte[]) localObject2);

            localObject2 = new RSAPublicKeySpec((BigInteger) localObject1, (BigInteger) localObject3);

            localObject2 = KeyFactory.getInstance("RSA").generatePublic((KeySpec) localObject2);
            localObject1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            ((Cipher) localObject1).init(1, (Key) localObject2);
            paramBytes = ((Cipher) localObject1).doFinal(paramString.getBytes("utf-8"));
            return paramBytes;
        } catch (Exception e) {
            e.printStackTrace();
            paramBytes = null;
            return paramBytes;
        }
    }
}
