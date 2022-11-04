package org.cap.mcc.batch;

import java.io.InputStream;

import org.cap.mcc.batch.service.TargetResolutionBatch;
import org.cap.mcc.batch.utils.CommonUtils;

public class MccLAPBatchService {
	
	public static void main(String[] args) {
		
		try {
			InputStream stream = MccLAPBatchService.class.getResourceAsStream("/Files/TargetResolution.properties");
			CommonUtils.loadProperties(stream);
			TargetResolutionBatch.getConsolidatedData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
