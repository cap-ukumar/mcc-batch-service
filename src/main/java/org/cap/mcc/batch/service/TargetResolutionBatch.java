package org.cap.mcc.batch.service;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cap.mcc.batch.dao.TargetResolutionDAO;
import org.cap.mcc.batch.model.CorrespondenceData;
import org.cap.mcc.batch.model.CorrespondenceData.AttachmentDetails;
import org.cap.mcc.batch.model.CorrespondenceData.TemplateData;
import org.cap.mcc.batch.model.CustomerData;
import org.cap.mcc.batch.model.CustomerDataModel;
import org.cap.mcc.batch.model.TRBatchResponse;
import org.cap.mcc.batch.model.TransactionData;
import org.cap.mcc.batch.utils.CapConfigConstants;
import org.cap.mcc.batch.utils.CommonUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class TargetResolutionBatch {
	static org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(TargetResolutionBatch.class);

	private static int processId;
	private static  RestTemplate restTemplate;
	private static Map<Integer,TransactionData> txnDataMap;
	
	
	public static List<TransactionData> getLabData()  {

		List<TransactionData> details = new ArrayList<TransactionData>();
		ResultSet rs = null;
		 try (Connection connection = DriverManager.getConnection(CommonUtils.getProperty(CapConfigConstants.INFORMIX_URL),
				 CommonUtils.getProperty(CapConfigConstants.INFORMIX_USERNAME),
				 CommonUtils.getProperty(CapConfigConstants.INFORMIX_PASSWORD));
	                Statement statement = connection.createStatement();) {
	            rs = statement.executeQuery(TargetResolutionDAO.LAB_DETAILS_QUERY);
	            txnDataMap = new HashMap<>();
	            while (rs.next()) {
	    			TransactionData data = new TransactionData();
	    			data.setCapNumber(rs.getInt("cap_number"));
	    			data.setCustomerName(rs.getString("customer_name"));
	    			data.setLongBusinessName(rs.getString("long_busn_name2"));
	    			data.setDueDate(rs.getString("due_date"));
	    			txnDataMap.put(data.getCapNumber(), data);
	    			details.add(data);
	    			}

	        }
	        catch (Exception e) {
	        	 updateProcessSchedule("FAIL", e.getMessage(),processId );
	        	logger.error("Exception in getting LabData ::"+ e.toString());
	        	
	        }
		return details;
	}
	
	
	public static List<String> getContactRolesData() {
		List<String> details = new ArrayList<String>();
		ResultSet resultSet = null;
		 try (Connection connection = DriverManager.getConnection(CommonUtils.getProperty(CapConfigConstants.POSTGRES_URL),
				 CommonUtils.getProperty(CapConfigConstants.POSTGRES_USERNAME)
				 ,CommonUtils.getProperty(CapConfigConstants.POSTGRES_PASSWORD));
	                Statement statement = connection.createStatement();) {
	            resultSet = statement.executeQuery(TargetResolutionDAO.GET_CONTACT_ROLES_QUERY);
	        	while (resultSet.next()) {
	    			details.add(resultSet.getString("column_data_t"));
	    			}

	        }
		 catch (Exception e) {
			 updateProcessSchedule("FAIL", e.getMessage(),processId );
	        	logger.error("Exception in getting contact roles ::"+ e.toString());
	        }
		 return details;
			
	}
	

	public static Map<Integer, List<CustomerData>> getCustomerData(List<String> contactRoleDetails,List<Integer> capNumberList) {
		Map<Integer, List<CustomerData>> customerList = null;
		try {
			CustomerDataModel dataModel = new CustomerDataModel();
			dataModel.setCapNumberList(capNumberList);
			dataModel.setContactRoleDetails(contactRoleDetails);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<CustomerDataModel> entity = new HttpEntity<CustomerDataModel>(dataModel ,headers);
			Map<Integer, List<CustomerData>> responseEntity = restTemplate
					.postForObject(CommonUtils.getProperty(CapConfigConstants.COMMON_SERVICE_BASE_URL)
							+ CapConfigConstants.CUSTOMER_DATA_END_POINT, entity, Map.class);

			
				customerList = new ObjectMapper().readValue(new Gson().toJson(responseEntity),
						new TypeReference<Map<Integer, List<CustomerData>>>() {
						});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return customerList;

	}


	private static List<Integer> getCapNumbers(List<TransactionData> labDetails) {
		List<TransactionData> details  = labDetails;
		return details.stream().map(det-> det.getCapNumber()).collect(Collectors.toList());
		
	}
	
	
	public static void getConsolidatedData() {

		try {
		if(restTemplate==null) {
			getRestTemplate();
		}	
		insertIntoProcessSchedule();
		List<String> contactRoleDetails = getContactRolesData();
		List<TransactionData> labDetails = getLabData();
		List<Integer> capNumberList = getCapNumbers(labDetails);
		TRBatchResponse batchResponse  = new TRBatchResponse();
		batchResponse.setRequestMode(CapConfigConstants.BATCH);
		batchResponse.setRequestType(CapConfigConstants.DOC);
		ArrayList<CorrespondenceData> correspondenseDataList = new ArrayList<CorrespondenceData>();
		Map<Integer, List<CustomerData>> customerDataList = getCustomerData(contactRoleDetails, capNumberList);
		RestTemplate restTemplate = new RestTemplate();
		 for (Map.Entry<Integer,List<CustomerData>> entry : customerDataList.entrySet()) {
			ResponseEntity<ArrayList> responseEntity = restTemplate
					.postForEntity(CommonUtils.getProperty(CapConfigConstants.COMMON_SERVICE_BASE_URL)+CapConfigConstants.TRACKING_END_POINT, entry.getValue(),ArrayList.class);

			if(responseEntity.getBody()!=null) {
				 ArrayList<CustomerData> customerList = new ObjectMapper().readValue(new Gson().toJson(responseEntity.getBody()), new TypeReference<List<CustomerData>>() {});
				 batchResponse.setRequestId(customerList.get(0).getRequestId());
				 if(CommonUtils.getProperty(CapConfigConstants.JSON_FILE_RESPONSE_TYPE).equalsIgnoreCase("SINGLE")) {
					
					batchResponse.setCorrespondenceDataList(mapTRData(batchResponse,customerList));
					boolean status = moveFileToEFSLocation(batchResponse,entry.getKey().toString());
					if(!status) {
						updateProcessSchedule("FAIL", "Exception Occured while moving Files",processId );
					}
				}else {
					correspondenseDataList.addAll(mapTRData(batchResponse,customerList));
				}
			}
			
			
		 }
		 if(CommonUtils.getProperty(CapConfigConstants.JSON_FILE_RESPONSE_TYPE).equalsIgnoreCase("BATCH")) {
				batchResponse.setCorrespondenceDataList(correspondenseDataList);
				boolean status = moveFileToEFSLocation(batchResponse,"TR_BATCH_RESPONSE");
				if(!status) {
					updateProcessSchedule("FAIL", "Exception Occured while moving Files",processId );
				}
			}
		 updateProcessSchedule("SUCC", "Batch Processed Succesfully",processId );
		}catch(Exception e) {
			 updateProcessSchedule("FAIL", e.getMessage(),processId );
			 logger.error("Exception occured while processing batch:::"+ e.toString());
			
		 }
		
	}


	private static ArrayList<CorrespondenceData> mapTRData(TRBatchResponse batchResponse, ArrayList<CustomerData> customerList) {
		ArrayList<CorrespondenceData> correspondenceDataList = new ArrayList<>();
		CorrespondenceData corrData = new CorrespondenceData();
		TemplateData templateData =  corrData.new TemplateData();
		templateData.setPartyNumber(customerList.get(0).getCapNumber());
		templateData.setCustomerName(customerList.get(0).getCustomerName());
		TransactionData data = txnDataMap.get(Integer.parseInt(customerList.get(0).getCapNumber()));
		templateData.setLongBusinessName2(data.getLongBusinessName());
		templateData.setDueDate(data.getDueDate());
		corrData.setTemplatePlaceholder(templateData);
		corrData.setRecipients(customerList);
		corrData.setCapNumber(Integer.parseInt(customerList.get(0).getCapNumber()));
		corrData.setCustomerNumber(Integer.parseInt(customerList.get(0).getCapNumber()));
		corrData.setTemplateCode(customerList.get(0).getTemplateCode());
		corrData.setTemplateName(customerList.get(0).getTemplateName());
		correspondenceDataList.add(corrData);
		AttachmentDetails details = corrData.new AttachmentDetails();
		details.setDocument(" ");
		List<AttachmentDetails> detailsList = new ArrayList<AttachmentDetails>();
		detailsList.add(details);
		corrData.setAttachments(detailsList);
		return correspondenceDataList;
	}


	private static boolean moveFileToEFSLocation(TRBatchResponse batchResponse, String capId) {
		boolean fileStatus = false;
		try {
			File dest = new File(CommonUtils.getProperty(CapConfigConstants.FILE_PATH));
		
			String fileName = dest+"\\"+capId.concat(".json");
			String doneFileName = dest+"\\"+capId.concat(".json.done");
			String customerJson = new Gson().toJson(batchResponse);
			try (FileWriter file = new FileWriter(fileName)) {
				file.write(customerJson);

			} catch (Exception e) {
				logger.error("Error occured while writing file ::"+ e.toString());
			}
			try (FileWriter file = new FileWriter(doneFileName)) {
				file.write("");

			} catch (Exception e) {
				logger.error("Error occured while writing done file ::"+ e.toString());
			}
			fileStatus = true;

		} catch (Exception e) {
			logger.error("Exception occured while moving f iles to processed location ::" + e.toString());
			fileStatus =  false;
		}
		return fileStatus;
	}
		
	


	private static int getRequestDetails() {
			return restTemplate.getForObject(
					CommonUtils.getProperty(CapConfigConstants.COMMON_SERVICE_BASE_URL)+CapConfigConstants.REQUEST_DETAILS_ENDPOINT,Integer.class);
	
	}
	
	
	public static void insertIntoProcessSchedule() {
		processId =   restTemplate.getForObject(
				CommonUtils.getProperty(CapConfigConstants.COMMON_SERVICE_BASE_URL)+CapConfigConstants.SCHEDULE_DETAILS_ENDPOINT,Integer.class);
		
		
	}
	
	public static void updateProcessSchedule(String status, String message,int processId) {
		String url = CommonUtils.getProperty(CapConfigConstants.COMMON_SERVICE_BASE_URL)+CapConfigConstants.UPDATE_SCHEDULE_ENDPOINT+"?status="+status+"&message="+message+"&processId="+processId;
		restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(null), null);
	}
	
	
	public static void getRestTemplate() {
		restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();        
		MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();

		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));        
		messageConverters.add(converter);  
		restTemplate.setMessageConverters(messageConverters); 
	}
	

}
