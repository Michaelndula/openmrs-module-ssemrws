package org.openmrs.module.ssemrws.form;

import org.openmrs.Form;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.ssemrws.AbstractEntityDescriptor;
import org.openmrs.module.ssemrws.UiResource;
import org.openmrs.module.ssemrws.app.AppRestrictedDescriptor;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Set;

public class FormDescriptor extends AbstractEntityDescriptor<Form> implements AppRestrictedDescriptor {
	
	/**
	 * Possible gender usages for a form
	 */
	public enum Gender {
		BOTH,
		MALE,
		FEMALE
	}
	
	private Class<? extends PatientCalculation> showIfCalculation;
	
	private Set<AppDescriptor> apps;
	
	private Gender gender = Gender.BOTH;
	
	private String autoCreateVisitTypeUuid;
	
	private UiResource icon;
	
	private UiResource htmlform;
	
	/**
	 * @see org.openmrs.module.ssemrws.AbstractEntityDescriptor#getTarget()
	 */
	@Override
	public Form getTarget() {
		return MetadataUtils.existing(Form.class, targetUuid);
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
	 * Gets the eligibility calculation class
	 * 
	 * @return the eligibility calculation class
	 */
	public Class<? extends PatientCalculation> getShowIfCalculation() {
		return showIfCalculation;
	}
	
	/**
	 * Sets the eligibility calculation class
	 * 
	 * @param showIfCalculation the eligibility calculation class
	 */
	public void setShowIfCalculation(Class<? extends PatientCalculation> showIfCalculation) {
		this.showIfCalculation = showIfCalculation;
	}
	
	/**
	 * Gets the gender usage
	 * 
	 * @return the gender usage
	 */
	public Gender getGender() {
		return gender;
	}
	
	/**
	 * Sets the gender usage
	 * 
	 * @param gender the gender usage
	 */
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	
	/**
	 * Sets the auto-create visit type UUID
	 * 
	 * @return the visit type UUID
	 */
	public String getAutoCreateVisitTypeUuid() {
		return autoCreateVisitTypeUuid;
	}
	
	/**
	 * Gets the auto-create visit type UUID
	 * 
	 * @param autoCreateVisitTypeUuid the visit type UUID
	 */
	public void setAutoCreateVisitTypeUuid(String autoCreateVisitTypeUuid) {
		this.autoCreateVisitTypeUuid = autoCreateVisitTypeUuid;
	}
	
	/**
	 * Gets the icon resource
	 * 
	 * @return the icon
	 */
	public UiResource getIcon() {
		return icon;
	}
	
	/**
	 * Sets the icon resource
	 * 
	 * @param icon the icon
	 */
	public void setIcon(UiResource icon) {
		this.icon = icon;
	}
	
	/**
	 * Gets the htmlform resource
	 * 
	 * @return the htmlform
	 */
	public UiResource getHtmlform() {
		return htmlform;
	}
	
	/**
	 * Sets the htmlform resource
	 * 
	 * @param htmlform the htmlform
	 */
	public void setHtmlform(UiResource htmlform) {
		this.htmlform = htmlform;
	}
	
}
