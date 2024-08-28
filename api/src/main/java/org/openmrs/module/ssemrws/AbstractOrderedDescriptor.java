package org.openmrs.module.ssemrws;

import org.openmrs.util.OpenmrsUtil;

public abstract class AbstractOrderedDescriptor extends AbstractDescriptor implements Comparable<AbstractOrderedDescriptor> {
	
	protected Integer order;
	
	/**
	 * Gets the order
	 * 
	 * @return the order
	 */
	public Integer getOrder() {
		return order;
	}
	
	/**
	 * Sets the order
	 * 
	 * @param order the order
	 */
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(AbstractOrderedDescriptor descriptor) {
		int byOrder = OpenmrsUtil.compareWithNullAsGreatest(order, descriptor.order);
		
		// Return by id if order is equal. Important to not return zero from this method
		// unless the objects
		// *are* actually equal. Otherwise TreeSet sees them as duplicates.
		return byOrder != 0 ? byOrder : id.compareTo(descriptor.id);
	}
}
