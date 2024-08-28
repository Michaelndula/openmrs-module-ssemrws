package org.openmrs.module.ssemrws;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openmrs.OpenmrsObject;

public abstract class AbstractEntityDescriptor<T extends OpenmrsObject> extends AbstractOrderedDescriptor {
	
	protected String targetUuid;
	
	/**
	 * Gets the target object UUID
	 * 
	 * @return the target object UUID
	 */
	public String getTargetUuid() {
		return targetUuid;
	}
	
	/**
	 * Sets the target object UUID
	 * 
	 * @param targetUuid the target object UUID
	 */
	public void setTargetUuid(String targetUuid) {
		this.targetUuid = targetUuid;
	}
	
	/**
	 * Gets the target object
	 * 
	 * @return the target object
	 */
	public abstract T getTarget();
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("targetUuid", targetUuid).append("enabled", enabled)
		        .toString();
	}
}
