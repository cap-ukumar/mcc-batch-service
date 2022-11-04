package org.cap.mcc.batch.model;

import java.util.ArrayList;

public class TRBatchResponse {
	
	private int requestId;
	private String requestType;
	private String requestMode;
	private ArrayList<CorrespondenceData> correspondenceDataList;
	public int getRequestId() {
		return requestId;
	}
	public void setRequestId(int i) {
		this.requestId = i;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getRequestMode() {
		return requestMode;
	}
	public void setRequestMode(String requestMode) {
		this.requestMode = requestMode;
	}
	public ArrayList<CorrespondenceData> getCorrespondenceDataList() {
		return correspondenceDataList;
	}
	public void setCorrespondenceDataList(ArrayList<CorrespondenceData> correspondenceDataList) {
		this.correspondenceDataList = correspondenceDataList;
	}
	
	

}
