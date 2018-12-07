package com.cignacmb.iuss.web.common.hmcUtil;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 通用HTTP工具管理类
 * @package com.cignacmb.member.service.util
 * @class HttpUtil
 * @author Jeck
 * @createdate 2016年4月28日 下午2:36:51
 */
public class HttpUtil {
	
	private HttpUtil(){}
	
	/**
	 * 日志对象
	 */
	private static final Logger logger = Logger.getLogger(HttpUtil.class);
	
	/**
	 * 默认字符集编码
	 */
	private static final String DEFAULT_CHARSET = "UTF-8";
	
	/**
	 * 设置请求连接超时时间(设置请求超时100秒钟)
	 */
	private static final int CONNECTION_REQUEST_TIMEOUT = 100 * 1000;
	
	/**
	 * 设置套接字连接超时时间(设置等待数据超时时间100秒钟)
	 */
	private static final int  SOCKET_TIMEOUT = 100 * 1000;
	
	/**
	 * 设置等待连接超时时间(设置等待数据超时时间100秒钟)
	 */
	private static final int CONNECTION_TIMEOUT = 100 * 1000;
	
	/**
	 * 采用Get方式发送请求
	 * @param url 请求URL
	 * @param params 请求参数
	 * @return 服务器端响应数据
	 * @creator MeiGaoBang
	 * @createdate 2015-5-17 下午9:05:29
	 */
	public static String sendHttpGet(String url, NameValuePair... params) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for(NameValuePair nameValuePair:params){
			list.add(nameValuePair);
		}
		return sendHttpGet(url,list);
	}
	
	/**
	 * 采用Get方式发送请求
	 * @param url 请求URL
	 * @param params 请求参数
	 * @return 服务器端响应数据
	 * @creator MeiGaoBang
	 * @createdate 2015-5-17 下午9:05:29
	 */
	public static String sendHttpGet(String url, Map<String,String> params) {
		return sendHttpGet(url,buildNameValuePair(params));
	}
	
	/**
	 * 采用GET方式请求
	 * @param requestUrl 请求URL
	 * @return 将服务器响应的信息转换后的JSONObject对象
	 * @creator MeiGaoBang
	 * @createdate 2015-5-15 下午4:49:04
	 */
	public static String sendHttpGet(String requestUrl, List<NameValuePair> params) {
		logger.info("=========采用GET方式发送HTTP请求开始=========");
		String result = null;
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		try{
			if(isNotNullList(params)){
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, DEFAULT_CHARSET);
				requestUrl += "?" + EntityUtils.toString(entity);
			}
			HttpGet httpGet = new HttpGet(requestUrl);
			setRequestConfig(httpGet);
			response = httpClient.execute(httpGet);
			result = getResult(response);
		}catch(ClientProtocolException e){
			logger.error("=========Http通信过程中客户端通信协议错误=========",e);
		}catch(ParseException e){
			logger.error("=========Http通信过程中数据解析错误=========",e);
		}catch(SocketTimeoutException e){
			logger.error("=========Http通信请求超时=========",e);
		}catch(IOException e){
			logger.error("=========Http通信过程中数据流输入输出错误=========",e);
		}finally{
			if(null!=response){
				try{
					response.close();
				}catch(IOException e){
					logger.error("=========关闭HttpResponse对象过程中数据流输入输出错误=========",e);
				}
			}
			try{
				httpClient.close();
			}catch(IOException e){
				logger.error("=========关闭HttpClient对象过程中数据流输入输出错误=========",e);
			}
		}
		logger.info("=========采用GET方式发送HTTP请求结束=========");
		return result;
	}
	
	/**
	 * 采用Post方式发送请求
	 * @param url 请求URL
	 * @param params 请求参数
	 * @return 服务器端响应数据
	 * @creator MeiGaoBang
	 * @createdate 2015-5-17 下午9:05:29
	 */
	public static String sendHttpPost(String url, NameValuePair... params) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for(NameValuePair nameValuePair:params){
			list.add(nameValuePair);
		}
		return sendHttpPost(url,list);
	}
	
	/**
	 * 采用Post方式发送请求
	 * @param url 请求URL
	 * @param params 请求参数
	 * @return 服务器端响应数据
	 * @creator MeiGaoBang
	 * @createdate 2015-5-17 下午9:05:29
	 */
	public static String sendHttpPost(String url, Map<String,String> params) {
		return sendHttpPost(url,buildNameValuePair(params));
	}
	
	/**
	 * 采用Post方式发送请求
	 * @param requestUrl 请求URL
	 * @param params 请求参数
	 * @return 服务器端响应数据
	 * @creator MeiGaoBang
	 * @createdate 2015-5-15 下午4:56:55
	 */
	public static String sendHttpPost(String requestUrl, List<NameValuePair> params) {
		logger.info("=========采用POST方式发送HTTP请求开始=========");
		String result = null;
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		try{
			HttpPost httpPost = new HttpPost(requestUrl);
			if(isNotNullList(params)){
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,DEFAULT_CHARSET);
				httpPost.setEntity(entity);
			}
			setRequestConfig(httpPost);
			response = httpClient.execute(httpPost);
			result = getResult(response);
		}catch(ClientProtocolException e){
			logger.error("=========Http通信过程中客户端通信协议错误=========",e);
		}catch(ParseException e){
			logger.error("=========Http通信过程中数据解析错误=========",e);
		}catch(SocketTimeoutException e){
			logger.error("=========Http通信请求超时=========",e);
		}catch(IOException e){
			logger.error("=========Http通信过程中数据流输入输出错误=========",e);
		}finally{
			if(null!=response){
				try{
					response.close();
				}catch(IOException e){
					logger.error("=========关闭HttpResponse对象过程中数据流输入输出错误=========",e);
				}
			}
			try{
				httpClient.close();
			}catch(IOException e){
				logger.error("=========关闭HttpClient对象过程中数据流输入输出错误=========",e);
			}
		}
		logger.info("=========采用POST方式发送HTTP请求结束=========");
		return result;
	}
	
	/**
	 * 通过GET方式发送HTTPS请求
	 * @param requestUrl 请求路径
	 * @return 响应得到的数据
	 * @creator MeiGaoBang
	 * @createdate 2015-5-14 下午11:34:59
	 */
	public static String sendHttpsGet(String requestUrl) {
		String result = null;
		HttpGet httpGet = null;
		try{
			SSLContextBuilder builder = SSLContexts.custom().useTLS();
			TrustStrategy strategy = new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;//信任所有
				}
			};
			SSLContext sslContext = builder.loadTrustMaterial(null, strategy).build();
			SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
			CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
			httpGet = new HttpGet(requestUrl);
			setRequestConfig(httpGet);
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			result = EntityUtils.toString(entity,DEFAULT_CHARSET);
		}catch(KeyManagementException e){
			logger.error("Https通信过程中密钥错误",e);
		}catch(NoSuchAlgorithmException e){
			logger.error("Https通信过程中算法不存在",e);
		}catch(KeyStoreException e){
			logger.error("Https通信过程中密钥错误",e);
		}catch(ClientProtocolException e){
			logger.error("Https通信过程中客户端通信协议错误",e);
		}catch(ParseException e){
			logger.error("Https通信过程中数据解析错误",e);
		}catch(IOException e){
			logger.error("Https通信过程中数据流输入输出错误",e);
		}
		return result;
	}
	
	/**
	 * 采用Get方式发送请求
	 * @param url 请求URL
	 * @param params 请求参数
	 * @return 服务器端响应数据
	 * @creator MeiGaoBang
	 * @createdate 2015-5-17 下午9:05:29
	 */
	public static String sendHttpsGet(String url, Map<String,String> params) {
		return sendHttpsGet(url,buildNameValuePair(params));
	}
	
	/**
	 * 通过GET方式发送请求
	 * @param requestUrl 请求路径
	 * @return 响应得到的数据
	 * @creator MeiGaoBang
	 * @createdate 2015-5-14 下午11:34:59
	 */
	public static String sendHttpsGet(String requestUrl, List<NameValuePair> params) {
		String result = null;
		HttpGet httpGet = null;
		try{
            X509TrustManager x509mgr = new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] xcs, String string) {}
                public void checkServerTrusted(X509Certificate[] xcs, String string) {}
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null,new TrustManager[]{x509mgr},null);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            CloseableHttpClient httpclient  = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            if(isNotNullList(params)){
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, DEFAULT_CHARSET);
				requestUrl += "?" + EntityUtils.toString(entity);
			}
            httpGet = new HttpGet(requestUrl);
            setRequestConfig(httpGet);
            CloseableHttpResponse httpResponse = httpclient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity, DEFAULT_CHARSET);
		}catch(KeyManagementException e){
			logger.error("Https通信过程中密钥错误",e);
		}catch(NoSuchAlgorithmException e){
			logger.error("Https通信过程中算法不存在",e);
		}catch(ClientProtocolException e){
			logger.error("Https通信过程中客户端通信协议错误",e);
		}catch(ParseException e){
			logger.error("Https通信过程中数据解析错误",e);
		}catch(IOException e){
			logger.error("Https通信过程中数据流输入输出错误",e);
		}finally{
			if(null!=httpGet)
				httpGet.abort();
		}
		return result;
	}
	
	/**
	 * 通过POST方式发送HTTPS请求<br>
	 */
	public static String sendHttpsPost(String requestUrl, Map<String, String> params) {
		return sendHttpsPost(requestUrl,buildNameValuePair(params));
	}
	
	/**
	 * 通过POST方式发送HTTPS请求<br>
	 */
	public static String sendHttpsPost(String requestUrl, List<NameValuePair> params) {
		String result = null;
		HttpPost httpPost = null;
		SSLContext sslContext = null;
		SSLConnectionSocketFactory sslsf = null;
		try{
			X509TrustManager x509mgr = new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] xcs, String string) {}
                public void checkServerTrusted(X509Certificate[] xcs, String string) {}
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null,new TrustManager[]{x509mgr},null);
            sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
	        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECTION_TIMEOUT).build();             
	        httpPost = new HttpPost(requestUrl);
	        httpPost.setConfig(requestConfig);
	        if(isNotNullList(params)){
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,DEFAULT_CHARSET);
				httpPost.setEntity(entity);
			}
	        HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, DEFAULT_CHARSET);
		}catch(KeyManagementException e){
			logger.error("Https通信过程中密钥错误",e);
		}catch(NoSuchAlgorithmException e){
			logger.error("Https通信过程中算法不存在",e);
		}catch(ClientProtocolException e){
			logger.error("Https通信过程中客户端通信协议错误",e);
		}catch(ParseException e){
			logger.error("Https通信过程中数据解析错误",e);
		}catch(IOException e){
			logger.error("Https通信过程中数据流输入输出错误",e);
		}finally{
			if(null!=httpPost) httpPost.abort();
		}
		return result;
	}
	
	/**
	 * 通过POST方式发送HTTPS请求<br>
	 * 默认不需要服务器证书 请求地址
	 * @param param 字符串参数
	 * @param xmlObject
	 * @return
	 * @throws Exception
	 */
	public static String sendHttpsPost(String requestUrl, String param) throws Exception {
		return sendHttpsPost(requestUrl, param, false);
	}
	
	/**
	 * 通过POST方式发送HTTPS请求<br>
	 * 默认不需要服务器证书 请求地址
	 * @param requestUrl 请求地址
	 * @param param 字符串参数
	 * @param hasCert 是否需要服务器证书 true-需要，false-不需要
	 * @return 响应得到的数据
	 */
	public static String sendHttpsPost(String requestUrl, String param, Boolean hasCert) throws Exception {
		return sendHttpsPost(requestUrl, param, false, null, null);
	}
	
	/**
	 * 通过POST方式发送HTTPS请求
	 * @param requestUrl 请求地址
	 * @param param 字符串参数
	 * @param hasCert 是否需要服务器证书 true-需要，false-不需要
	 * @param certLocalPath 证书路径  如果hasCert为true，则必传
	 * @param certPassword 证书密码 如果hasCert为true，则必传
	 * @return 响应得到的数据
	 */
	public static String sendHttpsPost(String requestUrl, String param, Boolean hasCert, String certLocalPath, String certPassword) throws Exception {
		String result = null;
		HttpPost httpPost = null;
		FileInputStream instream = null;
		SSLContext sslContext = null;
		SSLConnectionSocketFactory sslsf = null;
		try{
			if(hasCert){
				KeyStore keyStore = KeyStore.getInstance("PKCS12");
				//加载本地的证书进行https加密传输
				logger.info("安全证书完整路径为："+certLocalPath+"，证书登录密码为："+certPassword);
				instream = new FileInputStream(new File(certLocalPath));
				//设置证书密码
				keyStore.load(instream, certPassword.toCharArray());
				sslContext = SSLContexts.custom().loadKeyMaterial(keyStore, certPassword.toCharArray()).build();
				sslsf = new SSLConnectionSocketFactory(sslContext,new String[]{"TLSv1"},null,SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			}else{
				X509TrustManager x509mgr = new X509TrustManager(){
	                public void checkClientTrusted(X509Certificate[] xcs, String string) {}
	                public void checkServerTrusted(X509Certificate[] xcs, String string) {}
	                public X509Certificate[] getAcceptedIssuers() {
	                    return null;
	                }
	            };
	            sslContext = SSLContext.getInstance("TLS");
	            sslContext.init(null,new TrustManager[]{x509mgr},null);
	            sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			}
	        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
	        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECTION_TIMEOUT).build();             
	        httpPost = new HttpPost(requestUrl);
	        httpPost.setConfig(requestConfig);
	        StringEntity postEntity = new StringEntity(param, DEFAULT_CHARSET);
	        httpPost.setEntity(postEntity);
	        HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, DEFAULT_CHARSET);
		}catch(KeyManagementException e){
			logger.error("Https通信过程中密钥错误",e);
		}catch(NoSuchAlgorithmException e){
			logger.error("Https通信过程中算法不存在",e);
		}catch(ClientProtocolException e){
			logger.error("Https通信过程中客户端通信协议错误",e);
		}catch(ParseException e){
			logger.error("Https通信过程中数据解析错误",e);
		}catch(IOException e){
			logger.error("Https通信过程中数据流输入输出错误",e);
		}finally{
			try{
				if(null!=instream)
					instream.close();
			}catch(IOException e){
				logger.error("Https通信过程中数据流输入输出错误",e);
			}
			if(null!=httpPost)
				httpPost.abort();
		}
		return result;
	}
	
	/**
	 * 设置请求连接超时相关信息
	 * @param httpPost HttpPost对象
	 * @creator MeiGaoBang
	 * @createdate 2015-5-17 下午9:40:08
	 */
	public static void setRequestConfig(HttpPost httpPost) {
		Builder builder = RequestConfig.custom();
		builder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT);
		builder.setConnectTimeout(CONNECTION_TIMEOUT);
		builder.setSocketTimeout(SOCKET_TIMEOUT);
		httpPost.setConfig(builder.build());
	}
	
	/**
	 * 设置请求连接超时相关信息
	 * @param httpGet HttpGet对象
	 * @creator MeiGaoBang
	 * @createdate 2015-5-17 下午9:41:10
	 */
	public static void setRequestConfig(HttpGet httpGet) {
		Builder builder = RequestConfig.custom();
		builder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT);
		builder.setConnectTimeout(CONNECTION_TIMEOUT);
		builder.setSocketTimeout(SOCKET_TIMEOUT);
		httpGet.setConfig(builder.build());
	}
	
	/**
	 * 获取服务器端数据
	 * @param response 服务器响应对象
	 * @return 服务器端响应数据
	 * @throws ParseException 
	 * @throws IOException
	 * @creator MeiGaoBang
	 * @createdate 2015-5-17 下午8:59:57
	 */
	public static String getResult(CloseableHttpResponse response) throws ParseException, IOException {
		String result = null;
		StatusLine statusLine = response.getStatusLine();
		if(statusLine.getStatusCode() == HttpStatus.SC_OK){
			HttpEntity httpEntity = response.getEntity();
			if(null!=httpEntity){
				result = EntityUtils.toString(response.getEntity());
				logger.info("=========请求成功，返回的信息为："+result);
			}else{
				logger.info("=========请求成功，但没有响应信息返回=========");
			}
		}else{
		    logger.error("=========请求失败，状态响应信息为："+statusLine+"=========");
		}
		return result;
	}
	
	/**
	 * Map转换为键值对
	 * @param params 参数集合
	 * @return NameValuePair类型集合
	 * @creator MeiGaoBang
	 * @createdate 2015-5-17 下午9:09:45
	 */
	public static List<NameValuePair> buildNameValuePair(Map<String,String> params) {
		List<NameValuePair> pariList = new ArrayList<NameValuePair>();
		if(null!=params && params.size()>0){
			for(String key : params.keySet()){
				pariList.add(new BasicNameValuePair(key,params.get(key)));
			}
		}
		return pariList;
	}
	
	@SuppressWarnings("rawtypes")
	private static final boolean isNotNullList(List list){
		return null!=list && list.size()>0;
	}
	
	/**
  * 采用Get方式发送请求
  * @param url
  * @param params
  * @return
  */
 public static String sendGet(String url, Map<String, String> params) {
     if(null==url || "".equals(url)){
         return null;
     }
     if(url.startsWith("http")){
         return sendHttpGet(url, params);
     }else if(url.startsWith("https")){
         return sendHttpsGet(url, params);
     }
     return null;
 }
 
 /**
  * 采用Post方式发送请求
  * @param url
  * @param params
  * @return
  */
 public static String sendPost(String url, Map<String, String> params) {
     if(null==url || "".equals(url)){
         return null;
     }
     if(url.startsWith("http")){
         return sendHttpPost(url, params);
     }else if(url.startsWith("https")){
         return sendHttpsPost(url, params);
     }
     return null;
 }
	
}
