package org.openmrs.module.ssemrws.queries;

import org.openmrs.Patient;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class GetVLDueDate {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	/**
	 * Checks if the patient's most recent follow-up encounter has a collected VL sample but is still
	 * awaiting results.
	 * 
	 * @param patient The patient to check.
	 * @return True if a VL result is pending, otherwise false.
	 */
	private boolean isPatientVLPending(Patient patient) {
		String pendingCheckQuery = "SELECT 1 FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up latest_fp "
		        + "WHERE latest_fp.client_id = :patientId " + "AND latest_fp.date_vl_sample_collected IS NOT NULL "
		        + "AND latest_fp.date_vl_results_received IS NULL " + "AND latest_fp.encounter_datetime = ( "
		        + "    SELECT MAX(f.encounter_datetime) " + "    FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f "
		        + "    WHERE f.client_id = latest_fp.client_id " + ") LIMIT 1";
		
		try {
			Query query = entityManager.createNativeQuery(pendingCheckQuery).setParameter("patientId",
			    patient.getPatientId());
			return !query.getResultList().isEmpty();
		}
		catch (Exception e) {
			System.err.println("Error checking for pending VL status: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Checks if the patient's latest viral load result is high (>= 1000) AND they have not yet
	 * completed their third EAC session.
	 * 
	 * @param patient The patient to check.
	 * @return True if the patient meets the HVL criteria, otherwise false.
	 */
	private boolean isPatientInHVLCohort(Patient patient) {
		String hvlCheckQuery = "SELECT 1 FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up fp "
		        + "LEFT JOIN ssemr_etl.ssemr_flat_encounter_high_viral_load hvl ON fp.client_id = hvl.client_id "
		        + "WHERE fp.client_id = :patientId " + "AND fp.encounter_datetime = ( "
		        + "    SELECT MAX(f.encounter_datetime) " + "    FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f "
		        + "    WHERE f.client_id = fp.client_id " + ") " + "AND fp.viral_load_value >= 1000 "
		        + "AND hvl.third_eac_session_date IS NULL " + "LIMIT 1";
		
		try {
			Query query = entityManager.createNativeQuery(hvlCheckQuery).setParameter("patientId", patient.getPatientId());
			return !query.getResultList().isEmpty();
		}
		catch (Exception e) {
			System.err.println("Error checking for HVL cohort status: " + e.getMessage());
			return false;
		}
	}
	
	public String getVLDueDate(Patient patient) {
		if (isPatientVLPending(patient)) {
			return "Pending Results";
		}
		if (isPatientInHVLCohort(patient)) {
			return "Pending EAC 3";
		}
		String query = "SELECT client_id, DATE_FORMAT(MAX(eligibility_date), '%d-%m-%Y') AS max_due_date FROM ("
		        + "SELECT fp.client_id, " + "CASE " +
				
				// This handles cases where a sample was collected but results are pending or
				// unsuppressed.
		        "WHEN fp.date_vl_sample_collected IS NOT NULL AND (fp.viral_load_value IS NULL OR fp.viral_load_value >= 1000) "
		        + "THEN DATE_ADD(fp.date_vl_sample_collected, INTERVAL 6 MONTH) " +
				
				// Adults suppressed
		        "WHEN (mp.age > 18 AND pfh.art_start_date IS NOT NULL AND fp.client_pmtct = 'No' "
		        + " AND (fp.viral_load_value < 1000 OR fp.vl_results = 'Below Detectable (BDL)')) " + "THEN " + "    CASE "
		        + "        WHEN DATEDIFF(fp.encounter_datetime, pfh.art_start_date) > 365 "
		        + "        THEN DATE_ADD(fp.date_vl_sample_collected, INTERVAL 12 MONTH) "
		        + "        ELSE DATE_ADD(fp.date_vl_sample_collected, INTERVAL 6 MONTH) " + "    END " +
				
				// Adults newly on ART
		        "WHEN (mp.age > 18 AND pfh.art_start_date IS NOT NULL "
		        + " AND NOT EXISTS (SELECT 1 FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up v2 "
		        + "     WHERE v2.client_id = fp.client_id AND v2.date_vl_sample_collected IS NOT NULL)) "
		        + "THEN DATE_ADD(pfh.art_start_date, INTERVAL 6 MONTH) " +
				
				// Children
		        "WHEN (mp.age <= 18 AND pfh.art_start_date IS NOT NULL) THEN DATE_ADD(pfh.art_start_date, INTERVAL 6 MONTH) "
		        + "WHEN (mp.age <= 18 AND vlr.date_of_sample_collection IS NOT NULL) THEN DATE_ADD(vlr.date_of_sample_collection, INTERVAL 6 MONTH) "
		        +
				
				// Pregnant PMTCT
		        "WHEN (fp.client_pmtct = 'Yes' AND vlr.date_of_sample_collection IS NOT NULL) THEN DATE_ADD(vlr.date_of_sample_collection, INTERVAL 3 MONTH) "
		        + "WHEN (fp.client_pmtct = 'Yes' AND vlr.date_of_sample_collection IS NULL) THEN DATE_ADD(fp.encounter_datetime, INTERVAL 3 MONTH) "
		        +
				
				// Pregnant general
		        "WHEN (fp.client_pregnant = 'Yes' AND pfh.art_start_date IS NOT NULL) THEN fp.encounter_datetime " +
				
				// EAC
		        "WHEN (hvl.third_eac_session_date IS NOT NULL) THEN DATE_ADD(hvl.third_eac_session_date, INTERVAL 1 MONTH) "
		        +
				
				// Fallback
		        "WHEN pfh.art_start_date IS NOT NULL THEN DATE_ADD(pfh.art_start_date, INTERVAL 6 MONTH) " +
				
		        "ELSE NULL END AS eligibility_date " + "FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up fp "
		        + "LEFT JOIN ssemr_etl.ssemr_flat_encounter_hiv_care_enrolment en ON en.client_id = fp.client_id "
		        + "LEFT JOIN ssemr_etl.ssemr_flat_encounter_vl_laboratory_request vlr ON vlr.client_id = fp.client_id "
		        + "LEFT JOIN ssemr_etl.mamba_dim_person mp ON mp.person_id = fp.client_id "
		        + "LEFT JOIN ssemr_etl.ssemr_flat_encounter_personal_family_tx_history pfh ON pfh.client_id = fp.client_id "
		        + "LEFT JOIN ssemr_etl.ssemr_flat_encounter_high_viral_load hvl ON hvl.client_id = fp.client_id "
		        + "WHERE fp.client_id = :patientId AND DATE(fp.encounter_datetime) <= CURRENT_DATE "
		        + ") AS t GROUP BY client_id";
				
		try {
			Query nativeQuery = entityManager.createNativeQuery(query).setParameter("patientId", patient.getPatientId());
			List<Object[]> results = nativeQuery.getResultList();
			if (results != null && !results.isEmpty()) {
				Object[] firstResult = results.get(0);
				return (firstResult[1] != null) ? firstResult[1].toString() : "N/A";
			}
		}
		catch (Exception e) {
			System.err.println("Error calculating VL due date: " + e.getMessage());
		}
		
		return "N/A";
	}
}
