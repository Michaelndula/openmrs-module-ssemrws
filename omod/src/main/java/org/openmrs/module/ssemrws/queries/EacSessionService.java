package org.openmrs.module.ssemrws.queries;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Component
public class EacSessionService {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public List<Object[]> getEacSessionCountsByDateRange(Date startDate, Date endDate) {
		String qry = "SELECT 'EAC1' as eac_type, DATE_FORMAT(hvl.adherence_date, '%b') as month, COUNT(hvl.client_id) as count "
		        + "FROM ssemr_etl.ssemr_flat_encounter_high_viral_load hvl "
		        + "WHERE hvl.adherence_date BETWEEN :startDate AND :endDate " + "GROUP BY month " + "UNION ALL "
		        + "SELECT 'EAC2' as eac_type, DATE_FORMAT(hvl.second_eac_session_date, '%b') as month, COUNT(hvl.client_id) as count "
		        + "FROM ssemr_etl.ssemr_flat_encounter_high_viral_load hvl "
		        + "WHERE hvl.second_eac_session_date BETWEEN :startDate AND :endDate " + "GROUP BY month " + "UNION ALL "
		        + "SELECT 'EAC3' as eac_type, DATE_FORMAT(hvl.third_eac_session_date, '%b') as month, COUNT(hvl.client_id) as count "
		        + "FROM ssemr_etl.ssemr_flat_encounter_high_viral_load hvl "
		        + "WHERE hvl.third_eac_session_date BETWEEN :startDate AND :endDate " + "GROUP BY month";
		
		return entityManager.createNativeQuery(qry).setParameter("startDate", startDate).setParameter("endDate", endDate)
		        .getResultList();
	}
}
