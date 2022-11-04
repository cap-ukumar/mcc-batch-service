package org.cap.mcc.batch.dao;

public class TargetResolutionDAO {
	
	public static final String GET_CONTACT_ROLES_QUERY = "select column_data_t from mccf.code_column_value where key_u in "
				+ "(SELECT key_u FROM mccf.code_column_value where table_u='47' "
				+ " and column_code_u='TMPLCODE' and column_data_t='ODR_DEF') and column_code_u='CNTCROLE';";
	

	public static final String  LAB_DETAILS_QUERY = "\r\n"
				+ "SELECT 	DISTINCT 	\r\n"
				+ " rf.reference_u as CAP_Number\r\n"
				+ ",a.long_busn_name1 as Customer_Name\r\n"
				+ ",a.long_busn_name2\r\n"
				+ ",ii.actual_da + 30 as due_date FROM lpt_acc_au_cycle cyc,lpt_inp_instance ii,lpt_inp_event	ie,ptt_abe_reference rf,lpt_acc_au_event ev1,lpt_acc_event_tplt 	t1,lpt_acc_au_event ev2,lpt_acc_event_tplt t2,ptt_abe a\r\n"
				+ "\r\n"
				+ "   WHERE cyc.au_cycle_s	     	= 'OPEN' \r\n"
				+ "	and cyc.type_c 					in ('INIT','ROUT')\r\n"
				+ "	and ii.insp_inst_u				= ie.insp_inst_u\r\n"
				+ "	and ii.insp_inst_s 				<>'CANC'\r\n"
				+ "	and ie.ie_s						= 'ACTV'\r\n"
				+ "	and cyc.abe_au_u            	= ie.abe_au_u \r\n"
				+ "	and cyc.seq_no_u            	= ie.seq_no_u\r\n"
				+ "	and cyc.acc_cycle_u			= ie.acc_cycle_u\r\n"
				+ "	and ii.actual_da 				is not null  \r\n"
				+ "	and ie.abe_au_u   = rf.abe_u\r\n"
				+ "	and rf.assigning_abe_u =  1000000  \r\n"
				+ "	and rf.reference_type_c = 'I'     \r\n"
				+ "	and current between rf.effective_dt and rf.termination_dt\r\n"
				+ "	and cyc.abe_au_u			= ev1.abe_au_u  \r\n"
				+ "	and cyc.seq_no_u 	 	  	= ev1.seq_no_u \r\n"
				+ "	and t1.acc_cycle_u	 	= ev1.acc_cycle_u    \r\n"
				+ "    and t1.event_u 			  	= ev1.event_u    \r\n"
				+ "	and (t1.au_cyc_ev_type_c = 'ISRC' and ev1.event_s = 'COMP') \r\n"
				+ "    and (current between t1.effective_dt and t1.termination_dt)\r\n"
				+ "    and t2.acc_cycle_u 		= ev2.acc_cycle_u    \r\n"
				+ "    and t2.event_u 			  	= ev2.event_u    \r\n"
				+ "    and (t2.au_cyc_ev_type_c = 'LRER' and ev2.event_s = 'OPEN') \r\n"
				+ "    and (current between t2.effective_dt and t2.termination_dt) \r\n"
				+ "    and ev1.abe_au_u		  	= ev2.abe_au_u  \r\n"
				+ "    and ev1.seq_no_u 	 	  	= ev2.seq_no_u  \r\n"
				+ " 	and ii.insr_type_c 			<> 'AB' \r\n"
				+ "	and a.abe_u = ie.abe_au_u\r\n"
				+ ";";


}
