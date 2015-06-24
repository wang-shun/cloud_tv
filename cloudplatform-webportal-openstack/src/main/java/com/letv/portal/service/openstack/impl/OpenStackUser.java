package com.letv.portal.service.openstack.impl;

public class OpenStackUser {

	private String userId;
	private String password;
	private boolean firstLogin;
	private boolean internalUser;
	
	private String publicNetworkName;
	private String privateNetworkName;
	private String sharedNetworkName;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin;
	}

	public boolean getInternalUser() {
		return internalUser;
	}

	public void setInternalUser(boolean internalUser) {
		this.internalUser = internalUser;
	}

	public String getPublicNetworkName() {
		return publicNetworkName;
	}

	public void setPublicNetworkName(String publicNetworkName) {
		this.publicNetworkName = publicNetworkName;
	}

	public String getPrivateNetworkName() {
		return privateNetworkName;
	}

	public void setPrivateNetworkName(String privateNetworkName) {
		this.privateNetworkName = privateNetworkName;
	}

	public String getSharedNetworkName() {
		return sharedNetworkName;
	}

	public void setSharedNetworkName(String sharedNetworkName) {
		this.sharedNetworkName = sharedNetworkName;
	}

}
