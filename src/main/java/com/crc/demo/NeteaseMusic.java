package com.crc.demo;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;

import com.crc.demo.Utils;

public class NeteaseMusic {
	
	// ��̬����
	final static private String modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7" +
            "b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280" +
            "104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932" +
            "575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b" +
            "3ece0462db0a22b8e7";
	final static private String nonce = "0CoJUm6Qyw8W8jud";
	final static private String pubKey = "010001";
	final static private String headers[][] = {{"Accept","*/*"},
	        {"Accept-Encoding","deflate,sdch"},
	        {"Accept-Language","zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4"},
	        {"Connection","keep-alive"},
	        {"Content-Type","application/x-www-form-urlencoded"},
	        {"Host","music.163.com"},
	        {"Referer","http://music.163.com/search/"},
	        {"User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36"}
	};

	

	// ����
	private int uid;
	
	
	
	private String encryptMD5(String s){
		 char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};       
	     try {
	         byte[] btInput = s.getBytes();
	         // ���MD5ժҪ�㷨�� MessageDigest ����
	         MessageDigest mdInst = MessageDigest.getInstance("MD5");
	         // ʹ��ָ�����ֽڸ���ժҪ
	         mdInst.update(btInput);
	         // �������
	         byte[] md = mdInst.digest();
	         // ������ת����ʮ�����Ƶ��ַ�����ʽ
	         int j = md.length;
	         char str[] = new char[j * 2];
	         int k = 0;
	         for (int i = 0; i < j; i++) {
	             byte byte0 = md[i];
	             str[k++] = hexDigits[byte0 >>> 4 & 0xf];
	             str[k++] = hexDigits[byte0 & 0xf];
	         }
	         return new String(str);
	     } catch (Exception e) {
	         e.printStackTrace();
	         return null;
	     }
	}
	
	/***
	 * ���ڵ�¼����������
	 * @param username �ʺ� �������ֻ����룬��������
	 * @param passWd ���� ���Ĵ���
	 * @return �����Ƿ��¼�ɹ�
	 */
	public String login(String username, String passWd){
		
 		passWd = encryptMD5(passWd);
		
		JSONObject reqObj = new JSONObject();
		
		reqObj.put("username", username);
		reqObj.put("password", passWd);
		reqObj.put("rememberLogin", "true");
		
		String data = encryptedRequest(reqObj.toString());
		
		JSONObject response = Utils.httpPost("https://music.163.com/weapi/login/", data,headers);
		
		if(null !=response){
			return response.toString();
		}
		
		
		
		
		return "��¼ʧ��";
		
		
		
	}
	
	
	private static boolean isMobileNO(String mobiles){  		  
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");  	  
		Matcher m = p.matcher(mobiles);  		 	  
		return m.matches();    
	}  
	
	
	
	/**
	 * �����Ƽ����㷨����Ҫ������������
	 * @param text Ҫ���ܵ��ַ���
	 * @return ���ܺ���ַ���
	 */
	 //based on [darknessomi/musicbox](https://github.com/darknessomi/musicbox)
	public static String encryptedRequest(String text) {
	    String secKey = createSecretKey(16);
	    String encText = aesEncrypt(aesEncrypt(text, nonce), secKey);
	    String encSecKey = rsaEncrypt(secKey, pubKey, modulus);
	    try {
	        return "params=" + URLEncoder.encode(encText, "UTF-8") + "&encSecKey=" + URLEncoder.encode(encSecKey, "UTF-8");
	    } catch (UnsupportedEncodingException e) {
	        //ignore
	        return null;
	    }
	}
	
	//based on [darknessomi/musicbox](https://github.com/darknessomi/musicbox)
	private static String aesEncrypt(String text, String key) {
	    try {
	        IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes("UTF-8"));
	        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
	
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
	
	        byte[] encrypted = cipher.doFinal(text.getBytes());
	
	        return Base64.encodeBase64String(encrypted);
	    } catch (Exception ex) {
	        //ignore
	        return null;
	    }
	}
	
	
	//based on [darknessomi/musicbox](https://github.com/darknessomi/musicbox)
	private static String rsaEncrypt(String text, String pubKey, String modulus) {
	    text = new StringBuilder(text).reverse().toString();
	    BigInteger rs = new BigInteger(String.format("%x", new BigInteger(1, text.getBytes())), 16)
	            .modPow(new BigInteger(pubKey, 16), new BigInteger(modulus, 16));
	    String r = rs.toString(16);
	    if (r.length() >= 256) {
	        return r.substring(r.length() - 256, r.length());
	    } else {
	        while (r.length() < 256) {
	            r = 0 + r;
	        }
	        return r;
	    }
	}
	
	/**
	 * �������һ��16Ϊ����
	 * @param len
	 * @return
	 */
	private static String createSecretKey(int i){
		 return RandomStringUtils.random(i, "0123456789abcde");
	}

	    

	    
	    

}
