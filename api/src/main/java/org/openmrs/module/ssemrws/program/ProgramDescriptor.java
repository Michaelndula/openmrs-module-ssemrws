package org.openmrs.module.ssemrws.program;

import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.ssemrws.AbstractEntityDescriptor;
import org.openmrs.module.ssemrws.UiResource;
import org.openmrs.module.ssemrws.form.FormDescriptor;
import org.openmrs.module.ssemrws.report.ReportDescriptor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProgramDescriptor extends AbstractEntityDescriptor<Program> {
	
	private Class<? extends PatientCalculation> eligibilityCalculation;
	
	private FormDescriptor defaultEnrollmentForm;
	
	private FormDescriptor defaultCompletionForm;
	
	private Set<FormDescriptor> patientForms;
	
	private Set<FormDescriptor> visitForms;
	
	private Set<ReportDescriptor> reports;
	
	private Map<String, UiResource> fragments;
	
	/**
	 * @see org.openmrs.module.ssemrws.AbstractEntityDescriptor#getTarget()
	 */
	@Override
	public Program getTarget() {
		return MetadataUtils.existing(Program.class, targetUuid);
	}
	
	/**
	 * Gets the eligibility calculation class
	 * 
	 * @return the eligibility calculation class
	 */
	public Class<? extends PatientCalculation> getEligibilityCalculation() {
		return eligibilityCalculation;
	}
	
	/**
	 * Sets the eligibility calculation class
	 * 
	 * @param eligibilityCalculation the eligibility calculation class
	 */
	public void setEligibilityCalculation(Class<? extends PatientCalculation> eligibilityCalculation) {
		this.eligibilityCalculation = eligibilityCalculation;
	}
	
	/**
	 * Gets the default enrollment form
	 * 
	 * @return the form
	 */
	public FormDescriptor getDefaultEnrollmentForm() {
		return defaultEnrollmentForm;
	}
	
	/**
	 * Sets the default enrollment form
	 * 
	 * @param defaultEnrollmentForm the form
	 */
	public void setDefaultEnrollmentForm(FormDescriptor defaultEnrollmentForm) {
		this.defaultEnrollmentForm = defaultEnrollmentForm;
	}
	
	/**
	 * Gets the default completion form
	 * 
	 * @return the form
	 */
	public FormDescriptor getDefaultCompletionForm() {
		return defaultCompletionForm;
	}
	
	/**
	 * Sets the default completion form
	 * 
	 * @param defaultCompletionForm the form
	 */
	public void setDefaultCompletionForm(FormDescriptor defaultCompletionForm) {
		this.defaultCompletionForm = defaultCompletionForm;
	}
	
	/**
	 * Gets the per-patient forms
	 * 
	 * @return the patient forms
	 */
	public Set<FormDescriptor> getPatientForms() {
		return patientForms;
	}
	
	/**
	 * Sets the per-patient forms
	 * 
	 * @param patientForms the patient forms
	 */
	public void setPatientForms(Set<FormDescriptor> patientForms) {
		this.patientForms = patientForms;
	}
	
	/**
	 * Adds an additional per-patient form
	 * 
	 * @param patientForm the patient form
	 */
	public void addPatientForm(FormDescriptor patientForm) {
		if (patientForms == null) {
			patientForms = new HashSet<FormDescriptor>();
		}
		
		patientForms.add(patientForm);
	}
	
	/**
	 * Gets the per-visit forms
	 * 
	 * @return the visit forms
	 */
	public Set<FormDescriptor> getVisitForms() {
		return visitForms;
	}
	
	/**
	 * Sets the per-visit forms
	 * 
	 * @param visitForms the visit forms
	 */
	public void setVisitForms(Set<FormDescriptor> visitForms) {
		this.visitForms = visitForms;
	}
	
	/**
	 * Adds an additional per-visit form
	 * 
	 * @param visitForm the visit form
	 */
	public void addVisitForm(FormDescriptor visitForm) {
		if (visitForms == null) {
			visitForms = new HashSet<FormDescriptor>();
		}
		
		visitForms.add(visitForm);
	}
	
	/**
	 * Gets the reports
	 * 
	 * @return the reports
	 */
	public Set<ReportDescriptor> getReports() {
		return reports;
	}
	
	/**
	 * Sets the reports
	 * 
	 * @param reports the reports
	 */
	public void setReports(Set<ReportDescriptor> reports) {
		this.reports = reports;
	}
	
	/**
	 * Adds an additional report
	 * 
	 * @param report the report
	 */
	public void addReport(ReportDescriptor report) {
		if (reports == null) {
			reports = new HashSet<ReportDescriptor>();
		}
		
		reports.add(report);
	}
	
	/**
	 * Gets the fragments
	 * 
	 * @return the fragment resources
	 */
	public Map<String, UiResource> getFragments() {
		return fragments;
	}
	
	/**
	 * Sets the fragments
	 * 
	 * @param fragments the fragment resources
	 */
	public void setFragments(Map<String, UiResource> fragments) {
		this.fragments = fragments;
	}
}
