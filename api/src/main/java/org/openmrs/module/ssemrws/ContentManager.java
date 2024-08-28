package org.openmrs.module.ssemrws;

public interface ContentManager {
	
	/**
	 * Gets the priority value to determine refresh order
	 * 
	 * @return the priority value
	 */
	int getPriority();
	
	/**
	 * Refreshes the manager after a content refresh
	 */
	void refresh();
}
