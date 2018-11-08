package com.zzq.utils;

/**
 * @author zhangzhiqiang
 * @date 2018-11-05 12:36
 * &Desc vv
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5
{
    public static String getMD5(String paramString)
    {
//        localObject1 = null;
        try
        {
            Object localObject2 = MessageDigest.getInstance("MD5");
            ((MessageDigest)localObject2).update(paramString.getBytes());
            byte[] arrayOfByte = ((MessageDigest)localObject2).digest();
            StringBuffer localStringBuffer = new StringBuffer();
//            localStringBuffer.<init>();
            for (int i = 0; i < arrayOfByte.length; i++)
            {
                int j = arrayOfByte[i] & 0xFF;
                localObject2 = Integer.toHexString(j);
                paramString = (String)localObject2;
                if (j <= 15)
                {
//                    paramString = new StringBuilder();
//                    paramString.<init>();
                    paramString = "0" + (String)localObject2;
//                    paramString = "0" ;
                }
                localStringBuffer.append(paramString);
            }
            paramString = localStringBuffer.toString();
        }
        catch (NoSuchAlgorithmException paramString2)
        {
//            for (;;)
//            {
//                paramString.printStackTrace();
//                paramString = (String)localObject1;
//            }
            System.out.println(paramString2);
        }
        return paramString;
    }

    public static void main(String[] paramArrayOfString)
            throws NoSuchAlgorithmException
    {
        System.out.println(getMD5("?op=get&NoteID=b54641c027c94912baa2b853442129622CB3147B-D93C-964B-47AE-EEE448C84E3C"));
    }
}
