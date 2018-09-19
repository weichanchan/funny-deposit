package com.funny;

import com.funny.utils.AESUtils;
import com.funny.utils.EncryptUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

@RunWith(JUnit4.class)
//@SpringBootTest
public class RenrenApplicationTests {

	@Test
	public void contextLoads() {
		System.out.println(AESUtils.encrypt("466243_TVEQB1WNSH_20181201","01234567890123456789012345678912"));

	}

	@Test
	public void AesFeatures(){
		String secretKey = "01234567890123456789012345678912";
		String s1="{system:1}";
        try {
            String ss1 = EncryptUtil.encryptBase64(s1, secretKey);
            System.out.println("features加密encrypt："+ss1);

            String ss2 = EncryptUtil.decryptBase64(ss1, secretKey);
            System.out.println("features解密decrypt："+ss2);

            s1 = URLEncoder.encode(EncryptUtil.encryptBase64(s1, secretKey), "UTF-8");
            System.out.println("features加密URLEncode："+s1);

            String s2 = EncryptUtil.decryptBase64(URLDecoder.decode(s1,"UTF-8"),secretKey);
            System.out.println("features解密URLDecode："+s2);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
