package com.cms.setting;

import java.io.File;
import java.util.Scanner;
import org.json.*;

import com.cms.core.CmsConfig;
import com.cms.ecs.EcsConfig;
import com.cms.notif.Email;
import com.cms.notif.Sms;

public class Configuration {

	private static final Logger logger = new Logger();
	
	private static final String propertyPath = "config"+File.separator+"property.conf";
	private static final String notifPath = "config"+File.separator+"notif.conf";
	private static String propertyData;
	private static String notifData;
	private JSONObject jsonParse;

	public Configuration() {
		try {
			propertyData = readFileAsString(propertyPath);
			notifData = readFileAsString(notifPath);
		} catch (Exception e) {
			logger.error("com.cms.setting.Configuration Initialized fail. " + e.toString());
		}
	}

	public float getNotifQuota1() {
		try {
			jsonParse = new JSONObject(notifData);
			return jsonParse.getFloat("notifQuota1");
		} catch (Exception e) {
			return -1;
		}
	}
	
	public float getNotifQuota2() {
		try {
			jsonParse = new JSONObject(notifData);
			return jsonParse.getFloat("notifQuota2");
		} catch (Exception e) {
			return -1;
		}
	}
	
	public float getNotifRangeByDay() {
		try {
			jsonParse = new JSONObject(notifData);
			return jsonParse.getFloat("rangeByDay");
		} catch (Exception e) {
			return 1;
		}
	}
	
	public Email getEmailConfig() {
		Email email = new Email();
		try {
			jsonParse = new JSONObject(notifData);
			email.setEnable(jsonParse.getJSONArray("emailConfig").getJSONObject(0).getBoolean("mailEnable"));
			email.setBody(jsonParse.getJSONArray("emailConfig").getJSONObject(0).getString("mailBody"));
		} catch (Exception e) {
			return null;
		}
		return email;
	}
	
	public Sms getSmsConfig() {
		Sms sms = new Sms();
		try {
			jsonParse = new JSONObject(notifData);
			sms.setEnable(jsonParse.getJSONArray("smsConfig").getJSONObject(0).getBoolean("smsEnable"));
			sms.setBody(jsonParse.getJSONArray("smsConfig").getJSONObject(0).getString("smsBody"));
		} catch (Exception e) {
			return null;
		}
		return sms;
	}
	
	public EcsConfig getEcsConfig() {
		EcsConfig ecsAuthen = new EcsConfig();
		try {
			jsonParse = new JSONObject(propertyData);
			ecsAuthen.setEndPoint(jsonParse.getJSONArray("ecsSignin").getJSONObject(0).getString("endPonit"));
			ecsAuthen.setUserName(jsonParse.getJSONArray("ecsSignin").getJSONObject(0).getString("userName"));
			ecsAuthen.setPassword(jsonParse.getJSONArray("ecsSignin").getJSONObject(0).getString("password"));
		} catch (Exception e) {
			return null;
		}
		return ecsAuthen;
	}

	public CmsConfig getCmsConfig() {
		CmsConfig cmsConfig = new CmsConfig();
		try {
			jsonParse = new JSONObject(propertyData);
			cmsConfig.setDbUrl(jsonParse.getJSONArray("cmsDb").getJSONObject(0).getString("dbUrl"));
			cmsConfig.setDbUsername(jsonParse.getJSONArray("cmsDb").getJSONObject(0).getString("dbUsername"));
			cmsConfig.setDbPassword(jsonParse.getJSONArray("cmsDb").getJSONObject(0).getString("dbPassword"));
			cmsConfig.setDbSid(jsonParse.getJSONArray("cmsDb").getJSONObject(0).getString("dbSid"));
		} catch (Exception e) {
			return null;
		}
		return cmsConfig;
	}
	
	public String readFileAsString(String filePath) {
		String data = "";
		File file = new File(filePath);
		if (file.exists()) {
			try {
				Scanner myReader = new Scanner(file);
				while (myReader.hasNextLine()) {
					data += myReader.nextLine();
				}
				myReader.close();
			} catch (Exception e) {
				System.err.println("Read file error: " + e.toString());
			}
		}
		return data;
	}
}
