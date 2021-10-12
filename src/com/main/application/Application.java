package com.main.application;

import com.cms.notif.NotifManager;
import com.cms.setting.Logger;

public class Application {

	private static final Logger logger = new Logger();

	public static void main(String[] args) {
		NotifManager notif = new NotifManager();

		notif.pushNotify();
		//notif.exportReport();
		
		if (args.length > 0) {
			// help menu
			if (args[0].equals("-h") || args[0].equals("--help")) {
				System.out.println("----------------------------------\n" 
						+ "Welcome Viettel Storage Alert Helper.\n"
						+ "Menu:\n" 
						+ "-x\t--export\t: Export report for used total, used rate for namespaces.\n"
						+ "-p\t--push\t: Push alert notification for users.\n" 
						+ "----------------------------------");
			}
			// export report
			if (args[0].equals("-x") || args[0].equals("--export")) {
				System.out.println("Export report for used total, used rate for namespaces.");
				logger.debug("Export report for used total, used rate for namespaces");
				notif.exportReport();
			}
			// push notification
			if (args[0].equals("-p") || args[0].equals("--push")) {
				System.out.println("Push alert notification for users.");
				logger.debug("Push alert notification for users.");
				notif.pushNotify();
			}
		} else {
			System.out.println("Missing agruments. Please -h\t--help to see more.");
		}
	}
}
