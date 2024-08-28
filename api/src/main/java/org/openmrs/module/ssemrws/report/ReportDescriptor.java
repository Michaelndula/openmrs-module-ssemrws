package org.openmrs.module.ssemrws.report;

import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.ssemrws.AbstractEntityDescriptor;
import org.openmrs.module.ssemrws.app.AppRestrictedDescriptor;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;

import java.util.Set;

public class ReportDescriptor extends AbstractEntityDescriptor<ReportDefinition> implements AppRestrictedDescriptor {
	
	protected String name;
	
	protected String description;
	
	protected Set<AppDescriptor> apps;
	
	/**
	 * @see org.openmrs.module.ssemrws.AbstractEntityDescriptor#getTarget()
	 */
	@Override
	public ReportDefinition getTarget() {
		return Context.getService(ReportDefinitionService.class).getDefinitionByUuid(targetUuid);
	}
	
	/**
	 * Gets the name
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name
	 * 
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the description
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description
	 * 
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @see org.openmrs.module.ssemrws.app.AppRestrictedDescriptor#getApps()
	 */
	@Override
	public Set<AppDescriptor> getApps() {
		return apps;
	}
	
	/**
	 * @see org.openmrs.module.ssemrws.app.AppRestrictedDescriptor#setApps(java.util.Set)
	 */
	@Override
	public void setApps(Set<AppDescriptor> apps) {
		this.apps = apps;
	}
	
	/**
	 * Gets a definition summary
	 */
	public DefinitionSummary getDefinitionSummary() {
		DefinitionSummary ret = new DefinitionSummary();
		ret.setName(getName());
		ret.setDescription(getDescription());
		ret.setUuid(id);
		return ret;
	}
}
