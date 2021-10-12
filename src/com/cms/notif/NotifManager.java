package com.cms.notif;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.json.JSONObject;

import com.cms.core.CmsUser;
import com.cms.core.CmsUserManagerment;
import com.cms.ecs.EcsBilling;
import com.cms.setting.Configuration;
import com.cms.setting.Logger;
import com.opencsv.CSVWriter;

public class NotifManager {

	private static final Logger logger = new Logger();
	private static final String reportPath = "report" + File.separator;
	private static final DecimalFormat df = new DecimalFormat("0.00");
	private static final String emailKey1 = "EmailNotifQ1:";
	private static final String emailKey2 = "EmailNotifQ2:";
	private static final String smsKey1 = "SmsNotifQ1:";
	private static final String smsKey2 = "SmsNotifQ2:";

	private static Configuration CON;
	private static Email email;
	private static Sms sms;

	private float notifQ1;
	private float notifQ2;

	private static String record = "";

	public NotifManager() {
		CON = new Configuration();
		email = CON.getEmailConfig();
		sms = CON.getSmsConfig();
		notifQ1 = CON.getNotifQuota1();
		notifQ2 = CON.getNotifQuota2();
	}

	public void pushEmail(CmsUser user, float usedRate, Boolean isAlertHigh) {
		JSONObject body = createEmailBody(user, usedRate);
		boolean isPushed = false;
		boolean isSentOnToday = false;
		if(!isAlertHigh) { 
			// nQ1 <= rate <= nQ2
			if(!isSentOnToday(emailKey1, user)) {
				//send email
				System.out.println("Sending....\n"+ body);
				if(true) {
				isPushed = true;
				recordLog(emailKey1, user, usedRate);
				}
			} else 
				isSentOnToday = true;
		} else { 
			// rate >= nQ2
			if(!isSentOnToday(emailKey2, user)) {
				//send email
				System.out.println("Sending....\n"+ body);
				if(true) {
				isPushed = true;
				recordLog(emailKey2, user, usedRate);
				}
			}
			else 
				isSentOnToday = true;
		}
		
		if(isSentOnToday) {
			logger.info("Push mail. Ignore send again. " + user.getEMAIL());
			System.out.println("Push mail. Ignore send again. " + user.getEMAIL());
		} else if(!isPushed) {
			logger.info("Push mail. " + user.getEMAIL() + " false.");
		}
		
	}

	private boolean isSentOnToday(String key, CmsUser user) {
		return record.contains(key + user.getEMAIL());
	}

	private void recordLog(String key, CmsUser user, float usedRate) {
		logger.record(key + user.getEMAIL() + " successfully. Used(%): " +usedRate);
		logger.info("Push mail. " + key + user.getEMAIL() + " successfully. Used(%): " +usedRate);
		System.out.println("Push mail. " + key + user.getEMAIL() + " successfully. Used(%): " +usedRate);
	}

	public JSONObject createEmailBody(CmsUser user, float usedRate) {
		JSONObject body = new JSONObject();
		String bodyContent = email.getBody().replace("{total_size}", user.getBILLING().getTotal_size())
				.replace("{total_size_unit}", "GB").replace("{notificationQuota}", df.format(usedRate));
		body.put("body", bodyContent);
		return body;
	}

	public void pushNotify() {
		if (notifQ1 == -1 || (!email.isEnable() && !sms.isEnable())) {
			System.out.println("Push notif. Disable send alert notify. Check the config file /config/*.");
			logger.info("Push notif. Disable send alert notify. Check the config file /config/*.");
			return;
		}

		CmsUserManagerment userManagerment = new CmsUserManagerment();
		ArrayList<CmsUser> users = userManagerment.getCmsUserList();
		if (users.isEmpty()) {
			logger.error("Push notif. Cannot get user list.");
			return;
		}
		record = CON.readFileAsString(Logger.getRecordFilePath());
		logger.info("Push notif. Checking alert quota and push notif.");
		for (CmsUser user : users) {
			float usedRate = user.getUseRateBlock();
			boolean isNeededPushNotif = false;
			boolean isNeededPushNotif_high = false;
			if (notifQ1 == 0) {
				if (user.isNeedNotif() && usedRate < 100) {
					isNeededPushNotif = true;
					if (usedRate >= notifQ2)
						isNeededPushNotif_high = true;
				}
			} else {
				if (usedRate >= notifQ1 && usedRate < 100) {
					isNeededPushNotif = true;
					if (usedRate >= notifQ2)
						isNeededPushNotif_high = true;
				}
			}
			if (isNeededPushNotif) {
				logger.info("Push notif. Alert: " + user.getEMAIL() + " " + user.getNAMESPACE() + "\t\tUsed:"
						+ user.getBILLING().getTotal_size() + "GB - " + usedRate + "%");
				System.out.println("Push notif. Alert: " + user.getEMAIL() + " " + user.getNAMESPACE() + "\t\tUsed:"
						+ user.getBILLING().getTotal_size() + "GB - " + usedRate + "%");
				
				pushEmail(user, usedRate, isNeededPushNotif_high);

			}
		}
	}

	public void exportReport() {
		DateTimeFormatter dtfPath = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime now = LocalDateTime.now();
		CmsUserManagerment userManagerment = new CmsUserManagerment();
		ArrayList<CmsUser> users = (ArrayList<CmsUser>) userManagerment.getCmsUserList();
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(reportPath + "report-" + dtfPath.format(now) + ".csv"));
			String[] header = { "EMAIL", "NAMESPACE", "QUOTA(GiB)", "TOTAL SIZE(GiB)", "TOTAL RATE(%)" };
			writer.writeNext(header);
			for (CmsUser user : users) {
				float useRate = user.getUseRateBlock();
				EcsBilling billing = user.getBILLING();
				float useTotal = 0;
				float useQuota = 0;
				if (billing != null && billing.getTotal_size() != null && !billing.getTotal_size().equals(""))
					useTotal = Float.parseFloat(billing.getTotal_size());
				if (billing != null && billing.getBlockSize() != 0)
					useQuota = billing.getBlockSize();

				String[] record = { user.getEMAIL(), user.getNAMESPACE(), Float.toString(useQuota),
						Float.toString(useTotal), Float.toString(useRate) };
				writer.writeNext(record);
			}
			System.out.println("Push notif. Export report done.");
			writer.close();
		} catch (Exception e) {
			logger.error("Push notif. exportReport fail.\n" + e.toString());
			System.err.println("Push notif. exportReport fail.\n" + e.toString());
		}
	}

}
