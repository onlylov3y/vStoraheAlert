package com.cms.setting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

	private static final String logPath = "logs" + File.separator;
	private static final String RecordPath = "record" + File.separator;
	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final DateTimeFormatter dtfPath = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	public void info(String info) {
		writeLog("INFO: " + info);
	}

	public void error(String error) {
		writeLog("ERROR: " + error);
	}

	public void debug(String debug) {
		writeLog("DEBUG: " + debug);
	}

	public void record(String record) {
		writeRecord("RECORD: " + record);
	}

	private static void writeLog(String str) {
		
		LocalDateTime now = LocalDateTime.now();
		try {
			File file = new File(logPath + "console" + dtfPath.format(now) + ".log");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(dtf.format(now) + " " + str);
			bw.newLine();
			bw.close();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static String getRecordFilePath() {
		LocalDateTime now = LocalDateTime.now();
		return RecordPath + "record-" + dtfPath.format(now) + ".log";
	}
	
	private static void writeRecord(String str) {
		LocalDateTime now = LocalDateTime.now();
		try {
			File file = new File(getRecordFilePath());
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(dtf.format(now) + " " + str);
			bw.newLine();
			bw.close();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
