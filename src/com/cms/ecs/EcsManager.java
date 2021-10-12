package com.cms.ecs;

import java.io.IOException;
import java.util.Base64;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;

import com.cms.setting.Configuration;
import com.cms.setting.Logger;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @author ThangDv@viettelidc.com.vn
 */
public class EcsManager {

	private static final Logger logger = new Logger();
	private static final int RETRY = 3;
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	private static String endPoint;
	private static Configuration CON;
	private static EcsConfig ecsConfig;
	private static String tokent;

	private JSONObject jsonParse;

	public EcsManager() {
		CON = new Configuration();
		ecsConfig = CON.getEcsConfig();
		endPoint = ecsConfig.getEndPoint();
		setTokent();
	}

	public EcsBilling getEcsBilling(String namespace) {
		return getEcsBilling(namespace, false);
	}

	public EcsBilling getEcsBilling(String namespace, boolean getQuata) {
		EcsBilling ecsBilling = new EcsBilling();
		String billing = getBillingNamespace(namespace);
		if (billing == null)
			return null;
		try {
			jsonParse = new JSONObject(billing);
			ecsBilling.setNamespace(namespace);
			ecsBilling.setSample_time(jsonParse.getString("sample_time"));
			ecsBilling.setTotal_size(jsonParse.getString("total_size"));
			ecsBilling.setTotal_size_unit(jsonParse.getString("total_size_unit"));
			ecsBilling.setTotal_objects(jsonParse.getInt("total_objects"));
			ecsBilling.setUptodate_till(jsonParse.getString("uptodate_till"));
			ecsBilling.setTotal_mpu_size(jsonParse.getString("total_mpu_size"));
			ecsBilling.setTotal_mpu_parts(jsonParse.getInt("total_mpu_parts"));
			ecsBilling.setTotal_objects_deleted(jsonParse.getString("total_objects_deleted"));
			ecsBilling.setTotal_size_deleted(jsonParse.getString("total_size_deleted"));
			if (!getQuata)
				return ecsBilling;
			String quota = getQuotaNamespace(namespace);
			if (quota == null)
				return ecsBilling;
			jsonParse = new JSONObject(quota);
			ecsBilling.setBlockSize(jsonParse.getFloat("blockSize"));
			ecsBilling.setNotificationSize(jsonParse.getFloat("notificationSize"));
			return ecsBilling;
		} catch (Exception e) {
			return null;
		}
	}

	public String getQuotaNamespace(String namespace) {
		String path = "object/namespaces/namespace/" + namespace + "/quota.json";
		Response response = null;
		try {
			response = getApi(path);
			if (response == null) {
				return null;
			}
			return response.body().string();
		} catch (Exception e) {
			logger.error("getQuota fail.\n" + e.toString());
			return null;
		}
	}

	public String getListUser() {
		String path = "object/users.json";
		Response response = null;
		try {
			response = getApi(path);
			if (response == null) {
				return null;
			}
			return response.body().string();
		} catch (Exception e) {
			logger.error("getListUser fail.\n" + e.toString());
			return null;
		}
	}

	public String getListBucket(String namespace) {
		Response response = getApi("object/bucket?namespace=" + namespace);
		try {
			return response.body().string();
		} catch (IOException e) {
			return null;
		}
	}

	public String getCapacity() {
		String path = "object/capacity.json";
		Response response = null;
		try {
			response = getApi(path);
			if (response == null) {
				return null;
			}
			return response.body().string();
		} catch (Exception e) {
			logger.error("getCapacity fail.\n" + e.toString());
			return null;
		}
	}

	public String getBillingNamespace(String namespace) {
		String path = "object/billing/namespace/" + namespace + "/info.json";
		Response response = null;
		try {
			response = getApi(path);
			if (response == null) {
				return null;
			}
			return response.body().string();
		} catch (Exception e) {
			logger.error("getBillingNamespace fail.\n" + e.toString());
			return null;
		}
	}

	public String getBillingBucket(String namespace, String bucketname) {
		String path = "object/billing/buckets/" + namespace + "/" + bucketname + "/info.json";
		Response response = null;
		try {
			response = getApi(path);
			if (response == null) {
				return null;
			}
			return response.body().string();
		} catch (Exception e) {
			logger.error("getBillingBucket fail.\n" + e.toString());
			return null;
		}
	}

	// Token
	private void setTokent() {
		if (tokent != null && !tokent.equals("")) {
			return;
		}
		tokent = getTokent();
	}

	private String getTokent() {
		logger.info("renew token");
		String path = "login/";
		String username = ecsConfig.getUserName();
		String password = ecsConfig.getPassword();
		String urlRequest = endPoint + path;
		Response response;
		try {
			OkHttpClient restClient = createOkHttpClient();
			Request request = new Request.Builder().url(urlRequest)
					.addHeader("Content-type", "application/x-www-form-urlencoded")
					.addHeader("Authorization", "Basic " + base64Encode(username, password)).build();
			for (int i = 0; i < RETRY; i++) {
				response = restClient.newCall(request).execute();
				if (response.code() == 200) {
					logger.info("User " + username + " login ecs successful.");
					tokent = response.header("X-SDS-AUTH-TOKEN", "");
					return tokent;
				}
				logger.info("User " + username + " login ecs fail with status code: " + response.code());
			}
		} catch (Exception e) {
			logger.info("User " + username + " login ecs fail.\n" + e.toString());
			return null;
		}
		return null;
	}

	// Http request
	private Response getApi(String path) {
		String urlRequest = endPoint + path;
		OkHttpClient restClient = createOkHttpClient();
		try {
			Request request = new Request.Builder().url(urlRequest).header("Content-type", "application/json")
					.header("X-SDS-AUTH-TOKEN", tokent).build();
			Response response;
			for (int i = 0; i < RETRY; i++) {
				response = restClient.newCall(request).execute();
				if (response.code() == 200) {
					return response;
				}
				logger.error("getApi " + urlRequest + " fail with status code: " + response.code());
				if (response.code() == 401) {
					tokent = "";
					setTokent();
				}
			}
		} catch (Exception e) {
			logger.error("getApi " + urlRequest + " fail.\n" + e.toString());
			return null;
		}
		return null;
	}

	private Response postApi(String path, String json) {
		String urlRequest = endPoint + path;
		OkHttpClient restClient = createOkHttpClient();
		try {
			RequestBody body = RequestBody.create(JSON, json);
			Request request = new Request.Builder().url(urlRequest).post(body)
					.header("Content-type", "application/json").header("X-SDS-AUTH-TOKEN", tokent).build();
			Response response;
			for (int i = 0; i < RETRY; i++) {
				response = restClient.newCall(request).execute();
				if (response.code() == 200) {
					return response;
				}
				logger.error("getApi " + urlRequest + " fail with status code: " + response.code());
				if (response.code() == 401) {
					tokent = "";
					setTokent();
				}
			}
		} catch (Exception e) {
			logger.error("getApi " + urlRequest + " fail.\n" + e.toString());
			return null;
		}
		return null;
	}

	private OkHttpClient createOkHttpClient() {
		OkHttpClient restClient = new OkHttpClient();
		try {
			restClient = new OkHttpClient.Builder().hostnameVerifier((String string, SSLSession ssls) -> true)
					.sslSocketFactory(getSSLConfig().getSocketFactory(), getTrustManager()).build();

		} catch (Exception e) {
			logger.error("createOkHttpClient fail.\n" + e.toString());
		}
		return restClient;
	}

	private String base64Encode(String username, String password) {
		String baseEncode = username + ":" + password;
		byte[] encodedBytes = Base64.getEncoder().encode(baseEncode.getBytes());
		return new String(encodedBytes);
	}

	private X509TrustManager getTrustManager() {

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}
		} };
		return (X509TrustManager) trustAllCerts[0];
	}

	private SSLContext getSSLConfig() throws CertificateException, IOException, KeyStoreException,
			NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException {

		String keyStoreType = KeyStore.getDefaultType();
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(null, null);
		MySSLSocketFactory sf = new MySSLSocketFactory(keyStore);
		sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		return sf.getSslContext();
	}

}
