/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.ssemrws.web.controller;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * This class configured as controller using annotation and mapped with the URL of
 * 'module/${rootArtifactid}/${rootArtifactid}Link.form'.
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/ssemr")
public class SSEMRWebServicesController {
	
	public static final String enrolmentEncounterTypeUuid = "f469b65f-a4f6-4723-989a-46090de6a0e5";
	
	public static final String TRANSFER_IN_CONCEPT_UUID = "735cd395-0ef1-4832-a58c-e8afb567d3b3";
	
	public static final String CURRENTLY_BREASTFEEDING_CONCEPT_UUID = "e288fc7d-bbc5-479a-b94d-857e3819f926";
	
	private static final String CURRENTLY_PREGNANT_CONCEPT_UUID = "235a6246-6179-4309-ba84-6f0ec337eb48";
	
	public static final String CONCEPT_BY_UUID = "78763e68-104e-465d-8ce3-35f9edfb083d";
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	/**
	 * Gets a list of available/completed forms for a patient
	 * 
	 * @param request
	 * @param patientUuid
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/forms")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getAllAvailableFormsForVisit(HttpServletRequest request, @RequestParam("patientUuid") String patientUuid) {
		if (StringUtils.isBlank(patientUuid)) {
			return new ResponseEntity<Object>("You must specify patientUuid in the request!", new HttpHeaders(),
			        HttpStatus.BAD_REQUEST);
		}
		
		Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
		
		if (patient == null) {
			return new ResponseEntity<Object>("The provided patient was not found in the system!", new HttpHeaders(),
			        HttpStatus.NOT_FOUND);
		}
		
		List<Visit> activeVisits = Context.getVisitService().getActiveVisitsByPatient(patient);
		ArrayNode formList = JsonNodeFactory.instance.arrayNode();
		ObjectNode allFormsObj = JsonNodeFactory.instance.objectNode();
		
		if (!activeVisits.isEmpty()) {
			Visit patientVisit = activeVisits.get(0);
			
			/**
			 * {uuid: string; encounterType?: EncounterType; name: string; display: string; version:
			 * string; published: boolean; retired: boolean;}
			 */
			
			/*FormManager formManager = CoreContext.getInstance().getManager(FormManager.class);
			List<FormDescriptor> uncompletedFormDescriptors = formManager.getAllUncompletedFormsForVisit(patientVisit);
			
			if (!uncompletedFormDescriptors.isEmpty()) {
				
				for (FormDescriptor descriptor : uncompletedFormDescriptors) {
					if(!descriptor.getTarget().getRetired()) {
						ObjectNode formObj = generateFormDescriptorPayload(descriptor);
						formObj.put("formCategory", "available");
						formList.add(formObj);
					}
				}
				PatientWrapper patientWrapper = new PatientWrapper(patient);
				Encounter lastMchEnrollment = patientWrapper.lastEncounter(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT));
				if(lastMchEnrollment != null) {
					ObjectNode delivery = JsonNodeFactory.instance.objectNode();
					delivery.put("uuid", MCH_DELIVERY_FORM_UUID);
					delivery.put("name", "Delivery");
					delivery.put("display", "MCH Delivery Form");
					delivery.put("version", "1.0");
					delivery.put("published", true);
					delivery.put("retired", false);
					formList.add(delivery);
				}
				CalculationResult eligibleForDischarge = EmrCalculationUtils.evaluateForPatient(EligibleForMchmsDischargeCalculation.class, null, patient);
				if((Boolean) eligibleForDischarge.getValue() == true) {
					ObjectNode discharge = JsonNodeFactory.instance.objectNode();
					discharge.put("uuid", MCH_DISCHARGE_FORM_UUID);
					discharge.put("name", "Discharge");
					discharge.put("display", "MCH Discharge Form");
					discharge.put("version", "1.0");
					discharge.put("published", true);
					discharge.put("retired", false);
					formList.add(discharge);
				}
				ObjectNode labOrder = JsonNodeFactory.instance.objectNode();
				labOrder.put("uuid", LAB_ORDERS_FORM_UUID);
				labOrder.put("name", "Laboratory Test Orders");
				labOrder.put("display", "Laboratory Test Orders");
				labOrder.put("version", "1.0");
				labOrder.put("published", true);
				labOrder.put("retired", false);
				formList.add(labOrder);
			}*/
		}
		
		allFormsObj.put("results", formList);
		
		return allFormsObj.toString();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/newClients")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getNewPatients(HttpServletRequest request,
		 @RequestParam("startDate") @DateTimeFormat(pattern="dd-MM-yyyy") Date startDate,
		 @RequestParam("endDate") @DateTimeFormat(pattern="dd-MM-yyyy") Date endDate) {
		// Get all patients who were enrolled within the specified date range
		EncounterType enrolmentEncounterType = Context.getEncounterService().getEncounterTypeByUuid(enrolmentEncounterTypeUuid);
		// Add a filter for current location
		EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteria(
						        null, null, startDate, endDate, null, null, Collections.singletonList(enrolmentEncounterType), null, null, null,
		        false);
		List<Encounter> encounters = Context.getEncounterService().getEncounters(encounterSearchCriteria);
		// Extract patients from encounters into a hashset to remove duplicates
		HashSet<Patient> enrolledPatients = encounters.stream().map(Encounter::getPatient).collect(HashSet::new,
		        HashSet::add, HashSet::addAll);
		// Get Patients who were transferred in
		List<Obs> transferInObs = Context.getObsService().getObservations(null, encounters, Collections.singletonList(
		        Context.getConceptService().getConceptByUuid(TRANSFER_IN_CONCEPT_UUID)), Collections.singletonList(
						Context.getConceptService().getConceptByUuid("a2065636-5326-40f5-aed6-0cc2cca81ccc")), null,
		        null, null, null, null, null, endDate, false);
		// Extract patients from transfer in obs into a hashset to remove duplicates
		HashSet<Person> transferInPatients = transferInObs.stream().map(Obs::getPerson).collect(HashSet::new,
		        HashSet::add, HashSet::addAll);
		// Remove patients who were transferred in from the enrolled patients
		enrolledPatients.removeIf(transferInPatients::contains);
		
		return generatePatientListObj(enrolledPatients, endDate);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/activeClients")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getActivePatients(HttpServletRequest request) {
		List<Patient> allPatients = Context.getPatientService().getAllPatients(false);
		
		return generatePatientListObj(allPatients);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/dueForVl")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getPatientsDueForVl(HttpServletRequest request) {
		List<Patient> allPatients = Context.getPatientService().getAllPatients(false);
		
		return generatePatientListObj(allPatients);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/highVl")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getPatientsOnHighVl(HttpServletRequest request) {
		List<Patient> allPatients = Context.getPatientService().getAllPatients(false);
		
		return generatePatientListObj(allPatients);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/missedAppointment")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getPatientsMissedAppointment(HttpServletRequest request) {
		List<Patient> allPatients = Context.getPatientService().getAllPatients(false);
		
		return generatePatientListObj(allPatients);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/interruptedInTreatment")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getPatientsInterruptedInTreatment(HttpServletRequest request) {
		List<Patient> allPatients = Context.getPatientService().getAllPatients(false);
		
		return generatePatientListObj(allPatients);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/returnedToTreatment")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getPatientsReturnedToTreatment(HttpServletRequest request) {
		List<Patient> allPatients = Context.getPatientService().getAllPatients(false);
		// Add logic to filter patients who have returned to treatment
		
		return generatePatientListObj(allPatients);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/adultRegimenTreatment")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getPatientsOnAdultRegimenTreatment(HttpServletRequest request) {
		List<Patient> allPatients = Context.getPatientService().getAllPatients(false);
		// Add logic to filter patients on Adult regimen treatment
		
		return generatePatientListObj(allPatients);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/childRegimenTreatment")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getPatientsOnChildRegimenTreatment(HttpServletRequest request) {
		List<Patient> allPatients = Context.getPatientService().getAllPatients(false);
		// Add logic to filter patients on Child regimen treatment
		
		return generatePatientListObj(allPatients);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/underCareOfCommunityProgrammes")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getPatientsUnderCareOfCommunityProgrammes(HttpServletRequest request) {
		List<Patient> allPatients = Context.getPatientService().getAllPatients(false);
		// Add logic to filter patients on Child regimen treatment
		
		return generatePatientListObj(allPatients);
	}
	
	// Dummy method to be removed
	private Object generatePatientListObj(Collection<Patient> allPatients) {
		return generatePatientListObj((HashSet<Patient>) allPatients, new Date());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/viralLoadSamplesCollected")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getViralLoadSamplesCollected(HttpServletRequest request) {
		List<Patient> allPatients = Context.getPatientService().getAllPatients(false);
		// Add logic to filter patients on Child regimen treatment
		
		return generateViralLoadListObj(allPatients);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/viralLoadResults")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getViralLoadResults(HttpServletRequest request) {
		List<Patient> allPatients = Context.getPatientService().getAllPatients(false);
		// Add logic to filter patients on Child regimen treatment
		
		return generateViralLoadListObj(allPatients);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/viralLoadCoverage")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getViralLoadCoverage(HttpServletRequest request) {
		List<Patient> allPatients = Context.getPatientService().getAllPatients(false);
		// Add logic to filter patients on Child regimen treatment
		
		return generateViralLoadListObj(allPatients);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/viralLoadSuppression")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getViralLoadSuppression(HttpServletRequest request) {
		List<Patient> allPatients = Context.getPatientService().getAllPatients(false);
		// Add logic to filter patients on Child regimen treatment
		
		return generateViralLoadListObj(allPatients);
	}
	
	private Object generatePatientListObj(HashSet<Patient> allPatients, Date endDate) {
		ArrayNode patientList = JsonNodeFactory.instance.arrayNode();
		ObjectNode allPatientsObj = JsonNodeFactory.instance.objectNode();
		
		for (Patient patient : allPatients) {
			ObjectNode patientObj = JsonNodeFactory.instance.objectNode();
			patientObj.put("uuid", patient.getUuid());
			patientObj.put("name", patient.getPersonName() != null ? patient.getPersonName().toString() : "");
			patientObj.put("identifier", patient.getPatientIdentifier() != null ? patient.getPatientIdentifier().toString()
			        : "");
			patientObj.put("sex", patient.getGender());
			patientObj.put("dateEnrolled", dateTimeFormatter.format(new Date()));
			// Calculate age in years based on patient's birthdate and current date
			Date birthdate = patient.getBirthdate();
			Date currentDate = new Date();
			long age = (currentDate.getTime() - birthdate.getTime()) / (1000L * 60 * 60 * 24 * 365);
			patientObj.put("childOrAdolescent", age <= 19 ? "True" : "False");
			patientObj.put("pregnantAndBreastfeeding", determineIfPatientIsPregnantOrBreastfeeding(patient, endDate));
			patientObj.put("returningFromIT", determineIfPatientIsReturningFromIT(patient));
			patientObj.put("returningToTreatment", determineIfPatientIsReturningToTreatment(patient));
			patientList.add(patientObj);
		}
		ObjectNode groupingObj = JsonNodeFactory.instance.objectNode();
		ObjectNode groupYear = JsonNodeFactory.instance.objectNode();
		ObjectNode groupMonth = JsonNodeFactory.instance.objectNode();
		ObjectNode groupWeek = JsonNodeFactory.instance.objectNode();
		
		groupYear.put("Jan", 10);
		groupYear.put("Feb", 10);
		groupYear.put("Mar", 10);
		groupYear.put("Apr", 10);
		
		groupMonth.put("Week1", 10);
		groupMonth.put("Week2", 10);
		groupMonth.put("Week3", 10);
		groupMonth.put("Week4", 10);
		
		groupWeek.put("Mon", 10);
		groupWeek.put("Tue", 10);
		groupWeek.put("Wed", 10);
		groupWeek.put("Thu", 10);
		groupWeek.put("Fri", 10);
		
		groupingObj.put("groupYear", groupYear);
		groupingObj.put("groupMonth", groupMonth);
		groupingObj.put("groupWeek", groupWeek);
		
		allPatientsObj.put("results", patientList);
		allPatientsObj.put("summary", groupingObj);
		
		return allPatientsObj.toString();
	}
	
	private boolean determineIfPatientIsReturningToTreatment(Patient patient) {
		// Add logic to determine if patient is returning to treatment
		// This is the definition of patients returning to treatment:
		// Clients who experienced an interruption in treatment (IIT) during any previous reporting period, who successfully restarted ARVs within the reporting period and remained on treatment until the end of the reporting period."
		return false;
	}
	
	private boolean determineIfPatientIsReturningFromIT(Patient patient) {
		// Add logic to determine if patient is returning from IT
		// This is the definition of patients returning from IT:
		// clients who missed for at least 28 days from the last expected return visit date"
		return false;
	}
	
	private boolean determineIfPatientIsPregnantOrBreastfeeding(Patient patient, Date endDate) {
		
		List<Concept> pregnantAndBreastfeedingConcepts = new ArrayList<>();
		pregnantAndBreastfeedingConcepts.add(Context.getConceptService().getConceptByUuid(CURRENTLY_BREASTFEEDING_CONCEPT_UUID));
		pregnantAndBreastfeedingConcepts.add(Context.getConceptService().getConceptByUuid(CURRENTLY_PREGNANT_CONCEPT_UUID));
		
		List<Obs> obsList = Context.getObsService().getObservations(Collections.singletonList(patient), null, pregnantAndBreastfeedingConcepts, Collections.singletonList(Context.getConceptService().getConceptByUuid(
						CONCEPT_BY_UUID)), null, null,
		        null, null, null, null, endDate, false);
		
		return !obsList.isEmpty();
	}
	
	private Object generateViralLoadListObj(List<Patient> allPatients) {
		// The expected output for this method should resemble this JSON output
		// [{
		//"Jan"":[{""patient1""},{""patient2""}...],
		//"Feb"":[{""patient1""},{""patient2""}...],
		//"Mar"":[{""patient1""},{""patient2""}...],
		//"Apr"":[{""patient1""},{""patient2""}...],
		//"May"":[{""patient1""},{""patient2""}...],
		//"Jun"":[{""patient1""},{""patient2""}...],
		//}]"
		//}
		// ArrayNode patientList = JsonNodeFactory.instance.arrayNode();
		// ObjectNode allPatientsObj = JsonNodeFactory.instance.objectNode();
		
		return new ArrayList<>();
	}
}
