package org.openmrs.module.ssemrws;

import org.openmrs.module.ssemrws.form.FormDescriptor;
import org.openmrs.module.ssemrws.program.ProgramDescriptor;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class FormConfiguration extends AbstractContentConfiguration {
	
	private Set<FormDescriptor> commonPatientForms;
	
	private Set<FormDescriptor> commonVisitForms;
	
	private Map<ProgramDescriptor, Set<FormDescriptor>> programPatientForms;
	
	private Map<ProgramDescriptor, Set<FormDescriptor>> programVisitForms;
	
	private Set<FormDescriptor> disabledForms;
	
	/**
	 * Gets the common per-patient forms
	 * 
	 * @return the form descriptors
	 */
	public Set<FormDescriptor> getCommonPatientForms() {
		if (commonPatientForms == null) {
			commonPatientForms = new LinkedHashSet<FormDescriptor>();
		}
		
		return commonPatientForms;
	}
	
	/**
	 * Sets the common per-patient forms
	 * 
	 * @param commonPatientForms the form descriptors
	 */
	public void setCommonPatientForms(Set<FormDescriptor> commonPatientForms) {
		this.commonPatientForms = commonPatientForms;
	}
	
	/**
	 * Gets the general pre-visit forms
	 * 
	 * @return the form descriptors
	 */
	public Set<FormDescriptor> getCommonVisitForms() {
		if (commonVisitForms == null) {
			commonVisitForms = new LinkedHashSet<FormDescriptor>();
		}
		
		return commonVisitForms;
	}
	
	/**
	 * Sets the general per-visit forms
	 * 
	 * @param commonVisitForms the form descriptors
	 */
	public void setCommonVisitForms(Set<FormDescriptor> commonVisitForms) {
		this.commonVisitForms = commonVisitForms;
	}
	
	/**
	 * Gets the program specific per-patient forms
	 * 
	 * @return the map of program and form descriptors
	 */
	public Map<ProgramDescriptor, Set<FormDescriptor>> getProgramPatientForms() {
		if (programPatientForms == null) {
			programPatientForms = new LinkedHashMap<ProgramDescriptor, Set<FormDescriptor>>();
		}
		
		return programPatientForms;
	}
	
	/**
	 * Sets the program specific per-patient forms
	 * 
	 * @param programPatientForms the map of program and form descriptors
	 */
	public void setProgramPatientForms(Map<ProgramDescriptor, Set<FormDescriptor>> programPatientForms) {
		this.programPatientForms = programPatientForms;
	}
	
	/**
	 * Gets the program specific per-visit forms
	 * 
	 * @return the map of program and form descriptors
	 */
	public Map<ProgramDescriptor, Set<FormDescriptor>> getProgramVisitForms() {
		if (programVisitForms == null) {
			programVisitForms = new LinkedHashMap<ProgramDescriptor, Set<FormDescriptor>>();
		}
		
		return programVisitForms;
	}
	
	/**
	 * Sets the program specific per-visit forms
	 * 
	 * @param programVisitForms the map of program and form descriptors
	 */
	public void setProgramVisitForms(Map<ProgramDescriptor, Set<FormDescriptor>> programVisitForms) {
		this.programVisitForms = programVisitForms;
	}
	
	/**
	 * Gets the forms to be disabled
	 * 
	 * @return the form descriptors
	 */
	public Set<FormDescriptor> getDisabledForms() {
		if (disabledForms == null) {
			disabledForms = new LinkedHashSet<FormDescriptor>();
		}
		
		return disabledForms;
	}
	
	/**
	 * Sets the forms to be disabled
	 * 
	 * @param disabledForms the form descriptors
	 */
	public void setDisabledForms(Set<FormDescriptor> disabledForms) {
		this.disabledForms = disabledForms;
	}
}
