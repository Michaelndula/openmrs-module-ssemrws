package org.openmrs.module.ssemrws;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.BeanNameAware;

public abstract class AbstractDescriptor implements BeanNameAware, Descriptor {
	
	protected String id;
	
	protected boolean enabled = true;
	
	/**
	 * @see org.openmrs.module.ssemrws.Descriptor#getId()
	 */
	@Override
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the descriptor id
	 * 
	 * @param id the id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Gets whether enabled
	 * 
	 * @return true if enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Sets whether enabled
	 * 
	 * @param enabled true if enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * @see org.springframework.beans.factory.BeanNameAware#setBeanName(String)
	 */
	@Override
	public void setBeanName(String id) {
		setId(id);
	}
	
	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof AbstractDescriptor))
			return false;
		
		AbstractDescriptor that = (AbstractDescriptor) o;
		
		return id.equals(that.id);
	}
	
	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("enabled", enabled).toString();
	}
}
