package org.cap.mcc.batch.model;

public class TransactionData {
	
	private int capNumber;
	private String customerName;
	private String longBusinessName;
	private String dueDate;
	public int getCapNumber() {
		return capNumber;
	}
	public void setCapNumber(int capNumber) {
		this.capNumber = capNumber;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getLongBusinessName() {
		return longBusinessName;
	}
	public void setLongBusinessName(String longBusinessName) {
		this.longBusinessName = longBusinessName;
	}
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	
	

}
