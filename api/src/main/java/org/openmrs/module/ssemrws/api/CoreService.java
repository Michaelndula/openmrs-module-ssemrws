package org.openmrs.module.ssemrws.api;

import org.openmrs.api.APIException;
import org.openmrs.module.ssemrws.ContentManager;
import org.openmrs.module.ssemrws.chore.Chore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CoreService {
	
	/**
	 * Refreshes a content manager
	 * 
	 * @param manager the manager
	 */
	void refreshManager(ContentManager manager) throws APIException;
	
	/**
	 * Performs the given chore
	 * 
	 * @param chore the chore
	 */
	void performChore(Chore chore) throws APIException;
}
