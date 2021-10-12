package com.cms.ecs;

public class EcsBilling {
	private String namespace;
	private String sample_time;
	private String total_size;
	private String total_size_unit;
	private int total_objects;
	private String uptodate_till;
	private String total_mpu_size;
	private int total_mpu_parts;
	private String total_objects_deleted;
	private String total_size_deleted;
	private float blockSize;
	private float notificationSize;

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getSample_time() {
		return sample_time;
	}

	public void setSample_time(String sample_time) {
		this.sample_time = sample_time;
	}

	public String getTotal_size() {
		return total_size;
	}

	public void setTotal_size(String total_size) {
		this.total_size = total_size;
	}

	public String getTotal_size_unit() {
		return total_size_unit;
	}

	public void setTotal_size_unit(String total_size_unit) {
		this.total_size_unit = total_size_unit;
	}

	public int getTotal_objects() {
		return total_objects;
	}

	public void setTotal_objects(int total_objects) {
		this.total_objects = total_objects;
	}

	public String getUptodate_till() {
		return uptodate_till;
	}

	public void setUptodate_till(String uptodate_till) {
		this.uptodate_till = uptodate_till;
	}

	public String getTotal_mpu_size() {
		return total_mpu_size;
	}

	public void setTotal_mpu_size(String total_mpu_size) {
		this.total_mpu_size = total_mpu_size;
	}

	public int getTotal_mpu_parts() {
		return total_mpu_parts;
	}

	public void setTotal_mpu_parts(int total_mpu_parts) {
		this.total_mpu_parts = total_mpu_parts;
	}

	public String getTotal_objects_deleted() {
		return total_objects_deleted;
	}

	public void setTotal_objects_deleted(String total_objects_deleted) {
		this.total_objects_deleted = total_objects_deleted;
	}

	public String getTotal_size_deleted() {
		return total_size_deleted;
	}

	public void setTotal_size_deleted(String total_size_deleted) {
		this.total_size_deleted = total_size_deleted;
	}

	public float getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(float blockSize) {
		this.blockSize = blockSize;
	}

	public float getNotificationSize() {
		return notificationSize;
	}

	public void setNotificationSize(float notificationSize) {
		this.notificationSize = notificationSize;
	}

}
