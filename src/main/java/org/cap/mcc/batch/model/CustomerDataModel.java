package org.cap.mcc.batch.model;

import java.util.List;

public class CustomerDataModel {
	
	private List<String> contactRoleDetails;
	private List<Integer> capNumberList;
	private int requestId;
	
	
	
	
	
	public int getRequestId() {
		return requestId;
	}
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	public List<String> getContactRoleDetails() {
		return contactRoleDetails;
	}
	public void setContactRoleDetails(List<String> contactRoleDetails) {
		this.contactRoleDetails = contactRoleDetails;
	}
	public List<Integer> getCapNumberList() {
		return capNumberList;
	}
	public void setCapNumberList(List<Integer> capNumberList) {
		this.capNumberList = capNumberList;
	}
	
	

}
