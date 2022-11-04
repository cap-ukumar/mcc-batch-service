package org.cap.mcc.batch.model;

import java.util.ArrayList;
import java.util.List;

public class CorrespondenceData {
	
	
	private int customerNumber;
	private int capNumber;
	private String templateCode;
	private String templateName;
	private TemplateData templatePlaceholder;
	private ArrayList<CustomerData> recipients;
	private List<AttachmentDetails> attachments;
	
	
	public int getCustomerNumber() {
		return customerNumber;
	}


	public void setCustomerNumber(int customerNumber) {
		this.customerNumber = customerNumber;
	}


	public int getCapNumber() {
		return capNumber;
	}


	public void setCapNumber(int capNumber) {
		this.capNumber = capNumber;
	}


	public String getTemplateCode() {
		return templateCode;
	}


	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}


	public String getTemplateName() {
		return templateName;
	}


	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}


	public TemplateData getTemplatePlaceholder() {
		return templatePlaceholder;
	}


	public void setTemplatePlaceholder(TemplateData templatePlaceholder) {
		this.templatePlaceholder = templatePlaceholder;
	}


	public ArrayList<CustomerData> getRecipients() {
		return recipients;
	}


	public void setRecipients(ArrayList<CustomerData> recipients) {
		this.recipients = recipients;
	}
	
	public class AttachmentDetails {

		private String document;

		public String getDocument() {
			return document;
		}

		public void setDocument(String document) {
			this.document = document;
		}
	}



	public class TemplateData{
		private String partyNumber;
		private String customerName;
		private String longBusinessName2;
		private String dueDate;
		private String analytesHtm;
		private String associateName;
		private String associateExt;
		public String getPartyNumber() {
			return partyNumber;
		}
		public void setPartyNumber(String partyNumber) {
			this.partyNumber = partyNumber;
		}
		public String getCustomerName() {
			return customerName;
		}
		public void setCustomerName(String customerName) {
			this.customerName = customerName;
		}
		public String getLongBusinessName2() {
			return longBusinessName2;
		}
		public void setLongBusinessName2(String longBusinessName2) {
			this.longBusinessName2 = longBusinessName2;
		}
		public String getDueDate() {
			return dueDate;
		}
		public void setDueDate(String dueDate) {
			this.dueDate = dueDate;
		}
		public String getAnalytesHtm() {
			return analytesHtm;
		}
		public void setAnalytesHtm(String analytesHtm) {
			this.analytesHtm = analytesHtm;
		}
		public String getAssociateName() {
			return associateName;
		}
		public void setAssociateName(String associateName) {
			this.associateName = associateName;
		}
		public String getAssociateExt() {
			return associateExt;
		}
		public void setAssociateExt(String associateExt) {
			this.associateExt = associateExt;
		}
		
		
		
	}


	public List<AttachmentDetails> getAttachments() {
		return attachments;
	}


	public void setAttachments(List<AttachmentDetails> attachments) {
		this.attachments = attachments;
	}
	
	

}
