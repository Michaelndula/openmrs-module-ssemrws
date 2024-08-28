package org.openmrs.module.ssemrws.chore;

import org.openmrs.api.APIException;

import java.io.PrintWriter;

public interface Chore {
	
	/**
	 * Gets the id of the chore. Should not contains spaces or punctuation besides period as it will be
	 * used to form a global property
	 * 
	 * @return the chore id
	 */
	String getId();
	
	/**
	 * Performs the chore
	 * 
	 * @param output the writer for console output
	 */
	void perform(PrintWriter output) throws APIException;
}
