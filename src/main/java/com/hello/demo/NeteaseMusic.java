package com.hello.demo;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.com.common.HttpHeader;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;

public class NeteaseMusic {
	
	//静态变量���
	final static private String modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7" +
            "b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280" +
            "104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932" +
            "575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b" +
            "3ece0462db0a22b8e7";
	final static private String nonce = "0CoJUm6Qyw8W8jud";
	final static private String pubKey = "010001";
	private HttpHeader headers ;

	public NeteaseMusic(){
		headers = initHeader();
	}

	private HttpHeader initHeader(){
		return HttpHeader.custom()
				.acceptEncoding("deflate,sdch")
				.accept("*/*")
				.connection("keep-alive")
				.acceptLanguage("zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4")
				.contentType("application/x-www-form-urlencoded")
				.host("music.163.com")
				.referer("http://music.163.com/search/")
				.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36");
	}

	//用户uid
	private int uid;


	/**
	 *  MD5 加密，用于密码的加密（返回小写md5）
	 * @param s 要加密的字符串
	 * @return 加密后的字符串
	 */
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

	/**
	 * 登录
	 * @param username 用户名
	 * @param passWd 密码 （明文）TODO 暂时使用明文
	 * @return 请求后的字符串
	 */
	public String login(String username, String passWd){
 		passWd = encryptMD5(passWd);
		JSONObject reqObj = new JSONObject();
        JSONObject response = null;

        if(isMobileNO(username)){
            reqObj.put("username", username);
            reqObj.put("password", passWd);
            reqObj.put("rememberLogin", "true");

            String data = encryptedRequest(reqObj.toString());
            response = Utils.httpSend(ApiUtils.CELLPHONELOGIN,data,headers);

        }else{
            reqObj.put("username", username);
            reqObj.put("password", passWd);
            reqObj.put("rememberLogin", "true");

            String data = encryptedRequest(reqObj.toString());
            ;
            //	JSONObject response = Utils.httpPost(ApiUtils.LOGIN.getAddress(), data,headers);
            response = Utils.httpSend(ApiUtils.LOGIN,data,headers);
        }


		if(null !=response){
			return response.toString();
		}
		return "登录失败";
	}

	public String sign(){
		JSONObject reqObj = new JSONObject();
		JSONObject response = null;
		reqObj.put("type",1);
		//headers.cookie("MUSIC_U=01facb38a235cf7c615bcc917d3f6cee5ad7bfae5457715274f2b59befd5744387581dcafe41d8e93109871d12225f858b72a47e84f57be3305842396b5dfc01");
		String data = encryptedRequest(reqObj.toString());

		response = Utils.httpSend(ApiUtils.SIGN,data,headers);


		if(null !=response){
			return response.toString();
		}
		return "签到失败";
	}
	
	
	private static boolean isMobileNO(String mobiles){  		  
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");  	  
		Matcher m = p.matcher(mobiles);  		 	  
		return m.matches();    
	}


    /**
     *  用于请求数据的加密 加密算法musicbox
     * @param text 要加密的数据
     * @return 加密后的字符串
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
     * 生成随机数
     * @param i 要生成的位数
     * @return 生成后的结果
     */
	private static String createSecretKey(int i){
		 return RandomStringUtils.random(i, "0123456789abcde");
	}

}
