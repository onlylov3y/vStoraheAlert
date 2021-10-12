package com.cms.core;

import com.cms.ecs.EcsBilling;

public class CmsUser {

	private int ID;
	private String NAMESPACE;
	private String EMAIL;
	private String PHONE;
	private String ADRESS;
	private String QUOTA;
	private EcsBilling BILLING;

	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public String getNAMESPACE() {
		return NAMESPACE;
	}

	public void setNAMESPACE(String NAMESPACE) {
		this.NAMESPACE = NAMESPACE;
	}

	public String getEMAIL() {
		return EMAIL;
	}

	public void setEMAIL(String EMAIL) {
		this.EMAIL = EMAIL;
	}

	public String getPHONE() {
		return PHONE;
	}

	public void setPHONE(String PHONE) {
		this.PHONE = PHONE;
	}

	public String getADRESS() {
		return ADRESS;
	}

	public void setADRESS(String ADRESS) {
		this.ADRESS = ADRESS;
	}

	public String getQUOTA() {
		return QUOTA;
	}

	public void setQUOTA(String QUOTA) {
		this.QUOTA = QUOTA;
	}

	public EcsBilling getBILLING() {
		return BILLING;
	}

	public void setBILLING(EcsBilling BILLING) {
		this.BILLING = BILLING;
	}

	public float getUseRateQuota() {
		//Get quata from portal DB
		if (QUOTA == null || QUOTA.equals("") || Float.parseFloat(QUOTA) <= 0
				|| BILLING == null
				|| BILLING.getTotal_size() == null || BILLING.getTotal_size().equals(""))
			return 0;
		
		return (float)(Float.parseFloat(BILLING.getTotal_size()) / Float.parseFloat(QUOTA)) * 100; //Gb/Gb*100
	}
	
	public float getUseRateBlock() {
		//Get quata from cms storage (block size)
		if (BILLING == null || BILLING.getBlockSize() <= 0
				|| BILLING.getTotal_size() == null || BILLING.getTotal_size().equals(""))
			return 0;
		return (float)(Float.parseFloat(BILLING.getTotal_size()) / BILLING.getBlockSize()) * 100;
	}
	
	public boolean isNeedNotif() {
		if (BILLING == null || BILLING.getNotificationSize() <= 0 || BILLING.getBlockSize() <= 0 
				|| BILLING.getTotal_size() == null || BILLING.getTotal_size().equals(""))
			return false;
		return Float.parseFloat(BILLING.getTotal_size()) >= BILLING.getNotificationSize();
	}
	
}
