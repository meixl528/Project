package com.ssm.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * 
 * @name HttpUtil
 * @description Http请求工具类
 */
public class HttpUtil {
	/**
	 * 默认字符集
	 */
	private static final String DEF_CHATSET = "UTF-8";
	/**
	 * 默认连接超时时间
	 */
	private static final int DEF_CONN_TIMEOUT = 30000;
	/**
	 * 默认数据读取超时时间
	 */
	private static final int DEF_READ_TIMEOUT = 30000;
	
	

	/**
	 * @name HttpMethod
	 * @description 请求类型 
	 */
	public enum HttpMethod {
		GET("GET"), POST("POST");

		private String value;

		private HttpMethod(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * 默认CONTENT_TYPE
	 */
	private static final String DEF_CONTENT_TYPE = "application/xml";
	
	/**
	 * 默认域名验证器<br/>
	 * 决定了在<b>https</b>请求域名与服务器认证的域名不一致时是否继续<br/>
	 * 默认<b>继续</b>
	 */
	private static final HostnameVerifier DEF_HOST_NAME_VERIFIER = new HostnameVerifier() {
		@Override
		public boolean verify(String urlHostName, SSLSession session) {
			return true;
		}
	};
	
	/**
	 * http协议地址头
	 */
	private static final String HTTP_PROTOCOL_TYPE = "http://";
	
	/**
	 * https协议地址头
	 */
	private static final String HTTPS_PROTOCOL_TYPE = "https://";

	/**
	 * 此方法很早就停止维护了,BUG很多,请使用另一个同名函数
	 * @param strUrl
	 *            URL地址
	 * @param params
	 *            参数
	 * @param method
	 *            请求类型
	 * @param contenttype
	 *            请求内容格式
	 * @param cookies
	 *            cookies
	 * @return 返回报文体，状态码，cookie
	 * @throws Exception
	 */
	@Deprecated
	public static Map<String, Object> net(String strUrl, String params, String method, String contenttype,
			String cookies) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		String rs = null;
		// try {
		StringBuffer sb = new StringBuffer();
		URL url = new URL(strUrl.replaceAll(" ", "%20"));
		conn = (HttpURLConnection) url.openConnection();
		HttpURLConnection.setFollowRedirects(false);
		if (conn == null)
			throw new NullPointerException("Fault to create Connection");
		if (method == null || "GET".equals(method)) {
			conn.setRequestMethod("GET");
		} else {
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
		}
		if (cookies != null) {
			conn.setRequestProperty("Cookie", cookies);
		}
		conn.setRequestProperty("Content-Type", contenttype != null ? contenttype : DEF_CONTENT_TYPE);
		conn.setUseCaches(false);
		conn.setConnectTimeout(DEF_CONN_TIMEOUT);
		conn.setReadTimeout(DEF_READ_TIMEOUT);
		conn.setInstanceFollowRedirects(false);
		conn.connect();
		if (params != null && method.equals("POST")) {
			try {
				DataOutputStream out = new DataOutputStream(conn.getOutputStream());
				// Java中的char是16位的，一个char存储一个中文字符，直接用writeBytes方法转换会变为8位，直接导致高8位丢失。从而导致中文乱码。
				out.write(params.getBytes());
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		InputStream is = conn.getInputStream();
		reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
		String strRead = null;
		while ((strRead = reader.readLine()) != null) {
			sb.append(strRead);
		}

		rs = sb.toString();
		map.put("result", rs);
		map.put("statusCode", conn.getResponseCode());
		map.put("cookie", conn.getHeaderField("set-cookie"));

		if (reader != null) {
			reader.close();
		}
		if (conn != null) {
			conn.disconnect();
		}
		return map;
	}

	/**
	 * 将map型转为请求参数型,GET请求时很有用
	 * @param data
	 *            map型参数
	 * @return 拼接好的URL参数
	 */
	public static String urlEncode(Map<String, String> data) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : data.entrySet()) {
			try {
				sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue() + "", "UTF-8"))
						.append("&");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		String res = sb.toString();
		if (res.length() > 0) {
			res = res.substring(0, res.length() - 1);
		}
		return res;
	}
	
	/**
	 * 发送报文，接收结果
	 * @param urlStr
	 *            String类型的url地址，请一定要带上协议类型(http:// or https://)不然会报错
	 * @param data
	 *            发送的数据，可null
	 * @param httpMethod
	 *            method
	 * @param headArg
	 *            Http请求头部的参数，键值对形式
	 * @return 返回报文体(result)，状态码(statusCode)，cookie(cookie)，键值对形式
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 * @throws IOException 
	 * @throws Exception 
	 */
	public static Map<String, Object> net(String urlStr, String data, HttpMethod httpMethod,
			Map<String, String> headArg) throws KeyManagementException, NoSuchAlgorithmException, IOException{
		boolean isHttp = (urlStr.indexOf(HttpUtil.HTTP_PROTOCOL_TYPE) == 0);
		boolean isHttps = (urlStr.indexOf(HttpUtil.HTTPS_PROTOCOL_TYPE) == 0);
		if(!isHttp && !isHttps){
			throw new IOException("Protocol type (http:// or https://) is missing");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		String rs = null;
		StringBuffer sb = new StringBuffer();
		URL url = new URL(urlStr.replaceAll(" ", "%20"));
		if(isHttps){
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(DEF_HOST_NAME_VERIFIER);
		}
		conn = (HttpURLConnection) url.openConnection();
		HttpURLConnection.setFollowRedirects(false);
		if (conn == null) {
			throw new NullPointerException("Fault to create Connection");
		}

		httpMethod = httpMethod == null ? HttpMethod.GET : httpMethod;
		conn.setRequestMethod(httpMethod.getValue());
		if (httpMethod == HttpMethod.POST) {
			conn.setDoOutput(true);
		}

		if (headArg != null) {
			for (Entry<String, String> entry : headArg.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}

		if (conn.getRequestProperty("Content-Type") == null) {
			conn.setRequestProperty("Content-Type", DEF_CONTENT_TYPE);
		}
		conn.setUseCaches(false);
		conn.setConnectTimeout(DEF_CONN_TIMEOUT);
		conn.setReadTimeout(DEF_READ_TIMEOUT);
		conn.setInstanceFollowRedirects(false);
		conn.connect();
		if (data != null && httpMethod == HttpMethod.POST) {
			try {
				DataOutputStream out = new DataOutputStream(conn.getOutputStream());
				// Java中的char是16位的，一个char存储一个中文字符，直接用writeBytes方法转换会变为8位，直接导致高8位丢失。从而导致中文乱码。
				out.write(data.getBytes());
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		InputStream is = conn.getInputStream();
		reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
		String strRead = null;
		while ((strRead = reader.readLine()) != null) {
			sb.append(strRead);
		}

		rs = sb.toString();
		map.put("result", rs);
		map.put("statusCode", conn.getResponseCode());
		map.put("cookie", conn.getHeaderField("set-cookie"));

		if (reader != null) {
			reader.close();
		}
		if (conn != null) {
			conn.disconnect();
		}
		return map;
	}


	private static void trustAllHttpsCertificates() throws NoSuchAlgorithmException, KeyManagementException{
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}

}
