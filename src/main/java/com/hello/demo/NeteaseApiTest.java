package com.hello.demo;

import com.com.common.HttpHeader;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NeteaseApiTest {

    //静态变量���
    final static private String modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7" +
            "b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280" +
            "104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932" +
            "575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b" +
            "3ece0462db0a22b8e7";
    final static private String nonce = "0CoJUm6Qyw8W8jud";
    final static private String pubKey = "010001";
    final static private String headersStatic[][] = {{"Accept", "*/*"},
            {"Accept-Encoding", "deflate,sdch"},
            {"Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4"},
            {"Connection", "keep-alive"},
            {"Content-Type", "application/x-www-form-urlencoded"},
            {"Host", "music.163.com"},
            {"Referer", "http://music.163.com/search/"},
            {"User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36"}
    };

    final static private RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(120000).setSocketTimeout(60000)
            .setConnectionRequestTimeout(60000).setCookieSpec(CookieSpecs.STANDARD).build();
    private CookieStore cookieStore = new BasicCookieStore();

    final private HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig)
            .setDefaultCookieStore(cookieStore).build();

    private Map<String, String> headers = new HashMap<String, String>();

    public NeteaseApiTest() {
        for (String[] header : headersStatic) {
            headers.put(header[0], header[1]);
        }

    }

    private HttpHeader initHeader() {
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
     * MD5 加密，用于密码的加密（返回小写md5）
     *
     * @param s 要加密的字符串
     * @return 加密后的字符串
     */
    private String encryptMD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
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
     *
     * @param username 用户名
     * @param passWd   密码 （明文）TODO 暂时使用明文
     * @return 请求后的字符串
     */
    public String login(String username, String passWd) {
        passWd = encryptMD5(passWd);
        JSONObject reqObj = new JSONObject();
        JSONObject response = null;
        reqObj.put("username", username);
        reqObj.put("password", passWd);
        reqObj.put("rememberLogin", "true");
        String data = encryptedRequest(reqObj.toString());
        HttpResponse<String> result = null;
        try {
            Unirest.setHttpClient(this.httpClient);
            result = Unirest.post("https://music.163.com/weapi/login/")
                    .headers(headers)
                    .body(data)
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        JSONArray cookieArray = new JSONArray();
        if (cookieStore != null) {
            for (Cookie cookie : cookieStore.getCookies()) {
                JSONObject cookieObject = new JSONObject();
                cookieObject.put("name", cookie.getName());
                cookieObject.put("value", cookie.getValue());
                cookieObject.put("domain", cookie.getDomain());
                cookieObject.put("path", cookie.getPath());
                cookieArray.put(cookieObject);
            }
        }

        File f = new File("D:/cookie.txt");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fw = new FileWriter("D:/cookie.txt");
            fw.write(cookieArray.toString());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "登录失败";
    }


    public CookieStore getCookieStoreByStr(String cookieStr) {
        JSONArray jsonArray = new JSONArray(cookieStr);
        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        String str = null;

        str = "2017-1-18";
        try {
            date = format1.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int len = jsonArray.length();
        for (int i = 0; i < len; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            BasicClientCookie cookie = new BasicClientCookie(jsonObject.getString("name"), jsonObject.getString("value"));
            cookie.setVersion(1);
            cookie.setDomain(jsonObject.getString("domain"));
            cookie.setPath(jsonObject.getString("path"));
            cookie.setExpiryDate(date);
            cookie.setAttribute("path",jsonObject.getString("path"));
            cookie.setAttribute("httponly","null");
            cookie.setAttribute("expires","Mon, 23 Oct 2084 06:28:28 GMT");
            this.cookieStore.addCookie(cookie);
        }
        return this.cookieStore;
    }

    public String sign() {
        JSONObject reqObj = new JSONObject();
        HttpResponse<JsonNode> result = null;
        JSONObject response = null;
        reqObj.put("type", 1);
        //headers.cookie("MUSIC_U=01facb38a235cf7c615bcc917d3f6cee5ad7bfae5457715274f2b59befd5744387581dcafe41d8e93109871d12225f858b72a47e84f57be3305842396b5dfc01");
        String data = encryptedRequest(reqObj.toString());

        //response = Utils.httpSend(ApiUtils.SIGN, data, headers);

        try {
            Unirest.setHttpClient(this.httpClient);
            result = Unirest.post("http://music.163.com/weapi/point/dailyTask").headers(headers).body(data).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        if (null != result) {
            return result.toString();
        }
        return "签到失败";
    }


    private static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }


    /**
     * 用于请求数据的加密 加密算法musicbox
     *
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
     *
     * @param i 要生成的位数
     * @return 生成后的结果
     */
    private static String createSecretKey(int i) {
        return RandomStringUtils.random(i, "0123456789abcde");
    }

    public static void main(String[] args) {
        NeteaseApiTest api = new NeteaseApiTest();
  //      api.login("chen.ruo.chen@163.com", "858833crc");

        try {
            FileReader fileReader = new FileReader("D:/cookie.txt");
            char[] buf = new char[2000];
            fileReader.read(buf);
            JSONArray jsonArray = new JSONArray(String.valueOf(buf));
            api.getCookieStoreByStr(String.valueOf(buf));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        api.sign();
        // api.getCookieStoreByStr("[{\"name\":\"MUSIC_U\",\"value\":\"01facb38a235cf7c615bcc917d3f6cee5ad7bfae545771528cca4c62439509d8907496a76890ebcbfc6683304939ec5b2ad1c7c4b05e3b77f2f513a9c38b5dc7\"},{\"name\":\"NETEASE_WDA_UID\",\"value\":\"53149587#|#1423386031395\"},{\"name\":\"__csrf\",\"value\":\"d7d96e89b8f6b71e5987ef901734b6bc\"},{\"name\":\"__remember_me\",\"value\":\"true\"}]");
        //api.sign();
    }

}
