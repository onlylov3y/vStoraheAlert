package com.cms.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.cms.ecs.EcsBilling;
import com.cms.ecs.EcsManager;
import com.cms.setting.Configuration;
import com.cms.setting.Logger;


public class CmsUserManagerment {

	private static final Logger logger = new Logger();
	private static final int RETRY = 3;

	private static Configuration CON;
	private static CmsConfig cmsConfig;
	private EcsManager ecsManager;
	private ArrayList<CmsUser> users;
	
	public CmsUserManagerment() {
		CON = new Configuration();
		cmsConfig = CON.getCmsConfig();
		ecsManager = new EcsManager();
		users = new ArrayList<>();
	}

	public ArrayList<CmsUser> getCmsUserList() {
		for(CmsUser user : getCmsUsers()) {
			this.users.add(mappingUsers(user));
		}
		return this.users;
	}

	public CmsUser mappingUsers(CmsUser user) {
		//logger.info("Mapping user with quota and total used.");
			if (user != null && Integer.parseInt(user.getQUOTA()) > 0) {
				EcsBilling billing = ecsManager.getEcsBilling(user.getNAMESPACE(), true);
				user.setBILLING(billing);
			}
		return user;
	}

	// USER_STORAGE_INFO USER_STORAGE USER_CMS_STORAGE ROLE_FUNCTION ROLE CMS_FUNCTION
	public ArrayList<CmsUser> getCmsUsers() {
		logger.info("Get CMS Users from DB.");
		//String getAllUsers = "SELECT * FROM USER_STORAGE";
		
		String getAllUsers = "SELECT user_storage.id, user_storage.namespace, user_storage.email, "
				+ "user_storage.quota, user_storage_info.phone, user_storage_info.adress "
				+ "FROM user_storage "
				+ "LEFT JOIN user_storage_info "
				+ "ON user_storage.email = user_storage_info.email "
				+ "ORDER BY user_storage.id";
		
		Connection conn = null;
		String dbUrl = cmsConfig.getDbUrl() + "/" + cmsConfig.getDbSid();
		ArrayList<CmsUser> users = new ArrayList<>();
		try {
			for (int i = 0; i < RETRY; i++) {
				conn = DriverManager.getConnection(dbUrl, cmsConfig.getDbUsername(), cmsConfig.getDbPassword());
				//conn.setNetworkTimeout(null, 600000);
				if (conn != null) {
					Statement stmt = conn.createStatement();
					
					ResultSet result = stmt.executeQuery(getAllUsers);
					while (result.next()) {
						CmsUser user = new CmsUser();
						user.setID(result.getInt("ID"));
						user.setNAMESPACE(result.getString("NAMESPACE"));
						user.setEMAIL(result.getString("EMAIL"));
						user.setQUOTA(result.getString("QUOTA"));
						user.setPHONE(result.getString("PHONE"));
						user.setADRESS(result.getString("ADRESS"));
						users.add(user);
					}
					return users;
				}
			}
			logger.error("dbQuery fail. Cannot connect to db.");
		} catch (Exception e) {
			logger.error("dbQuery fail. Cannot connect to db.\n" + e.toString());
			System.out.println(e.toString());
		} finally {
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException ex) {
			}
		}
		return users;
	}

}
