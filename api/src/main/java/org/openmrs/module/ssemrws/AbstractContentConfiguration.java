package org.openmrs.module.ssemrws;

import org.openmrs.module.ModuleFactory;

public abstract class AbstractContentConfiguration extends AbstractDescriptor {
	
	protected String moduleId;
	
	/**
	 * Gets the class loader to use with this configuration
	 * 
	 * @return the class loader
	 */
	public ClassLoader getClassLoader() {
		return (moduleId != null) ? ModuleFactory.getModuleClassLoader(moduleId) : getClass().getClassLoader();
	}
	
	/**
	 * Gets the module id of the owner module
	 * 
	 * @return the module id
	 */
	public String getModuleId() {
		return moduleId;
	}
	
	/**
	 * Sets the module id of the owner module
	 * 
	 * @param moduleId the module id
	 */
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
}
