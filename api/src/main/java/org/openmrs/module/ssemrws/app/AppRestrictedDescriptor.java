package org.openmrs.module.ssemrws.app;

import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.ssemrws.Descriptor;

import java.util.Set;

public interface AppRestrictedDescriptor extends Descriptor {
	
	/**
	 * Gets the apps allowed to access this
	 * 
	 * @return the apps descriptors
	 */
	Set<AppDescriptor> getApps();
	
	/**
	 * Sets the apps allowed to access this
	 * 
	 * @param apps the app descriptors
	 */
	void setApps(Set<AppDescriptor> apps);
}
