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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.util.PropertySource;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.*;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ssemrws.web.dto.PatientObservations;
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

/**
 * This class configured as controller using annotation and mapped with the URL of
 * 'module/${rootArtifactid}/${rootArtifactid}Link.form'.
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/ssemr")
public class SSEMRWebServicesController {
	
	public static final String VL_LAB_REQUEST_ENCOUNTER_TYPE = "82024e00-3f10-11e4-adec-0800271c1b75";
	
	public static final String FOLLOW_UP_FORM_ENCOUNTER_TYPE = "e8481555-9dd1-4bb5-ba8c-cb721dafb166";
	
	public static final String PERSONAL_FAMILY_HISTORY_ENCOUNTERTYPE_UUID = "0e9f540d-92cb-43c9-a95c-9407f5bf3f2a";
	
	public static final String SAMPLE_COLLECTION_DATE_UUID = "ed520e2d-acb4-4ea9-8ae5-16ca27ace96d";
	
	public static final String YES_CONCEPT = "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public static final String LAST_REFILL_DATE_UUID = "494915d8-4cf3-4c24-9efa-371710992cb3";
	
	public static final String VIRAL_LOAD_CONCEPT_UUID = "01c3ce55-b7eb-45f5-93d5-bace353e3cfd";
	
	public static final String RETURNING_TO_TREATMENT_UUID = "4913c7f0-3362-4407-8d48-4b115f2f59dd";
	
	public static final String DATE_INTERRUPTION_IN_TREATMENT_CONCEPT_UUID = "84c23dc4-40f4-4d9a-a2f5-ebeb4b4f3250";
	
	public static final String DATE_OF_LAST_VISIT_CONCEPT_UUID = "773cd838-7b21-4f34-9b33-0a071140f817";
	
	public static final String ART_TREATMENT_INTURRUPTION_ENCOUNTER_TYPE_UUID = "81852aee-3f10-11e4-adec-0800271c1b75";
	
	public static final String ACTIVE_REGIMEN_CONCEPT_UUID = "23322fd6-3dbb-410e-8bee-6210dfcd5f71";
	
	public static final String COMMUNITY_LINKAGE_ENCOUNTER_UUID = "3c2df02e-6856-11ee-8c99-0242ac120002";
	
	public static final String DATE_OF_ENROLLMENT_UUID = "e27f8561-e242-4744-9193-b84d752dd86d";
	
	public static final String DATE_APPOINTMENT_SCHEDULED_CONCEPT_UUID = "e605731b-2e81-41a9-8446-2ed442c339e2";
	
	public static final String PEDIATRIC_INTAKE_FORM = "356def6a-fa66-4a78-97d5-b43154064875";
	
	public static final String ADULT_AND_ADOLESCENT_INTAKE_FORM = "b645dbdd-7d58-41d4-9b11-eeff023b8ee5";
	
	public static final String regimen_1A = "9062c6d9-a650-44d2-8929-da84f617c427";
	
	public static final String regimen_1B = "c22e6700-a937-4909-b4ad-e82ff51325ac";
	
	public static final String regimen_1C = "a00d9620-e88b-4c2f-9293-b1ac9e5943f2";
	
	public static final String regimen_1D = "5d500ca2-350a-49ee-a3d4-f340db32ff31";
	
	public static final String regimen_1E = "3fd242a8-7ade-463c-8919-d82573ea8526";
	
	public static final String regimen_1F = "91f5d0d6-eb81-484f-9376-4bfb926a5a81";
	
	public static final String regimen_1G = "0dd3e78f-e1fc-47de-95bc-1f489d0dfcc5";
	
	public static final String regimen_1H = "4a86fbee-07a9-422a-bf69-16256c0c2b8b";
	
	public static final String regimen_1J = "03224cae-f115-4814-bd53-c99c72288446";
	
	public static final String regimen_2A = "bd97cacd-4a91-4901-8803-3a4a2e5f1ca8";
	
	public static final String regimen_2B = "ad6ff4ef-769e-4aec-b8bb-7f033fe6aaaa";
	
	public static final String regimen_2C = "47167ec1-4957-4d0a-a58c-3894bdeb93ff";
	
	public static final String regimen_2D = "e649a0ec-e193-4af0-bb49-02687107a893";
	
	public static final String regimen_2E = "accca537-b8ee-41ec-b902-7de814d099b2";
	
	public static final String regimen_2F = "77f201fc-aefc-4068-baa7-cb3284782a38";
	
	public static final String regimen_2G = "cb0f9fcd-fb52-493f-95aa-d0197387fbdb";
	
	public static final String regimen_2H = "28790bde-81db-4490-806b-ac10c17b41dc";
	
	public static final String regimen_2I = "aae69cae-2806-4e8b-a916-f22ed733a19b";
	
	public static final String regimen_2J = "64336206-c9bc-4d37-accf-c7abac7a37f6";
	
	public static final String regimen_2K = "25f0cca5-902d-4e36-9e4f-5ce5da744a75";
	
	public static final String regimen_4A = "c224b116-27ec-4156-93ba-d4838a3ac1c0";
	
	public static final String regimen_4B = "5efe8d99-c65e-4136-8820-5f3646437ff7";
	
	public static final String regimen_4C = "f8f64be8-ccb4-404d-b99e-3c4975155da5";
	
	public static final String regimen_4D = "28c5d192-ba71-4ef5-8604-ecf6bd177126";
	
	public static final String regimen_4E = "3eaf04dc-e284-42f7-860e-02cda37cf230";
	
	public static final String regimen_4F = "0372f3fb-5e8a-474f-8250-01af7a485778";
	
	public static final String regimen_4G = "ce2412c4-a041-4328-bfaa-35e041ca4802";
	
	public static final String regimen_4H = "6ed47806-8809-4c5a-a1b6-fe2ec0158563";
	
	public static final String regimen_4I = "f6b1c6ea-b0a2-46a0-b7e0-3038d268356c";
	
	public static final String regimen_4J = "2e1ab9d3-7fe1-48ba-a12c-fd8d26bc161d";
	
	public static final String regimen_4K = "99f54f96-e761-4d86-bb1b-0abc2a24fa16";
	
	public static final String regimen_4L = "5287f2a4-23e5-4314-b60c-0a4b91753ec6";
	
	public static final String regimen_5A = "b23cf614-dfec-48c9-a12f-ba577e28347d";
	
	public static final String regimen_5B = "dabc93c3-8c3d-41e1-b3e3-d7e14c4765b6";
	
	public static final String regimen_5C = "da3c6710-c431-4582-a444-a466d54693ec";
	
	public static final String regimen_5D = "6c383d11-2b29-4cc2-bfa4-811ff7a988f1";
	
	public static final String regimen_5E = "82725d14-00c6-4864-bf8b-ad5db0b3c3fa";
	
	public static final String regimen_5F = "140ede93-5691-463b-9d17-2dc8834621f8";
	
	public static final String regimen_5G = "06017ac1-2ce8-4689-a3bf-4e9f3d54978f";
	
	public static final String regimen_5H = "2c0a5b91-7b2a-4f8e-86fd-a8007841fca8";
	
	public static final String regimen_5I = "50b60d77-186d-4a0d-8784-659ee2d60ec9";
	
	public static final String regimen_5J = "78e49624-0e33-4374-93b7-60b132b26dae";
	
	public static final String HIGH_VL_ENCOUNTERTYPE_UUID = "f7f1c854-69e5-11ee-8c99-0242ac120002";
	
	public static final String BREASTFEEDING_CONCEPT_UUID = "cf5c7deb-f67c-4406-82ea-d619e502f47c";
	
	public static final String PREGNANT_CONCEPT_UUID = "5dcb1bc9-ee89-4b57-9493-a1f245c5ee8b";
	
	public static final String PMTCT_CONCEPT_UUID = "da20f4fc-94b4-421c-896c-cf16a834227a";
	
	public static final String EAC_SESSION_CONCEPT_UUID = "65536958-fc01-4002-8839-af4b6ab0489b";
	
	public static final String EXTENDED_EAC_CONCEPT_UUID = "99c7c0f1-3c7c-4e26-bf4b-60a74734bc7c";
	
	public static final String VIRAL_LOAD_RESULTS_UUID = "8b5ef5c4-3c88-49b8-87e5-cb8d30caa77d";
	
	public static final String FIRST_EAC_SESSION = "37f1e487-24f7-48f3-a347-391e877152e4";
	
	public static final String SECOND_EAC_SESSION = "348c5147-4e54-420a-a035-b322e1ba0b6e";
	
	public static final String THIRD_EAC_SESSION = "60966dae-3999-42b9-aec1-6161cebb22c1";
	
	public static final String REAPEAT_VL_COLLECTION = "06cee3e6-daaa-48cf-aa53-296254ee61a3";
	
	public static final String REPEAT_VL_RESULTS = "68c60487-62b5-45af-9773-0bc163b9e076";
	
	public static final String END_OF_FOLLOW_UP_ENCOUTERTYPE_UUID = "3bf40d2b-c8a2-4a7d-9da2-adb33860e0f8";
	
	public static final String TRANSFERRED_OUT_CONCEPT_UUID = "68f68ae1-272c-44e4-85af-009e46e60015";
	
	public static final String DATE_OF_ART_INITIATION_CONCEPT_UUID = "e27f8561-e242-4744-9193-b84d752dd86d";
	
	public static final String DECEASED_CONCEPT_UUID = "417b7273-8d62-4720-9fb6-075e9d1530ec";
	
	public static final String LAST_CD4_COUNT_UUID = "809dd0f5-ce54-441c-b835-a2a8b06a6140";
	
	public static final String TB_STATUS_CONCEPT_UUID = "9a1a731e-937b-46fe-aca2-acf83a326b71";
	
	public static final String ARV_REGIMEN_DOSE_UUID = "3fa33347-9871-497b-81f3-7a791b9f4bae";
	
	public static final String WHO_CLINICAL_UUID = "e38e6ce2-92d9-4382-927b-5555ff9fa850";
	
	public static final String DATE_VL_RESULTS_RECEIVED_UUID = "80e34f1b-26e8-49ea-9b6e-d7d903a91e26";
	
	public static final String CHW_NAME_UUID = "5b258179-1872-4339-8b29-5ecf618bd4d3";
	
	public static final String CHW_PHONE_UUID = "cb3385ee-18c3-4856-a410-c7b4d105a779";
	
	public static final String CHW_ADDRESS_UUID = "77a35b01-7713-437a-a82b-7f74c75ea41d";
	
	public static final String BDL_CONCEPT_UUID = "62df0577-a1f7-493e-80d2-e458998f1a2f";
	
	public static final String BMI_CONCEPT_UUID = "1342AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public static final String MUAC_CONCEPT_UUID = "1343AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public static final String WHO_CLINICAL_STAGE_INTAKE_UUID = "a90652b2-f2c7-471f-a287-64495d2a6791";
	
	// Create Enum of the following filter categories: CHILDREN_ADOLESCENTS,
	// PREGNANT_BREASTFEEDING, RETURN_FROM_IIT, RETURN_TO_TREATMENT
	public enum filterCategory {
		CHILDREN_ADOLESCENTS,
		PREGNANT_BREASTFEEDING,
		IIT,
		RETURN_TO_TREATMENT
	};
	
	public static final String CURRENTLY_BREASTFEEDING_CONCEPT_UUID = "e288fc7d-bbc5-479a-b94d-857e3819f926";
	
	private static final String CURRENTLY_PREGNANT_CONCEPT_UUID = "235a6246-6179-4309-ba84-6f0ec337eb48";
	
	public static final String CONCEPT_BY_UUID = "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	private static final double THRESHOLD = 1000.0;
	
	private static final int SIX_MONTHS_IN_DAYS = 183;
	
	public static final Set<String> FIRST_LINE_REGIMENS = new HashSet<>(
	        Arrays.asList("9062c6d9-a650-44d2-8929-da84f617c427", "c22e6700-a937-4909-b4ad-e82ff51325ac",
	            "91f5d0d6-eb81-484f-9376-4bfb926a5a81", "0dd3e78f-e1fc-47de-95bc-1f489d0dfcc5",
	            "4a86fbee-07a9-422a-bf69-16256c0c2b8b", "03224cae-f115-4814-bd53-c99c72288446",
	            "c224b116-27ec-4156-93ba-d4838a3ac1c0", "5efe8d99-c65e-4136-8820-5f3646437ff7",
	            "f8f64be8-ccb4-404d-b99e-3c4975155da5", "28c5d192-ba71-4ef5-8604-ecf6bd177126",
	            "3eaf04dc-e284-42f7-860e-02cda37cf230", "0372f3fb-5e8a-474f-8250-01af7a485778",
	            "ce2412c4-a041-4328-bfaa-35e041ca4802", "6ed47806-8809-4c5a-a1b6-fe2ec0158563",
	            "f6b1c6ea-b0a2-46a0-b7e0-3038d268356c", "2e1ab9d3-7fe1-48ba-a12c-fd8d26bc161d",
	            "99f54f96-e761-4d86-bb1b-0abc2a24fa16", "5287f2a4-23e5-4314-b60c-0a4b91753ec6"));
	
	public static final Set<String> SECOND_LINE_REGIMENS = new HashSet<>(
	        Arrays.asList("bd97cacd-4a91-4901-8803-3a4a2e5f1ca8", "ad6ff4ef-769e-4aec-b8bb-7f033fe6aaaa",
	            "47167ec1-4957-4d0a-a58c-3894bdeb93ff", "e649a0ec-e193-4af0-bb49-02687107a893",
	            "accca537-b8ee-41ec-b902-7de814d099b2", "77f201fc-aefc-4068-baa7-cb3284782a38",
	            "cb0f9fcd-fb52-493f-95aa-d0197387fbdb", "28790bde-81db-4490-806b-ac10c17b41dc",
	            "aae69cae-2806-4e8b-a916-f22ed733a19b", "64336206-c9bc-4d37-accf-c7abac7a37f6",
	            "25f0cca5-902d-4e36-9e4f-5ce5da744a75", "b23cf614-dfec-48c9-a12f-ba577e28347d",
	            "dabc93c3-8c3d-41e1-b3e3-d7e14c4765b6", "da3c6710-c431-4582-a444-a466d54693ec",
	            "6c383d11-2b29-4cc2-bfa4-811ff7a988f1", "82725d14-00c6-4864-bf8b-ad5db0b3c3fa",
	            "140ede93-5691-463b-9d17-2dc8834621f8", "06017ac1-2ce8-4689-a3bf-4e9f3d54978f",
	            "2c0a5b91-7b2a-4f8e-86fd-a8007841fca8", "50b60d77-186d-4a0d-8784-659ee2d60ec9",
	            "78e49624-0e33-4374-93b7-60b132b26dae"));
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	static SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd");
	
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
			 * {uuid: string; encounterType?: EncounterType; name: string; display: string; version: string;
			 * published: boolean; retired: boolean;}
			 */
			
			/*
			 * FormManager formManager =
			 * CoreContext.getInstance().getManager(FormManager.class); List<FormDescriptor>
			 * uncompletedFormDescriptors =
			 * formManager.getAllUncompletedFormsForVisit(patientVisit);
			 * 
			 * if (!uncompletedFormDescriptors.isEmpty()) {
			 * 
			 * for (FormDescriptor descriptor : uncompletedFormDescriptors) {
			 * if(!descriptor.getTarget().getRetired()) { ObjectNode formObj =
			 * generateFormDescriptorPayload(descriptor); formObj.put("formCategory",
			 * "available"); formList.add(formObj); } } PatientWrapper patientWrapper = new
			 * PatientWrapper(patient); Encounter lastMchEnrollment =
			 * patientWrapper.lastEncounter(MetadataUtils.existing(EncounterType.class,
			 * MchMetadata._EncounterType.MCHMS_ENROLLMENT)); if(lastMchEnrollment != null)
			 * { ObjectNode delivery = JsonNodeFactory.instance.objectNode();
			 * delivery.put("uuid", MCH_DELIVERY_FORM_UUID); delivery.put("name",
			 * "Delivery"); delivery.put("display", "MCH Delivery Form");
			 * delivery.put("version", "1.0"); delivery.put("published", true);
			 * delivery.put("retired", false); formList.add(delivery); } CalculationResult
			 * eligibleForDischarge =
			 * EmrCalculationUtils.evaluateForPatient(EligibleForMchmsDischargeCalculation.
			 * class, null, patient); if((Boolean) eligibleForDischarge.getValue() == true)
			 * { ObjectNode discharge = JsonNodeFactory.instance.objectNode();
			 * discharge.put("uuid", MCH_DISCHARGE_FORM_UUID); discharge.put("name",
			 * "Discharge"); discharge.put("display", "MCH Discharge Form");
			 * discharge.put("version", "1.0"); discharge.put("published", true);
			 * discharge.put("retired", false); formList.add(discharge); } ObjectNode
			 * labOrder = JsonNodeFactory.instance.objectNode(); labOrder.put("uuid",
			 * LAB_ORDERS_FORM_UUID); labOrder.put("name", "Laboratory Test Orders");
			 * labOrder.put("display", "Laboratory Test Orders"); labOrder.put("version",
			 * "1.0"); labOrder.put("published", true); labOrder.put("retired", false);
			 * formList.add(labOrder); }
			 */
		}
		
		allFormsObj.put("results", formList);
		
		return allFormsObj.toString();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/allClients")
	@ResponseBody
	public Object getAllPatients(HttpServletRequest request, @RequestParam("startDate") String qStartDate,
	        @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory,
	        @RequestParam(value = "page", defaultValue = "0") int page,
	        @RequestParam(value = "size", defaultValue = "50") int size) throws ParseException {
		
		Date startDate = dateTimeFormatter.parse(qStartDate);
		Date endDate = dateTimeFormatter.parse(qEndDate);
		
		if (qStartDate != null && qEndDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			endDate = calendar.getTime();
		}
		
		List<Patient> allPatients = Context.getPatientService().getAllPatients(false);
		
		int startIndex = page * size;
		int endIndex = Math.min(startIndex + size, allPatients.size());
		
		List<Patient> patients = allPatients.subList(startIndex, endIndex);
		
		return generatePatientListObj(new HashSet<>(patients), startDate, endDate, filterCategory);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/dueForVl")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getPatientsDueForVl(HttpServletRequest request, @RequestParam("startDate") String qStartDate,
	        @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		
		Date startDate = dateTimeFormatter.parse(qStartDate);
		Date endDate = dateTimeFormatter.parse(qEndDate);
		
		if (qStartDate != null && qEndDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			endDate = calendar.getTime();
		}
		
		List<Patient> allPatients = Context.getPatientService().getAllPatients(false);
		
		List<Patient> patientsDueForVl = new ArrayList<>();
		
		for (Patient patient : allPatients) {
			if (isPatientDueForVl(patient, startDate, endDate)) {
				patientsDueForVl.add(patient);
			}
		}
		
		return generatePatientListObj(new HashSet<>(patientsDueForVl), startDate, endDate);
	}
	
	private static boolean isPatientDueForVl(Patient patient, Date startDate, Date endDate) {
		boolean isDueForVl = false;
		
		List<String> dueForVlEncounterTypeUuids = Arrays.asList(PERSONAL_FAMILY_HISTORY_ENCOUNTERTYPE_UUID,
		    FOLLOW_UP_FORM_ENCOUNTER_TYPE, HIGH_VL_ENCOUNTERTYPE_UUID);
		
		List<String> dueForVlConceptUuids = Arrays.asList(ACTIVE_REGIMEN_CONCEPT_UUID, VIRAL_LOAD_CONCEPT_UUID,
		    BREASTFEEDING_CONCEPT_UUID, PREGNANT_CONCEPT_UUID, PMTCT_CONCEPT_UUID, EAC_SESSION_CONCEPT_UUID,
		    EXTENDED_EAC_CONCEPT_UUID);
		
		List<Encounter> dueForVlEncounters = getEncountersByEncounterTypes(dueForVlEncounterTypeUuids, startDate, endDate);
		List<Concept> dueForVlConcepts = getConceptsByUuids(dueForVlConceptUuids);
		
		// Retrieve the observations for the patient within the given date range
		List<Obs> observations = Context.getObsService().getObservations(null, dueForVlEncounters, dueForVlConcepts, null,
		    null, null, null, null, null, startDate, endDate, false);
		
		// Iterate through observations to determine criteria fulfillment
		for (Obs obs : observations) {
			if (obs.getPerson().equals(patient)) {
				
				// Criteria 1: Clients who are adults, have been on ART for more than 6 months,
				// not breastfeeding and the VL result is suppressed (< 1000 copies/ml).
				// If the VL results are suppressed the client will be due for VL in 6 months
				// then 12 months and so on.
				if (isAdult(patient) && onArtForMoreThanSixMonths(patient) && !isBreastfeeding(patient)
				        && isViralLoadSuppressed(patient)) {
					Date nextDueDate = calculateNextDueDate(obs, 6);
					if (nextDueDate.before(endDate)) {
						isDueForVl = true;
						break;
					}
				}
				
				// Criteria 2: Child or adolescent up to 18 yrs of age. The will have the VL
				// sample collected after 6 months and when they turn 19 yrs they join criteria
				// 1.
				if (isChildOrAdolescent(patient) && onArtForMoreThanSixMonths(patient)) {
					Date nextDueDate = calculateNextDueDate(obs, 6);
					if (nextDueDate.before(endDate)) {
						isDueForVl = true;
						break;
					}
				}
				
				// Criteria 3: Pregnant woman, newly enrolled on ART will be due for Viral load
				// after every 3 months until they are no longer in PMTCT.
				if (isPregnant(patient) && newlyEnrolledOnArt(patient)) {
					Date nextDueDate = calculateNextDueDate(obs, 3);
					if (nextDueDate.before(endDate)) {
						isDueForVl = true;
						break;
					}
				}
				
				// Criteria 4: Pregnant woman already on ART are eligible immediately they find
				// out they are pregnant.
				if (isPregnant(patient) && alreadyOnArt(patient)) {
					isDueForVl = true;
					break;
				}
				
				// Criteria 5: After EAC 3, they are eligible for VL in the next one month.
				if (afterEac3(patient)) {
					Date nextDueDate = calculateNextDueDate(obs, 1);
					if (nextDueDate.before(endDate)) {
						isDueForVl = true;
						break;
					}
				}
			}
		}
		return isDueForVl;
	}
	
	private static boolean isAdult(Patient patient) {
		Date birthdate = patient.getBirthdate();
		if (birthdate == null) {
			return false;
		}
		
		Date currentDate = new Date();
		long ageInMillis = currentDate.getTime() - birthdate.getTime();
		long ageInYears = ageInMillis / (1000L * 60 * 60 * 24 * 365);
		
		return ageInYears >= 18;
	}
	
	private static boolean onArtForMoreThanSixMonths(Patient patient) {
		List<Obs> onArtObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()), null,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(ACTIVE_REGIMEN_CONCEPT_UUID)), null, null,
		    null, null, 1, null, null, null, false);
		
		if (onArtObs != null && !onArtObs.isEmpty()) {
			Date startDate = onArtObs.get(0).getObsDatetime();
			Date currentDate = new Date();
			
			// Calculate the difference in days between the current date and the start date
			long diffInMillis = currentDate.getTime() - startDate.getTime();
			long diffInDays = diffInMillis / (1000L * 60 * 60 * 24);
			
			return diffInDays > SIX_MONTHS_IN_DAYS;
		}
		return false;
	}
	
	private static boolean isBreastfeeding(Patient patient) {
		List<Obs> breastFeedingObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()),
		    null, Collections.singletonList(Context.getConceptService().getConceptByUuid(BREASTFEEDING_CONCEPT_UUID)),
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(YES_CONCEPT)), null, null, null, 1, null,
		    null, null, false);
		
		return breastFeedingObs != null && !breastFeedingObs.isEmpty();
	}
	
	private static boolean isViralLoadSuppressed(Patient patient) {
		List<Obs> viralLoadSuppressedObs = Context.getObsService().getObservations(
		    Collections.singletonList(patient.getPerson()), null,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(VIRAL_LOAD_CONCEPT_UUID)), null, null,
		    null, null, null, null, null, null, false);
		
		if (viralLoadSuppressedObs != null && !viralLoadSuppressedObs.isEmpty()) {
			return viralLoadSuppressedObs.get(0).getValueNumeric() < THRESHOLD;
		}
		
		return false;
	}
	
	private static boolean isChildOrAdolescent(Patient patient) {
		Date birthdate = patient.getBirthdate();
		if (birthdate == null) {
			return false;
		}
		
		Date currentDate = new Date();
		long ageInMillis = currentDate.getTime() - birthdate.getTime();
		long ageInYears = ageInMillis / (1000L * 60 * 60 * 24 * 365);
		
		return ageInYears < 18;
	}
	
	private static boolean isPregnant(Patient patient) {
		List<Obs> pregnantObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()), null,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(PREGNANT_CONCEPT_UUID)),
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(YES_CONCEPT)), null, null, null, 1, null,
		    null, null, false);
		
		return pregnantObs != null && !pregnantObs.isEmpty();
	}
	
	private static boolean newlyEnrolledOnArt(Patient patient) {
		List<Obs> newlyEnrolledOnArtObs = Context.getObsService().getObservations(
		    Collections.singletonList(patient.getPerson()), null,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(ACTIVE_REGIMEN_CONCEPT_UUID)), null, null,
		    null, null, 1, null, null, null, false);
		
		if (newlyEnrolledOnArtObs != null && !newlyEnrolledOnArtObs.isEmpty()) {
			Date startDate = newlyEnrolledOnArtObs.get(0).getObsDatetime();
			Date currentDate = new Date();
			
			// Calculate the difference in days between the current date and the start date
			long diffInMillis = currentDate.getTime() - startDate.getTime();
			long diffInDays = diffInMillis / (1000L * 60 * 60 * 24);
			
			return diffInDays < SIX_MONTHS_IN_DAYS;
		}
		return false;
	}
	
	private static boolean alreadyOnArt(Patient patient) {
		List<Obs> alreadyOnArtObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()),
		    null, Collections.singletonList(Context.getConceptService().getConceptByUuid(ACTIVE_REGIMEN_CONCEPT_UUID)), null,
		    null, null, null, 1, null, null, null, false);
		
		return alreadyOnArtObs != null && !alreadyOnArtObs.isEmpty();
	}
	
	private static boolean afterEac3(Patient patient) {
		List<Obs> extendedEacObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()),
		    null, Collections.singletonList(Context.getConceptService().getConceptByUuid(EAC_SESSION_CONCEPT_UUID)),
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(EXTENDED_EAC_CONCEPT_UUID)), null, null,
		    null, 1, null, null, null, false);
		
		return extendedEacObs != null && !extendedEacObs.isEmpty();
	}
	
	private static Date calculateNextDueDate(Obs obs, int months) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(obs.getObsDatetime());
		calendar.add(Calendar.MONTH, months);
		return calendar.getTime();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/adultRegimenTreatment")
	@ResponseBody
	public Object getPatientsOnAdultRegimenTreatment(HttpServletRequest request,
	        @RequestParam("startDate") String qStartDate, @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		
		return getPatientsOnRegimenTreatment(qStartDate, qEndDate,
		    Arrays.asList(regimen_1A, regimen_1B, regimen_1C, regimen_1D, regimen_1E, regimen_1F, regimen_1G, regimen_1H,
		        regimen_1J, regimen_2A, regimen_2B, regimen_2C, regimen_2D, regimen_2E, regimen_2F, regimen_2G, regimen_2H,
		        regimen_2I, regimen_2J, regimen_2K),
		    ACTIVE_REGIMEN_CONCEPT_UUID);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/childRegimenTreatment")
	@ResponseBody
	public Object getPatientsOnChildRegimenTreatment(HttpServletRequest request,
	        @RequestParam("startDate") String qStartDate, @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		
		return getPatientsOnRegimenTreatment(qStartDate, qEndDate,
		    Arrays.asList(regimen_4A, regimen_4B, regimen_4C, regimen_4D, regimen_4E, regimen_4F, regimen_4G, regimen_4H,
		        regimen_4I, regimen_4J, regimen_4K, regimen_4L, regimen_5A, regimen_5B, regimen_5C, regimen_5D, regimen_5E,
		        regimen_5F, regimen_5G, regimen_5H, regimen_5I, regimen_5J),
		    ACTIVE_REGIMEN_CONCEPT_UUID);
	}
	
	private Object getPatientsOnRegimenTreatment(String qStartDate, String qEndDate, List<String> regimenConceptUuids,
	        String activeRegimenConceptUuid) throws ParseException {
		
		Date startDate = dateTimeFormatter.parse(qStartDate);
		Date endDate = dateTimeFormatter.parse(qEndDate);
		
		List<String> regimenTreatmentEncounterTypeUuids = Arrays.asList(PERSONAL_FAMILY_HISTORY_ENCOUNTERTYPE_UUID,
		    FOLLOW_UP_FORM_ENCOUNTER_TYPE);
		
		List<Encounter> regimenTreatmentEncounters = getEncountersByEncounterTypes(regimenTreatmentEncounterTypeUuids,
		    startDate, endDate);
		
		List<Concept> regimenConcepts = getConceptsByUuids(regimenConceptUuids);
		
		List<Obs> regimenTreatmentObs = Context.getObsService().getObservations(null, regimenTreatmentEncounters,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(activeRegimenConceptUuid)),
		    regimenConcepts, null, null, null, null, null, null, endDate, false);
		
		Map<String, Integer> regimenCounts = new HashMap<>();
		
		for (Obs obs : regimenTreatmentObs) {
			Concept regimenConcept = obs.getValueCoded();
			if (regimenConcept != null) {
				String conceptName = regimenConcept.getName().getName();
				regimenCounts.put(conceptName, regimenCounts.getOrDefault(conceptName, 0) + 1);
			}
		}
		
		Map<String, Object> results = new HashMap<>();
		List<Map<String, Object>> regimenList = new ArrayList<>();
		
		for (Map.Entry<String, Integer> entry : regimenCounts.entrySet()) {
			Map<String, Object> regimenEntry = new HashMap<>();
			regimenEntry.put("text", entry.getKey());
			regimenEntry.put("total", entry.getValue());
			regimenList.add(regimenEntry);
		}
		
		results.put("results", regimenList);
		return results;
	}
	
	/**
	 * Retrieves a list of patients under the care of community programs within a specified date range.
	 * 
	 * @return An Object representing the list of patients under the care of community programs within
	 *         the specified date range.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/underCareOfCommunityProgrammes")
	@ResponseBody
	public Object getPatientsUnderCareOfCommunityProgrammes(HttpServletRequest request,
	        @RequestParam("startDate") String qStartDate, @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		Date startDate = dateTimeFormatter.parse(qStartDate);
		Date endDate = dateTimeFormatter.parse(qEndDate);
		
		if (qStartDate != null && qEndDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			endDate = calendar.getTime();
		}
		
		EncounterType communityLinkageEncounterType = Context.getEncounterService()
		        .getEncounterTypeByUuid(COMMUNITY_LINKAGE_ENCOUNTER_UUID);
		EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteria(null, null, startDate, endDate, null,
		        null, Collections.singletonList(communityLinkageEncounterType), null, null, null, false);
		List<Encounter> encounters = Context.getEncounterService().getEncounters(encounterSearchCriteria);
		
		HashSet<Patient> underCareOfCommunityPatients = encounters.stream().map(Encounter::getPatient).collect(HashSet::new,
		    HashSet::add, HashSet::addAll);
		
		return generatePatientListObj(underCareOfCommunityPatients, endDate);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/viralLoadSamplesCollected")
	@ResponseBody
	public String getViralLoadSamplesCollected(HttpServletRequest request,
	        @RequestParam(value = "startDate") String qStartDate, @RequestParam(value = "endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) {
		try {
			Date startDate = dateTimeFormatter.parse(qStartDate);
			Date endDate = dateTimeFormatter.parse(qEndDate);
			
			if (qStartDate != null && qEndDate != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(endDate);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				endDate = calendar.getTime();
			}
			
			EncounterType viralLoadEncounterType = Context.getEncounterService()
			        .getEncounterTypeByUuid(VL_LAB_REQUEST_ENCOUNTER_TYPE);
			EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteria(null, null, null, endDate, null,
			        null, Collections.singletonList(viralLoadEncounterType), null, null, null, false);
			List<Encounter> viralLoadSampleEncounters = Context.getEncounterService().getEncounters(encounterSearchCriteria);
			
			Concept sampleCollectionDateConcept = Context.getConceptService().getConceptByUuid(SAMPLE_COLLECTION_DATE_UUID);
			List<Obs> sampleCollectionDateObs = Context.getObsService().getObservations(null, viralLoadSampleEncounters,
			    Collections.singletonList(sampleCollectionDateConcept), null, null, null, null, null, null, startDate,
			    endDate, false);
			
			// Generate the summary data
			Object summaryData = generateDashboardSummaryFromObs(startDate, endDate, sampleCollectionDateObs,
			    filterCategory);
			
			// Convert the summary data to JSON format
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonResponse = objectMapper.writeValueAsString(summaryData);
			
			return jsonResponse;
			
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private Map<String, Map<String, Integer>> generateDashboardSummaryFromObs(Date startDate, Date endDate,
	        List<Obs> obsList, filterCategory filterCategory) {
		
		if (obsList == null) {
			throw new IllegalArgumentException("Observation list cannot be null");
		}
		
		List<Date> dates = new ArrayList<>();
		for (Obs obs : obsList) {
			if (obs == null) {
				System.out.println("Encountered null observation");
				continue;
			}
			
			Date obsDate = obs.getObsDatetime();
			if (obsDate == null) {
				System.out.println("Encountered observation with null date: " + obs);
				continue;
			}
			
			if (obsDate.after(DateUtils.addDays(startDate, -1)) && obsDate.before(DateUtils.addDays(endDate, 1))) {
				dates.add(obsDate);
			}
		}
		
		return generateSummary(dates);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/viralLoadResults")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getViralLoadResults(HttpServletRequest request, @RequestParam(value = "startDate") String qStartDate,
	        @RequestParam(value = "endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) {
		try {
			Date startDate = dateTimeFormatter.parse(qStartDate);
			Date endDate = dateTimeFormatter.parse(qEndDate);
			
			if (qStartDate != null && qEndDate != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(endDate);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				endDate = calendar.getTime();
			}
			
			EncounterType viralLoadEncounterType = Context.getEncounterService()
			        .getEncounterTypeByUuid(FOLLOW_UP_FORM_ENCOUNTER_TYPE);
			if (viralLoadEncounterType == null) {
				throw new RuntimeException("Encounter type not found: " + FOLLOW_UP_FORM_ENCOUNTER_TYPE);
			}
			
			EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteria(null, null, null, endDate, null,
			        null, Collections.singletonList(viralLoadEncounterType), null, null, null, false);
			List<Encounter> viralLoadSampleEncounters = Context.getEncounterService().getEncounters(encounterSearchCriteria);
			if (viralLoadSampleEncounters == null || viralLoadSampleEncounters.isEmpty()) {
				throw new RuntimeException("No encounters found for criteria");
			}
			
			Concept viralLoadResultConcept = Context.getConceptService().getConceptByUuid(VIRAL_LOAD_RESULTS_UUID);
			if (viralLoadResultConcept == null) {
				throw new RuntimeException("Concept not found: " + VIRAL_LOAD_RESULTS_UUID);
			}
			
			List<Obs> viralLoadResultObs = Context.getObsService().getObservations(null, viralLoadSampleEncounters,
			    Collections.singletonList(viralLoadResultConcept), null, null, null, null, null, null, startDate, endDate,
			    false);
			if (viralLoadResultObs == null || viralLoadResultObs.isEmpty()) {
				throw new RuntimeException("No observations found for the given criteria");
			}
			
			// Generate the summary data
			Map<String, Map<String, Integer>> summaryData = generateDashboardSummaryFromObs(startDate, endDate,
			    viralLoadResultObs, filterCategory);
			if (summaryData == null || summaryData.isEmpty()) {
				throw new RuntimeException("Failed to generate summary data");
			}
			
			// Convert the summary data to JSON format
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonResponse = objectMapper.writeValueAsString(summaryData);
			
			return jsonResponse;
			
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error occurred while processing viral load results", e);
		}
	}
	
	private Object generatePatientListObj(HashSet<Patient> allPatients) {
		return generatePatientListObj(allPatients, new Date());
	}
	
	public Object generatePatientListObj(HashSet<Patient> allPatients, Date endDate) {
		return generatePatientListObj(allPatients, new Date(), endDate);
	}
	
	public Object generatePatientListObj(HashSet<Patient> allPatients, Date startDate, Date endDate) {
		return generatePatientListObj(allPatients, startDate, endDate, null);
	}
	
	/**
	 * Generates a summary of patient data within a specified date range, grouped by year, month, and
	 * week.
	 * 
	 * @param allPatients A set of all patients to be considered for the summary.
	 * @param startDate The start date of the range for which to generate the summary.
	 * @param endDate The end date of the range for which to generate the summary.
	 * @param filterCategory The category to filter patients.
	 * @return A JSON string representing the summary of patient data.
	 */
	public Object generatePatientListObj(HashSet<Patient> allPatients, Date startDate, Date endDate,
	        filterCategory filterCategory) {
		
		ArrayNode patientList = JsonNodeFactory.instance.arrayNode();
		ObjectNode allPatientsObj = JsonNodeFactory.instance.objectNode();
		
		List<Date> patientDates = new ArrayList<>();
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
		
		for (Patient patient : allPatients) {
			ObjectNode patientObj = generatePatientObject(startDate, endDate, filterCategory, patient);
			if (patientObj != null) {
				patientList.add(patientObj);
				
				Calendar patientCal = Calendar.getInstance();
				patientCal.setTime(patient.getDateCreated());
				
				if (!patientCal.before(startCal) && !patientCal.after(endCal)) {
					patientDates.add(patient.getDateCreated());
				}
			}
		}
		
		Map<String, Map<String, Integer>> summary = generateSummary(patientDates);
		
		ObjectNode groupingObj = JsonNodeFactory.instance.objectNode();
		ObjectNode groupYear = JsonNodeFactory.instance.objectNode();
		ObjectNode groupMonth = JsonNodeFactory.instance.objectNode();
		ObjectNode groupWeek = JsonNodeFactory.instance.objectNode();
		
		summary.get("groupYear").forEach(groupYear::put);
		summary.get("groupMonth").forEach(groupMonth::put);
		summary.get("groupWeek").forEach(groupWeek::put);
		
		groupingObj.put("groupYear", groupYear);
		groupingObj.put("groupMonth", groupMonth);
		groupingObj.put("groupWeek", groupWeek);
		
		allPatientsObj.put("results", patientList);
		allPatientsObj.put("summary", groupingObj);
		
		return allPatientsObj.toString();
	}
	
	private Map<String, Map<String, Integer>> generateSummary(List<Date> dates) {
		String[] months = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
		        "Dec" };
		String[] days = new String[] { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
		
		Map<String, Integer> monthlySummary = new HashMap<>();
		Map<String, Integer> weeklySummary = new HashMap<>();
		Map<String, Integer> dailySummary = new HashMap<>();
		
		for (Date date : dates) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			
			String month = months[calendar.get(Calendar.MONTH)];
			monthlySummary.put(month, monthlySummary.getOrDefault(month, 0) + 1);
			
			int week = calendar.get(Calendar.WEEK_OF_MONTH);
			String weekOfTheMonth = String.format("%s_Week%s", month, week);
			weeklySummary.put(weekOfTheMonth, weeklySummary.getOrDefault(weekOfTheMonth, 0) + 1);
			
			int day = calendar.get(Calendar.DAY_OF_WEEK);
			String dayInWeek = String.format("%s_%s", month, days[day - 1]);
			dailySummary.put(dayInWeek, dailySummary.getOrDefault(dayInWeek, 0) + 1);
		}
		
		// Sorting the summaries
		Map<String, Integer> sortedMonthlySummary = monthlySummary.entrySet().stream()
		        .sorted(Comparator.comparingInt(e -> Arrays.asList(months).indexOf(e.getKey())))
		        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		Map<String, Integer> sortedWeeklySummary = weeklySummary.entrySet().stream().sorted((e1, e2) -> {
			String[] parts1 = e1.getKey().split("_Week");
			String[] parts2 = e2.getKey().split("_Week");
			int monthCompare = Arrays.asList(months).indexOf(parts1[0]) - Arrays.asList(months).indexOf(parts2[0]);
			if (monthCompare != 0) {
				return monthCompare;
			} else {
				return Integer.parseInt(parts1[1]) - Integer.parseInt(parts2[1]);
			}
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		Map<String, Integer> sortedDailySummary = dailySummary.entrySet().stream().sorted((e1, e2) -> {
			String[] parts1 = e1.getKey().split("_");
			String[] parts2 = e2.getKey().split("_");
			int monthCompare = Arrays.asList(months).indexOf(parts1[0]) - Arrays.asList(months).indexOf(parts2[0]);
			if (monthCompare != 0) {
				return monthCompare;
			} else {
				return Arrays.asList(days).indexOf(parts1[1]) - Arrays.asList(days).indexOf(parts2[1]);
			}
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		Map<String, Map<String, Integer>> summary = new HashMap<>();
		summary.put("groupYear", sortedMonthlySummary);
		summary.put("groupMonth", sortedWeeklySummary);
		summary.put("groupWeek", sortedDailySummary);
		
		return summary;
	}
	
	private static ObjectNode generatePatientObject(Date startDate, Date endDate, filterCategory filterCategory,
	        Patient patient) {
		ObjectNode patientObj = JsonNodeFactory.instance.objectNode();
		String dateEnrolled = getEnrolmentDate(patient);
		String lastRefillDate = getLastRefillDate(patient);
		String artRegimen = getARTRegimen(patient);
		String artInitiationDate = getArtInitiationDate(patient);
		String contact = patient.getAttribute("Client Telephone Number") != null
		        ? String.valueOf(patient.getAttribute("Client Telephone Number"))
		        : "";
		String alternateContact = patient.getAttribute("AltTelephoneNo") != null
		        ? String.valueOf(patient.getAttribute("AltTelephoneNo"))
		        : "";
		// Calculate age in years based on patient's birthdate and current date
		Date birthdate = patient.getBirthdate();
		Date currentDate = new Date();
		long age = (currentDate.getTime() - birthdate.getTime()) / (1000L * 60 * 60 * 24 * 365);
		
		ArrayNode identifiersArray = JsonNodeFactory.instance.arrayNode();
		for (PatientIdentifier identifier : patient.getIdentifiers()) {
			ObjectNode identifierObj = JsonNodeFactory.instance.objectNode();
			identifierObj.put("identifier", identifier.getIdentifier());
			identifierObj.put("identifierType", identifier.getIdentifierType().getName());
			identifiersArray.add(identifierObj);
		}
		
		String village = "";
		String landmark = "";
		for (PersonAddress address : patient.getAddresses()) {
			if (address.getAddress5() != null) {
				village = address.getAddress5();
			}
			if (address.getAddress6() != null) {
				landmark = address.getAddress6();
			}
		}
		String fullAddress = "Village: " + village + ", Landmark: " + landmark;
		
		ClinicalStatus clinicalStatus = determineClinicalStatus(patient, startDate, endDate);
		
		patientObj.put("name", patient.getPersonName() != null ? patient.getPersonName().toString() : "");
		patientObj.put("uuid", patient.getUuid());
		patientObj.put("sex", patient.getGender());
		patientObj.put("age", age);
		patientObj.put("identifiers", identifiersArray);
		patientObj.put("address", fullAddress);
		patientObj.put("contact", contact);
		patientObj.put("alternateContact", alternateContact);
		patientObj.put("clinicalStatus", clinicalStatus.toString());
		patientObj.put("newClient", newlyEnrolledOnArt(patient));
		patientObj.put("childOrAdolescent", age <= 19 ? true : false);
		patientObj.put("pregnantAndBreastfeeding", determineIfPatientIsPregnantOrBreastfeeding(patient, endDate));
		patientObj.put("IIT", determineIfPatientIsIIT(patient, startDate, endDate));
		patientObj.put("returningToTreatment", determineIfPatientIsReturningToTreatment(patient));
		patientObj.put("dueForVl", isPatientDueForVl(patient, startDate, endDate));
		patientObj.put("highVl", determineIfPatientIsHighVl(patient));
		patientObj.put("onAppointment", determineIfPatientIsOnAppointment(patient));
		patientObj.put("missedAppointment", determineIfPatientMissedAppointment(patient));
		patientObj.put("dateEnrolled", dateEnrolled);
		patientObj.put("lastRefillDate", lastRefillDate);
		patientObj.put("ARTRegimen", artRegimen);
		patientObj.put("initiationDate", artInitiationDate);
		
		// check filter category and filter patients based on the category
		if (filterCategory != null) {
			switch (filterCategory) {
				case CHILDREN_ADOLESCENTS:
					if (age <= 19) {
						return patientObj;
					}
					break;
				case PREGNANT_BREASTFEEDING:
					if (determineIfPatientIsPregnantOrBreastfeeding(patient, endDate)) {
						return patientObj;
					}
					break;
				case IIT:
					if (determineIfPatientIsIIT(patient, startDate, endDate)) {
						return patientObj;
					}
			}
		} else {
			return patientObj;
		}
		return null;
	}
	
	// public List<AppointmentsService> getAllAppointments(@RequestParam(value =
	// "forDate", required = false) String forDate) throws ParseException {
	// List<Appointment> appointments =
	// appointmentsService.getAllAppointments(DateUtil.convertToLocalDateFromUTC(forDate));
	// return appointmentMapper.constructResponse(appointments);
	// }
	
	/**
	 * Returns a list of patients on appointment.
	 * 
	 * @return A JSON representation of the list of patients on appointment, including summary
	 *         information about each patient.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/onAppointment")
	@ResponseBody
	public Object getPatientsOnAppointment(HttpServletRequest request, @RequestParam("startDate") String qStartDate,
	        @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		Date startDate = dateTimeFormatter.parse(qStartDate);
		Date endDate = dateTimeFormatter.parse(qEndDate);
		
		if (qStartDate != null && qEndDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			endDate = calendar.getTime();
		}
		
		List<String> onAppointmentencounterTypeUuids = Collections.singletonList(FOLLOW_UP_FORM_ENCOUNTER_TYPE);
		
		List<Encounter> onAppointmentEncounters = getEncountersByEncounterTypes(onAppointmentencounterTypeUuids, startDate,
		    endDate);
		
		List<Obs> onAppointmentObs = Context.getObsService().getObservations(null, onAppointmentEncounters,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(DATE_APPOINTMENT_SCHEDULED_CONCEPT_UUID)),
		    null, null, null, null, null, null, null, endDate, false);
		
		HashSet<Patient> onAppointmentPatientsFiltered = onAppointmentObs.stream()
		        .filter(obs -> obs.getPerson() instanceof Patient).map(obs -> (Patient) obs.getPerson()).filter(patient -> {
			        Date appointmentScheduledDate = null;
			        for (Obs obs : onAppointmentObs) {
				        if (obs.getPerson().equals(patient)
				                && obs.getConcept().getUuid().equals(DATE_APPOINTMENT_SCHEDULED_CONCEPT_UUID)) {
					        appointmentScheduledDate = obs.getValueDatetime();
					        break;
				        }
			        }
			        
			        if (appointmentScheduledDate != null) {
				        LocalDate today = LocalDate.now();
				        LocalDate scheduledDate = appointmentScheduledDate.toInstant().atZone(ZoneId.systemDefault())
				                .toLocalDate();
				        long diffInDays = ChronoUnit.DAYS.between(scheduledDate, today);
				        
				        return diffInDays == 0;
			        }
			        return false;
		        }).collect(Collectors.toCollection(HashSet::new));
		
		return generatePatientListObj(onAppointmentPatientsFiltered, endDate);
	}
	
	private static boolean determineIfPatientIsOnAppointment(Patient patient) {
		List<Obs> obsList = Context.getObsService().getObservations(Collections.singletonList(patient), null,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(DATE_APPOINTMENT_SCHEDULED_CONCEPT_UUID)),
		    null, null, null, null, null, null, null, null, false);
		
		if (!obsList.isEmpty()) {
			Date appointmentScheduledDate = null;
			for (Obs obs : obsList) {
				if (obs.getPerson().equals(patient)
				        && obs.getConcept().getUuid().equals(DATE_APPOINTMENT_SCHEDULED_CONCEPT_UUID)) {
					appointmentScheduledDate = obs.getValueDatetime();
					break;
				}
			}
			
			if (appointmentScheduledDate != null) {
				LocalDate today = LocalDate.now();
				LocalDate scheduledDate = appointmentScheduledDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				long diffInDays = ChronoUnit.DAYS.between(scheduledDate, today);
				
				return diffInDays == 0;
			}
		}
		return false;
	}
	
	/**
	 * Returns a list of patients who missed an appointment.
	 * 
	 * @return A JSON representation of the list of patients who missed an appointment, including
	 *         summary information about each patient.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/missedAppointment")
	@ResponseBody
	public Object getPatientsMissedAppointment(HttpServletRequest request, @RequestParam("startDate") String qStartDate,
	        @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		Date startDate = dateTimeFormatter.parse(qStartDate);
		Date endDate = dateTimeFormatter.parse(qEndDate);
		
		if (qStartDate != null && qEndDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			endDate = calendar.getTime();
		}
		
		List<String> misseAppointmentencounterTypeUuids = Collections.singletonList(FOLLOW_UP_FORM_ENCOUNTER_TYPE);
		
		List<Encounter> missedAppointmentEncounters = getEncountersByEncounterTypes(misseAppointmentencounterTypeUuids,
		    startDate, endDate);
		
		List<Obs> missedAppointmentObs = Context.getObsService().getObservations(null, missedAppointmentEncounters,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(DATE_APPOINTMENT_SCHEDULED_CONCEPT_UUID)),
		    null, null, null, null, null, null, startDate, endDate, false);
		
		HashSet<Patient> missedAppointmentPatientsFiltered = missedAppointmentObs.stream()
		        .filter(obs -> obs.getPerson() instanceof Patient).map(obs -> (Patient) obs.getPerson()).filter(patient -> {
			        Date appointmentScheduledDate = null;
			        for (Obs obs : missedAppointmentObs) {
				        if (obs.getPerson().equals(patient)
				                && obs.getConcept().getUuid().equals(DATE_APPOINTMENT_SCHEDULED_CONCEPT_UUID)) {
					        appointmentScheduledDate = obs.getValueDatetime();
					        break;
				        }
			        }
			        
			        if (appointmentScheduledDate != null) {
				        LocalDate today = LocalDate.now();
				        LocalDate scheduledDate = appointmentScheduledDate.toInstant().atZone(ZoneId.systemDefault())
				                .toLocalDate();
				        long diffInDays = ChronoUnit.DAYS.between(scheduledDate, today);
				        
				        return diffInDays >= 1 && diffInDays < 28;
			        }
			        return false;
		        }).collect(Collectors.toCollection(HashSet::new));
		
		return generatePatientListObj(missedAppointmentPatientsFiltered, startDate, endDate);
	}
	
	private static boolean determineIfPatientMissedAppointment(Patient patient) {
		List<Obs> obsList = Context.getObsService().getObservations(Collections.singletonList(patient), null,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(DATE_APPOINTMENT_SCHEDULED_CONCEPT_UUID)),
		    null, null, null, null, null, null, null, null, false);
		
		if (!obsList.isEmpty()) {
			Date appointmentScheduledDate = null;
			for (Obs obs : obsList) {
				if (obs.getPerson().equals(patient)
				        && obs.getConcept().getUuid().equals(DATE_APPOINTMENT_SCHEDULED_CONCEPT_UUID)) {
					appointmentScheduledDate = obs.getValueDatetime();
					break;
				}
			}
			
			if (appointmentScheduledDate != null) {
				LocalDate today = LocalDate.now();
				LocalDate scheduledDate = appointmentScheduledDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				long diffInDays = ChronoUnit.DAYS.between(scheduledDate, today);
				
				return diffInDays >= 1 && diffInDays < 28;
			}
		}
		return false;
	}
	
	/**
	 * Handles the HTTP GET request to retrieve patients with high viral load values within a specified
	 * date range. This method filters patients based on their viral load observations, identifying
	 * those with values above a predefined threshold.
	 * 
	 * @return A JSON representation of the list of patients with high viral load, including summary
	 *         information about each patient.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/highVl")
	// gets all visit forms for a patient
	@ResponseBody
	public Object getPatientsOnHighVl(HttpServletRequest request, @RequestParam("startDate") String qStartDate,
	        @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		
		Date startDate = dateTimeFormatter.parse(qStartDate);
		Date endDate = dateTimeFormatter.parse(qEndDate);
		
		if (qStartDate != null && qEndDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			endDate = calendar.getTime();
		}
		
		HashSet<Patient> highVLPatients = getPatientsWithHighVL(startDate, endDate);
		
		return generatePatientListObj(highVLPatients, startDate, endDate);
	}
	
	// Get all patients who have high Viral Load
	private HashSet<Patient> getPatientsWithHighVL(Date startDate, Date endDate) {
		return getPatientsWithVL(startDate, endDate, FOLLOW_UP_FORM_ENCOUNTER_TYPE, VIRAL_LOAD_CONCEPT_UUID);
	}
	
	private HashSet<Patient> getPatientsWithPersistentHighVL(Date startDate, Date endDate) {
		return getPatientsWithVL(startDate, endDate, HIGH_VL_ENCOUNTERTYPE_UUID, REPEAT_VL_RESULTS);
	}
	
	private HashSet<Patient> getPatientsWithVL(Date startDate, Date endDate, String encounterTypeUuid, String conceptUuid) {
		EncounterType encounterType = Context.getEncounterService().getEncounterTypeByUuid(encounterTypeUuid);
		EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteria(null, null, startDate, endDate, null,
		        null, Collections.singletonList(encounterType), null, null, null, false);
		List<Encounter> encounters = Context.getEncounterService().getEncounters(encounterSearchCriteria);
		
		HashSet<Patient> vlPatients = new HashSet<>();
		
		List<Obs> vlObs = Context.getObsService().getObservations(null, encounters,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(conceptUuid)), null, null, null, null,
		    null, null, startDate, endDate, false);
		
		for (Obs obs : vlObs) {
			if (obs.getValueNumeric() != null && obs.getValueNumeric() >= THRESHOLD) {
				vlPatients.add((Patient) obs.getPerson());
			}
		}
		
		HashSet<Patient> deceasedPatients = getDeceasedPatientsByDateRange(startDate, endDate);
		HashSet<Patient> transferredOutPatients = getTransferredOutPatients(startDate, endDate);
		
		vlPatients.removeAll(deceasedPatients);
		vlPatients.removeAll(transferredOutPatients);
		
		return vlPatients;
	}
	
	private HashSet<Patient> getPatientsWithRepeatedVL(Date startDate, Date endDate) {
		EncounterType repeatViralLoadEncounterType = Context.getEncounterService()
		        .getEncounterTypeByUuid(HIGH_VL_ENCOUNTERTYPE_UUID);
		EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteria(null, null, startDate, endDate, null,
		        null, Collections.singletonList(repeatViralLoadEncounterType), null, null, null, false);
		List<Encounter> encounters = Context.getEncounterService().getEncounters(encounterSearchCriteria);
		
		HashSet<Patient> repeatviralLoadPatients = new HashSet<>();
		
		List<Obs> repeatviralLoadObs = Context.getObsService().getObservations(null, encounters,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(REAPEAT_VL_COLLECTION)), null, null, null,
		    null, null, null, startDate, endDate, false);
		
		for (Obs obs : repeatviralLoadObs) {
			if (obs != null) {
				repeatviralLoadPatients.add((Patient) obs.getPerson());
			}
		}
		
		return repeatviralLoadPatients;
		
	}
	
	private HashSet<Patient> getPatientsWithSwitchART(Date startDate, Date endDate) {
		EncounterType switchARTRegimenEncounterType = Context.getEncounterService()
		        .getEncounterTypeByUuid(FOLLOW_UP_FORM_ENCOUNTER_TYPE);
		EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteria(null, null, startDate, endDate, null,
		        null, Collections.singletonList(switchARTRegimenEncounterType), null, null, null, false);
		List<Encounter> encounters = Context.getEncounterService().getEncounters(encounterSearchCriteria);
		
		HashSet<Patient> switchARTRegimenPatients = new HashSet<>();
		Map<Patient, String> patientPreviousRegimen = new HashMap<>();
		
		List<Obs> switchARTRegimenObs = Context.getObsService().getObservations(null, encounters,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(ACTIVE_REGIMEN_CONCEPT_UUID)), null, null,
		    null, null, null, null, startDate, endDate, false);
		
		for (Obs obs : switchARTRegimenObs) {
			if (obs != null && obs.getPerson() instanceof Patient) {
				Patient patient = (Patient) obs.getPerson();
				String currentRegimen = obs.getValueCoded() != null ? obs.getValueCoded().getUuid() : null;
				
				if (currentRegimen != null) {
					if (patientPreviousRegimen.containsKey(patient)) {
						String previousRegimen = patientPreviousRegimen.get(patient);
						if (!currentRegimen.equals(previousRegimen)) {
							switchARTRegimenPatients.add(patient);
						}
					}
					patientPreviousRegimen.put(patient, currentRegimen);
				}
			}
		}
		
		return switchARTRegimenPatients;
	}
	
	public HashSet<Patient> getPatientsWithSecondLineSwitchART(Date startDate, Date endDate) {
		EncounterType secondLineSwitchARTRegimenEncounterType = Context.getEncounterService()
		        .getEncounterTypeByUuid(FOLLOW_UP_FORM_ENCOUNTER_TYPE);
		EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteria(null, null, startDate, endDate, null,
		        null, Collections.singletonList(secondLineSwitchARTRegimenEncounterType), null, null, null, false);
		List<Encounter> encounters = Context.getEncounterService().getEncounters(encounterSearchCriteria);
		
		HashSet<Patient> secondLineSwitchARTRegimenPatients = new HashSet<>();
		
		List<Obs> secondLineSwitchARTRegimenObs = Context.getObsService().getObservations(null, encounters,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(ACTIVE_REGIMEN_CONCEPT_UUID)), null, null,
		    null, null, null, null, startDate, endDate, false);
		
		for (Obs obs : secondLineSwitchARTRegimenObs) {
			if (obs != null && obs.getPerson() instanceof Patient) {
				Patient patient = (Patient) obs.getPerson();
				String currentRegimen = obs.getValueCoded() != null ? obs.getValueCoded().getUuid() : null;
				
				if (currentRegimen != null && SECOND_LINE_REGIMENS.contains(currentRegimen)) {
					secondLineSwitchARTRegimenPatients.add(patient);
				}
			}
		}
		
		return secondLineSwitchARTRegimenPatients;
	}
	
	// Determine if Patient is High Viral Load and return true if it is equal or
	// above threshold
	private static boolean determineIfPatientIsHighVl(Patient patient) {
		List<Obs> vlObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()), null,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(VIRAL_LOAD_CONCEPT_UUID)), null, null,
		    null, null, 1, null, null, null, false);
		
		if (vlObs != null && !vlObs.isEmpty()) {
			return vlObs.get(0).getValueNumeric() >= THRESHOLD;
		}
		return false;
	}
	
	/**
	 * Retrieves Clients with viral load coverage data
	 * 
	 * @return JSON representation of the list of patients with viral load coverage data
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/viralLoadCoverage")
	@ResponseBody
	public Object getViralLoadCoverage(HttpServletRequest request, @RequestParam("startDate") String qStartDate,
	        @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		Date startDate = dateTimeFormatter.parse(qStartDate);
		Date endDate = dateTimeFormatter.parse(qEndDate);
		
		if (qStartDate != null && qEndDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			endDate = calendar.getTime();
		}
		
		List<String> encounterTypeUuids = Collections.singletonList(FOLLOW_UP_FORM_ENCOUNTER_TYPE);
		
		List<Encounter> viralLoadCoverageEncounters = getEncountersByEncounterTypes(encounterTypeUuids, startDate, endDate);
		
		HashSet<Patient> viralLoadPatients = viralLoadCoverageEncounters.stream().map(Encounter::getPatient)
		        .collect(Collectors.toCollection(HashSet::new));
		
		List<Obs> viralLoadObs = Context.getObsService().getObservations(null, viralLoadCoverageEncounters,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(VIRAL_LOAD_CONCEPT_UUID)), null, null,
		    null, null, null, null, startDate, endDate, false);
		
		HashSet<Patient> viralLoadCoveredClients = viralLoadObs.stream().filter(obs -> obs.getPerson() instanceof Patient)
		        .map(obs -> (Patient) obs.getPerson()).collect(Collectors.toCollection(HashSet::new));
		
		viralLoadPatients.addAll(viralLoadCoveredClients);
		
		return generatePatientListObj(viralLoadPatients, startDate, endDate);
	}
	
	/**
	 * Handles the HTTP GET request to retrieve patients with Suppressed viral load values. This method
	 * filters patients based on their viral load observations, identifying those with values below a
	 * predefined threshold.
	 * 
	 * @return A JSON representation of the list of patients with Suppressed viral load
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/viralLoadSuppression")
	@ResponseBody
	public Object getViralLoadSuppression(HttpServletRequest request, @RequestParam("startDate") String qStartDate,
	        @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		
		Date startDate = dateTimeFormatter.parse(qStartDate);
		Date endDate = dateTimeFormatter.parse(qEndDate);
		
		if (qStartDate != null && qEndDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			endDate = calendar.getTime();
		}
		
		EncounterType followUpEncounterType = Context.getEncounterService()
		        .getEncounterTypeByUuid(FOLLOW_UP_FORM_ENCOUNTER_TYPE);
		EncounterSearchCriteria viralLoadSuppressedSearchCriteria = new EncounterSearchCriteria(null, null, startDate,
		        endDate, null, null, Collections.singletonList(followUpEncounterType), null, null, null, false);
		List<Encounter> encounters = Context.getEncounterService().getEncounters(viralLoadSuppressedSearchCriteria);
		
		HashSet<Patient> viralLoadSuppressedPatients = new HashSet<>();
		
		List<Obs> viralLoadSuppressedPatientsObs = Context.getObsService().getObservations(null, encounters,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(VIRAL_LOAD_CONCEPT_UUID)), null, null,
		    null, null, null, null, startDate, endDate, false);
		
		for (Obs obs : viralLoadSuppressedPatientsObs) {
			if (obs.getValueNumeric() != null && obs.getValueNumeric() < THRESHOLD) {
				viralLoadSuppressedPatients.add((Patient) obs.getPerson());
			}
		}
		
		return generatePatientListObj(viralLoadSuppressedPatients, startDate, endDate);
	}
	
	private static boolean determineIfPatientIsDueForVl(Patient patient) {
		return Math.random() < 0.5;
		// TODO: Add logic to determine if patient is due for VL
		// return false;
	}
	
	private static boolean determineIfPatientIsNewClient(Patient patient, Date startDate, Date endDate) {
		List<Obs> newClientObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()),
		    null, Collections.singletonList(Context.getConceptService().getConceptByUuid(ACTIVE_REGIMEN_CONCEPT_UUID)), null,
		    null, null, null, 1, null, startDate, endDate, false);
		
		if (newClientObs != null && !newClientObs.isEmpty()) {
			Date obsStartDate = newClientObs.get(0).getObsDatetime();
			Date currentDate = new Date();
			
			// Calculate the difference in days between the current date and the start date
			long diffInMillis = currentDate.getTime() - obsStartDate.getTime();
			long diffInDays = diffInMillis / (1000L * 60 * 60 * 24);
			
			return diffInDays < SIX_MONTHS_IN_DAYS;
		}
		return false;
	}
	
	/**
	 * Handles the HTTP GET request to retrieve patients who have returned to treatment after an
	 * interruption. This method filters encounters based on ART treatment interruption encounter types
	 * and aggregates patients who have returned to treatment within the specified date range.
	 * 
	 * @param request The HttpServletRequest object, providing request information for HTTP servlets.
	 * @return A JSON representation of the list of patients who have returned to treatment, including
	 *         summary information about each patient.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/returnedToTreatment")
	@ResponseBody
	public Object getPatientsReturnedToTreatment(HttpServletRequest request, @RequestParam("startDate") String qStartDate,
	        @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		
		Date startDate = dateTimeFormatter.parse(qStartDate);
		Date endDate = dateTimeFormatter.parse(qEndDate);
		
		if (qStartDate != null && qEndDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			endDate = calendar.getTime();
		}
		
		HashSet<Patient> returnToTreatmentPatients = getReturnToTreatmentPatients(startDate, endDate);
		
		return generatePatientListObj(new HashSet<>(returnToTreatmentPatients), startDate, endDate);
	}
	
	private static HashSet<Patient> getReturnToTreatmentPatients(Date startDate, Date endDate) {
		List<String> returnedToTreatmentencounterTypeUuids = Collections
		        .singletonList(ART_TREATMENT_INTURRUPTION_ENCOUNTER_TYPE_UUID);
		
		List<Encounter> returnedToTreatmentEncounters = getEncountersByEncounterTypes(returnedToTreatmentencounterTypeUuids,
		    startDate, endDate);
		
		List<Obs> returnedToTreatmentObs = Context.getObsService().getObservations(null, returnedToTreatmentEncounters,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(RETURNING_TO_TREATMENT_UUID)),
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(CONCEPT_BY_UUID)), null, null, null, null,
		    null, startDate, endDate, false);
		
		HashSet<Patient> returnToTreatmentPatients = new HashSet<>();
		
		for (Obs obs : returnedToTreatmentObs) {
			Patient patient = (Patient) obs.getPerson();
			returnToTreatmentPatients.add(patient);
		}
		
		return returnToTreatmentPatients;
	}
	
	// Determine if patient is returning to treatment
	private static boolean determineIfPatientIsReturningToTreatment(Patient patient) {
		List<Obs> obsList = Context.getObsService().getObservations(Collections.singletonList(patient), null,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(RETURNING_TO_TREATMENT_UUID)),
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(CONCEPT_BY_UUID)), null, null, null, null,
		    null, null, null, false);
		
		return !obsList.isEmpty();
	}
	
	/**
	 * Handles the HTTP GET request to retrieve patients who have experienced an interruption in their
	 * treatment. This method filters encounters based on ART treatment interruption encounter types and
	 * aggregates patients who have had such encounters within the specified date range. It aims to
	 * identify patients who might need follow-up or intervention due to treatment interruption.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/interruptedInTreatment")
	@ResponseBody
	public Object getPatientsInterruptedInTreatment(HttpServletRequest request, @RequestParam("startDate") String qStartDate,
	        @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		
		Date startDate = dateTimeFormatter.parse(qStartDate);
		Date endDate = dateTimeFormatter.parse(qEndDate);
		
		if (qStartDate != null && qEndDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			endDate = calendar.getTime();
		}
		
		HashSet<Patient> interruptedInTreatmentPatients = getInterruptedInTreatmentPatients(startDate, endDate);
		
		return generatePatientListObj(new HashSet<>(interruptedInTreatmentPatients), startDate, endDate);
	}
	
	// Determine if patient is Interrupted In Treatment
	private static boolean determineIfPatientIsIIT(Patient patient, Date startDate, Date endDate) {
		List<Obs> obsList = Context.getObsService().getObservations(Collections.singletonList(patient), null,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(DATE_APPOINTMENT_SCHEDULED_CONCEPT_UUID)),
		    null, null, null, null, null, null, null, null, false);
		
		return !getInterruptedInTreatmentPatients(startDate, endDate).isEmpty();
	}
	
	private static HashSet<Patient> getInterruptedInTreatmentPatients(Date startDate, Date endDate) {
		List<String> interruptedInTreatmentEncounterTypeUuids = Collections.singletonList(FOLLOW_UP_FORM_ENCOUNTER_TYPE);
		
		// Get encounters for the specified date range and encounter types
		List<Encounter> interruptedInTreatmentEncounters = getEncountersByEncounterTypes(
		    interruptedInTreatmentEncounterTypeUuids, startDate, endDate);
		
		// Get observations related to interrupted in-treatment concept within the date
		// range
		Concept dateAppointmentScheduledConcept = Context.getConceptService()
		        .getConceptByUuid(DATE_APPOINTMENT_SCHEDULED_CONCEPT_UUID);
		List<Obs> interruptedInTreatmentObs = Context.getObsService().getObservations(null, interruptedInTreatmentEncounters,
		    Collections.singletonList(dateAppointmentScheduledConcept), null, null, null, null, null, null, startDate,
		    endDate, false);
		
		// Filter patients who are interrupted in treatment based on appointment
		// scheduled date
		HashSet<Patient> interruptedPatients = new HashSet<>();
		
		for (Obs obs : interruptedInTreatmentObs) {
			if (obs.getPerson() instanceof Patient) {
				Patient patient = (Patient) obs.getPerson();
				Date appointmentScheduledDate = obs.getValueDatetime();
				
				if (appointmentScheduledDate != null) {
					LocalDate today = LocalDate.now();
					LocalDate scheduledDate = appointmentScheduledDate.toInstant().atZone(ZoneId.systemDefault())
					        .toLocalDate();
					long diffInDays = ChronoUnit.DAYS.between(scheduledDate, today);
					
					if (diffInDays >= 28) {
						interruptedPatients.add(patient);
					}
				}
			}
		}
		
		return interruptedPatients;
	}
	
	/**
	 * Handles the request to get a list of active patients within a specified date range. Active
	 * patients are determined based on an active Regimen.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/activeClients")
	@ResponseBody
	public Object getActiveClientsEndpoint(HttpServletRequest request, @RequestParam("startDate") String qStartDate,
	        @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory,
	        @RequestParam(value = "page", defaultValue = "0") int page,
	        @RequestParam(value = "size", defaultValue = "50") int size) throws ParseException {
		
		// Parse the end date
		Date endDate = (qEndDate != null) ? dateTimeFormatter.parse(qEndDate) : new Date();
		
		// Adjust the start date to the beginning of the first month to consider
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		calendar.set(Calendar.DAY_OF_MONTH, 1); // Start of current month
		Date startDate = (qStartDate != null) ? dateTimeFormatter.parse(qStartDate) : calendar.getTime();
		
		// Get active clients
		HashSet<Patient> activeClients = getActiveClients(startDate, endDate);
		
		// Paginate the results
		List<Patient> paginatedPatients = new ArrayList<>(activeClients).subList(page * size,
		    Math.min((page + 1) * size, activeClients.size()));
		
		return generatePatientListObj(new HashSet<>(paginatedPatients), startDate, endDate, filterCategory);
	}
	
	private HashSet<Patient> getActiveClients(Date startDate, Date endDate) throws ParseException {
		
		// Get all relevant encounter types for active clients
		List<String> activeClientsEncounterTypeUuids = Arrays.asList(PERSONAL_FAMILY_HISTORY_ENCOUNTERTYPE_UUID,
		    FOLLOW_UP_FORM_ENCOUNTER_TYPE, ADULT_AND_ADOLESCENT_INTAKE_FORM, PEDIATRIC_INTAKE_FORM);
		
		// Get all relevant encounters within the specified date range
		List<Encounter> activeRegimenEncounters = getEncountersByDateRange(activeClientsEncounterTypeUuids, startDate,
		    endDate);
		HashSet<Patient> activePatients = extractPatientsFromEncounters(activeRegimenEncounters);
		
		// Add patients with active regimens
		List<Obs> regimenObs = getObservationsByDateRange(activeRegimenEncounters,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(ACTIVE_REGIMEN_CONCEPT_UUID)), startDate,
		    endDate);
		HashSet<Patient> activeClients = extractPatientsFromObservations(regimenObs);
		
		activePatients.addAll(activeClients);
		
		// Add patients who returned to treatment
		HashSet<Patient> returnToTreatment = getReturnToTreatmentPatients(startDate, endDate);
		activePatients.addAll(returnToTreatment);
		
		// Add patients who transferred in
		HashSet<Patient> transferInPatients = getTransferredInPatients(startDate, endDate);
		activePatients.addAll(transferInPatients);
		
		// Remove patients who interrupted treatment, deceased, or transferred out
		HashSet<Patient> interruptedInTreatmentPatients = getInterruptedInTreatmentPatients(startDate, endDate);
		HashSet<Patient> deceasedPatients = getDeceasedPatientsByDateRange(startDate, endDate);
		HashSet<Patient> transferredOutPatients = getTransferredOutPatients(startDate, endDate);
		
		activePatients.removeAll(interruptedInTreatmentPatients);
		activePatients.removeAll(deceasedPatients);
		activePatients.removeAll(transferredOutPatients);
		
		return activePatients;
	}
	
	// Retrieves a list of encounters filtered by encounter types.
	private static List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeUuids, Date startDate,
	        Date endDate) {
		List<EncounterType> encounterTypes = encounterTypeUuids.stream()
		        .map(uuid -> Context.getEncounterService().getEncounterTypeByUuid(uuid)).collect(Collectors.toList());
		
		EncounterSearchCriteria encounterCriteria = new EncounterSearchCriteria(null, null, startDate, endDate, null, null,
		        encounterTypes, null, null, null, false);
		return Context.getEncounterService().getEncounters(encounterCriteria);
	}
	
	/**
	 * Retrieves a list of concepts based on their UUIDs.
	 * 
	 * @param conceptUuids A list of UUIDs of concepts to retrieve.
	 * @return A list of concepts corresponding to the given UUIDs.
	 */
	private static List<Concept> getConceptsByUuids(List<String> conceptUuids) {
		return conceptUuids.stream().map(uuid -> Context.getConceptService().getConceptByUuid(uuid))
		        .collect(Collectors.toList());
	}
	
	// Determine if Patient is Pregnant or Breastfeeding
	private static boolean determineIfPatientIsPregnantOrBreastfeeding(Patient patient, Date endDate) {
		
		List<Concept> pregnantAndBreastfeedingConcepts = new ArrayList<>();
		pregnantAndBreastfeedingConcepts
		        .add(Context.getConceptService().getConceptByUuid(CURRENTLY_BREASTFEEDING_CONCEPT_UUID));
		pregnantAndBreastfeedingConcepts.add(Context.getConceptService().getConceptByUuid(CURRENTLY_PREGNANT_CONCEPT_UUID));
		
		List<Obs> obsList = Context.getObsService().getObservations(Collections.singletonList(patient), null,
		    pregnantAndBreastfeedingConcepts,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(CONCEPT_BY_UUID)), null, null, null, null,
		    null, null, endDate, false);
		
		return !obsList.isEmpty();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/newClients")
	@ResponseBody
	public Object getNewPatients(HttpServletRequest request,
	        @RequestParam(required = false, value = "startDate") String qStartDate,
	        @RequestParam(required = false, value = "endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		
		Calendar calendar = Calendar.getInstance();
		
		// Set startDate to the first day of the current month
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date startDate = calendar.getTime();
		
		// Set endDate to the last day of the current month
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date endDate = calendar.getTime();
		
		// If specific dates are provided, override the default logic
		if (qStartDate != null) {
			startDate = dateTimeFormatter.parse(qStartDate);
		}
		if (qEndDate != null) {
			endDate = dateTimeFormatter.parse(qEndDate);
		}
		
		HashSet<Patient> enrolledPatients = getNewlyEnrolledPatients(startDate, endDate);
		return generatePatientListObj(enrolledPatients, startDate, endDate);
	}
	
	private HashSet<Patient> getNewlyEnrolledPatients(Date startDate, Date endDate) {
		List<String> enrolledClientsEncounterTypeUuids = Arrays.asList(ADULT_AND_ADOLESCENT_INTAKE_FORM,
		    PEDIATRIC_INTAKE_FORM, FOLLOW_UP_FORM_ENCOUNTER_TYPE, PERSONAL_FAMILY_HISTORY_ENCOUNTERTYPE_UUID);
		
		// Filter encounters within the current month
		List<Encounter> enrolledEncounters = getEncountersByDateRange(enrolledClientsEncounterTypeUuids, startDate, endDate);
		HashSet<Patient> enrolledPatients = extractPatientsFromEncounters(enrolledEncounters);
		
		// Get observations within the current month
		List<Obs> enrollmentObs = getObservationsByDateRange(enrolledEncounters,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(DATE_OF_ENROLLMENT_UUID)), startDate,
		    endDate);
		HashSet<Patient> enrolledClients = extractPatientsFromObservations(enrollmentObs);
		
		List<Obs> regimenObs = getObservationsByDateRange(enrolledEncounters,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(ACTIVE_REGIMEN_CONCEPT_UUID)), startDate,
		    endDate);
		HashSet<Patient> regimenPatients = extractPatientsFromObservations(regimenObs);
		
		enrolledPatients.addAll(enrolledClients);
		enrolledPatients.addAll(regimenPatients);
		
		// Filter out patients who are transferred in, deceased, or transferred out
		HashSet<Patient> transferredInPatients = getTransferredInPatients(startDate, endDate);
		HashSet<Patient> deceasedPatients = getDeceasedPatientsByDateRange(startDate, endDate);
		HashSet<Patient> transferredOutPatients = getTransferredOutPatients(startDate, endDate);
		
		enrolledPatients.removeAll(transferredInPatients);
		enrolledPatients.removeAll(transferredOutPatients);
		enrolledPatients.removeAll(deceasedPatients);
		
		return enrolledPatients;
	}
	
	// Determine Patient Enrollment Date From the Adult and Adolescent and Pediatric
	// Forms
	private static String getEnrolmentDate(Patient patient) {
		List<Obs> enrollmentDateObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()),
		    null, Collections.singletonList(Context.getConceptService().getConceptByUuid(DATE_OF_ENROLLMENT_UUID)), null,
		    null, null, null, null, null, null, null, false);
		
		enrollmentDateObs.sort(Comparator.comparing(Obs::getObsDatetime).reversed());
		
		if (!enrollmentDateObs.isEmpty()) {
			Obs dateObs = enrollmentDateObs.get(0);
			Date enrollmentDate = dateObs.getValueDate();
			if (enrollmentDate != null) {
				return dateTimeFormatter.format(enrollmentDate);
			}
		}
		return "";
		
	}
	
	// Retrieve the Last Refill Date from Patient Observation
	private static String getLastRefillDate(Patient patient) {
		List<Obs> lastRefillDateObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()),
		    null, Collections.singletonList(Context.getConceptService().getConceptByUuid(LAST_REFILL_DATE_UUID)), null, null,
		    null, null, null, null, null, null, false);
		
		lastRefillDateObs.sort(Comparator.comparing(Obs::getObsDatetime).reversed());
		
		if (!lastRefillDateObs.isEmpty()) {
			Obs lastObs = lastRefillDateObs.get(0);
			Date lastRefillDate = lastObs.getValueDate();
			if (lastRefillDate != null) {
				return dateTimeFormatter.format(lastRefillDate);
			}
		}
		return "";
	}
	
	// Retrieve the Initiation Date from Patient Observation
	private static String getArtInitiationDate(Patient patient) {
		List<Obs> initiationDateObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()),
		    null,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(DATE_OF_ART_INITIATION_CONCEPT_UUID)),
		    null, null, null, null, null, null, null, null, false);
		
		initiationDateObs.sort(Comparator.comparing(Obs::getObsDatetime).reversed());
		
		if (!initiationDateObs.isEmpty()) {
			Obs lastObs = initiationDateObs.get(0);
			Date initiationDate = lastObs.getValueDate();
			if (initiationDate != null) {
				return dateTimeFormatter.format(initiationDate);
			}
		}
		return "";
	}
	
	/**
	 * Retrieves the ART Regimen of a patient from their Observations.
	 * 
	 * @param patient The patient for whom the ART Regimen is to be retrieved.
	 * @return A string representing the ART Regimen of the patient. If no ART Regimen is found, an
	 *         empty string is returned.
	 */
	private static String getARTRegimen(Patient patient) {
		List<Obs> artRegimenObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()),
		    null, Collections.singletonList(Context.getConceptService().getConceptByUuid(ACTIVE_REGIMEN_CONCEPT_UUID)), null,
		    null, null, null, null, null, null, null, false);
		
		artRegimenObs.sort(Comparator.comparing(Obs::getObsDatetime).reversed());
		
		for (Obs obs : artRegimenObs) {
			if (obs.getValueCoded() != null) {
				return obs.getValueCoded().getName().getName();
			}
		}
		return "";
	}
	
	private static List<Encounter> getEncountersByDateRange(List<String> encounterTypeUuids, Date startDate, Date endDate) {
		return getEncountersByEncounterTypes(encounterTypeUuids, startDate, endDate);
	}
	
	private static List<Obs> getObservationsByDateRange(List<Encounter> encounters, List<Concept> concepts, Date startDate,
	        Date endDate) {
		return Context.getObsService().getObservations(null, encounters, concepts, null, null, null, null, null, null,
		    startDate, endDate, false);
	}
	
	private static HashSet<Patient> extractPatientsFromEncounters(List<Encounter> encounters) {
		HashSet<Patient> patients = new HashSet<>();
		for (Encounter encounter : encounters) {
			patients.add(encounter.getPatient());
		}
		return patients;
	}
	
	private static HashSet<Patient> extractPatientsFromObservations(List<Obs> observations) {
		HashSet<Patient> patients = new HashSet<>();
		for (Obs obs : observations) {
			Person person = obs.getPerson();
			if (person != null) {
				Patient patient = Context.getPatientService().getPatient(person.getPersonId());
				if (patient != null) {
					patients.add(patient);
				}
			}
		}
		return patients;
	}
	
	/**
	 * This method handles the viral load cascade endpoint for the ART dashboard. It retrieves the
	 * necessary data from the database and calculates the viral load cascade.
	 * 
	 * @param request The HTTP request object.
	 * @param qStartDate The start date for the viral load cascade in the format "yyyy-MM-dd".
	 * @param qEndDate The end date for the viral load cascade in the format "yyyy-MM-dd".
	 * @param filterCategory The filter category for the viral load cascade.
	 * @return A JSON object containing the results of the viral load cascade.
	 * @throws ParseException If the start or end date cannot be parsed.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/viralLoadCascade")
	@ResponseBody
	public Object viralLoadCascade(HttpServletRequest request, @RequestParam("startDate") String qStartDate,
	        @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		
		return getViralLoadCascade(qStartDate, qEndDate,
		    Arrays.asList(FIRST_EAC_SESSION, SECOND_EAC_SESSION, THIRD_EAC_SESSION, EXTENDED_EAC_CONCEPT_UUID,
		        REAPEAT_VL_COLLECTION, REPEAT_VL_RESULTS, HIGH_VL_ENCOUNTERTYPE_UUID, ACTIVE_REGIMEN_CONCEPT_UUID),
		    EAC_SESSION_CONCEPT_UUID);
	}
	
	/**
	 * This method calculates the viral load cascade for the ART dashboard. It retrieves the necessary
	 * data from the database, calculates the viral load cascade, and returns the results in a JSON
	 * object format.
	 * 
	 * @param qStartDate The start date for the viral load cascade in the format "yyyy-MM-dd".
	 * @param qEndDate The end date for the viral load cascade in the format "yyyy-MM-dd".
	 * @param vlCascadeConceptUuids A list of UUIDs representing the concepts related to the viral load
	 *            cascade.
	 * @param eacSessionConceptUuid The UUID of the concept representing the EAC session.
	 * @return A JSON object containing the results of the viral load cascade.
	 * @throws ParseException If the start or end date cannot be parsed.
	 */
	private Object getViralLoadCascade(String qStartDate, String qEndDate, List<String> vlCascadeConceptUuids,
	        String eacSessionConceptUuid) throws ParseException {
		
		Date startDate = dateTimeFormatter.parse(qStartDate);
		Date endDate = dateTimeFormatter.parse(qEndDate);
		
		if (qStartDate != null && qEndDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			endDate = calendar.getTime();
		}
		
		List<String> viralLoadCascadeEncounterTypeUuids = Arrays.asList(HIGH_VL_ENCOUNTERTYPE_UUID,
		    FOLLOW_UP_FORM_ENCOUNTER_TYPE);
		
		List<Encounter> viralLoadCascadeEncounters = getEncountersByEncounterTypes(viralLoadCascadeEncounterTypeUuids,
		    startDate, endDate);
		
		List<Concept> viralLoadCascadeConcepts = getConceptsByUuids(vlCascadeConceptUuids);
		
		List<Obs> viralLoadCascadeObs = Context.getObsService().getObservations(null, viralLoadCascadeEncounters,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(eacSessionConceptUuid)),
		    viralLoadCascadeConcepts, null, null, null, null, null, null, endDate, false);
		
		Map<String, Integer> viralLoadCascadeCounts = new HashMap<>();
		
		Set<Patient> patientsWithHighViralLoad = getPatientsWithHighVL(startDate, endDate);
		Set<Patient> patientsWithRepeatedVL = getPatientsWithRepeatedVL(startDate, endDate);
		Set<Patient> patientsWithPersistentHighVL = getPatientsWithPersistentHighVL(startDate, endDate);
		Set<Patient> patientsWithARTSwitch = getPatientsWithSwitchART(startDate, endDate);
		Set<Patient> patientsWithSecondLineSwitch = getPatientsWithSecondLineSwitchART(startDate, endDate);
		
		// Maps to track observation dates for each patient
		Map<Patient, Date> firstEACDates = new HashMap<>();
		Map<Patient, Date> secondEACDates = new HashMap<>();
		Map<Patient, Date> thirdEACDates = new HashMap<>();
		Map<Patient, Date> extendedEACDates = new HashMap<>();
		Map<Patient, Date> repeatVLCollectedDates = new HashMap<>();
		Map<Patient, Date> persistentHVLDates = new HashMap<>();
		Map<Patient, Date> artSwitchDates = new HashMap<>();
		Map<Patient, Date> artSwitchSecondLineDates = new HashMap<>();
		
		// Filter the observations to only include patients with high viral load
		for (Obs obs : viralLoadCascadeObs) {
			Concept viralLoadCascadeConcept = obs.getValueCoded();
			Patient patient = (Patient) obs.getPerson();
			if (viralLoadCascadeConcept != null && patientsWithHighViralLoad.contains(patient)) {
				String conceptName = viralLoadCascadeConcept.getName().getName();
				viralLoadCascadeCounts.put(conceptName, viralLoadCascadeCounts.getOrDefault(conceptName, 0) + 1);
				
				// Track the observation dates for each EAC session
				switch (conceptName) {
					case "First EAC Session":
						firstEACDates.put(patient, obs.getObsDatetime());
						break;
					case "Second EAC Session":
						secondEACDates.put(patient, obs.getObsDatetime());
						break;
					case "Third EAC Session":
						thirdEACDates.put(patient, obs.getObsDatetime());
						break;
					case "Extended EAC Session":
						extendedEACDates.put(patient, obs.getObsDatetime());
						break;
					case "Repeat Viral Load Collected":
						repeatVLCollectedDates.put(patient, obs.getObsDatetime());
						break;
					case "Persistent High Viral Load":
						persistentHVLDates.put(patient, obs.getObsDatetime());
						break;
					case "ART Switch":
						artSwitchDates.put(patient, obs.getObsDatetime());
						break;
					case "ART Switch (2nd Line)":
						artSwitchSecondLineDates.put(patient, obs.getObsDatetime());
						break;
					default:
						break;
				}
			}
		}
		
		// Calculate total turnaround time for each session
		double totalFirstToSecond = calculateTotalTurnaroundTime(firstEACDates, secondEACDates);
		double totalSecondToThird = calculateTotalTurnaroundTime(secondEACDates, thirdEACDates);
		double totalThirdToExtended = calculateTotalTurnaroundTime(thirdEACDates, extendedEACDates);
		double totalExtendedToRepeatVL = calculateTotalTurnaroundTime(extendedEACDates, repeatVLCollectedDates);
		
		// Calculate counts based on hierarchical structure
		int highViralLoadCount = patientsWithHighViralLoad.size();
		int firstEACCount = (int) firstEACDates.keySet().stream().filter(patientsWithHighViralLoad::contains).count();
		int secondEACCount = (int) secondEACDates.keySet().stream().filter(firstEACDates::containsKey).count();
		int thirdEACCount = (int) thirdEACDates.keySet().stream().filter(secondEACDates::containsKey).count();
		int extendedEACCount = (int) extendedEACDates.keySet().stream().filter(thirdEACDates::containsKey).count();
		int repeatVLCount = (int) repeatVLCollectedDates.keySet().stream().filter(extendedEACDates::containsKey).count();
		int persistentHighVLCount = (int) patientsWithPersistentHighVL.stream().filter(repeatVLCollectedDates::containsKey)
		        .count();
		int artSwitchCount = (int) patientsWithARTSwitch.stream().filter(persistentHVLDates::containsKey).count();
		int secondLineSwitchCount = (int) patientsWithSecondLineSwitch.stream().filter(artSwitchDates::containsKey).count();
		
		// Combine the results
		Map<String, Object> results = new LinkedHashMap<>();
		List<Map<String, Object>> viralLoadCascadeList = new ArrayList<>();
		
		// Add the entries in the desired order
		addCascadeEntry(viralLoadCascadeList, "HVL(≥1000 c/ml)", highViralLoadCount, highViralLoadCount,
		    calculateAverageTurnaroundTime(startDate, endDate, highViralLoadCount), true);
		addCascadeEntry(viralLoadCascadeList, "First EAC Session", firstEACCount, highViralLoadCount,
		    totalFirstToSecond / Math.max(firstEACCount, 1), false);
		addCascadeEntry(viralLoadCascadeList, "Second EAC Session", secondEACCount, firstEACCount,
		    totalSecondToThird / Math.max(secondEACCount, 1), false);
		addCascadeEntry(viralLoadCascadeList, "Third EAC Session", thirdEACCount, secondEACCount,
		    totalThirdToExtended / Math.max(thirdEACCount, 1), false);
		addCascadeEntry(viralLoadCascadeList, "Extended EAC Session", extendedEACCount, thirdEACCount,
		    totalExtendedToRepeatVL / Math.max(extendedEACCount, 1), false);
		addCascadeEntry(viralLoadCascadeList, "Repeat Viral Load Collected", repeatVLCount, extendedEACCount,
		    calculateAverageTurnaroundTime(startDate, endDate, repeatVLCount), false);
		addCascadeEntry(viralLoadCascadeList, "Persistent High Viral Load", persistentHighVLCount, repeatVLCount,
		    calculateAverageTurnaroundTime(startDate, endDate, persistentHighVLCount), false);
		addCascadeEntry(viralLoadCascadeList, "ART Switch", artSwitchCount, persistentHighVLCount,
		    calculateAverageTurnaroundTime(startDate, endDate, artSwitchCount), false);
		addCascadeEntry(viralLoadCascadeList, "ART Switch (2nd Line)", secondLineSwitchCount, artSwitchCount,
		    calculateAverageTurnaroundTime(startDate, endDate, secondLineSwitchCount), false);
		
		results.put("results", viralLoadCascadeList);
		return results;
	}
	
	private void addCascadeEntry(List<Map<String, Object>> list, String text, int count, int previousCount,
	        double averageTurnaroundTime, boolean isBaseCount) {
		Map<String, Object> entry = new LinkedHashMap<>();
		entry.put("text", text);
		entry.put("total", count);
		entry.put("previousCount", previousCount);
		entry.put("percentage", isBaseCount ? 100.0 : (previousCount == 0 ? 0.0 : (count * 100.0 / previousCount)));
		entry.put("averageTurnaroundTimeMonths", averageTurnaroundTime);
		list.add(entry);
	}
	
	private double calculateTotalTurnaroundTime(Map<Patient, Date> startDates, Map<Patient, Date> endDates) {
		double totalTurnaroundTime = 0.0;
		int count = 0;
		
		for (Map.Entry<Patient, Date> entry : startDates.entrySet()) {
			Patient patient = entry.getKey();
			Date startDate = entry.getValue();
			Date endDate = endDates.get(patient);
			
			if (endDate != null) {
				long timeDifference = endDate.getTime() - startDate.getTime();
				double monthsDifference = timeDifference / (1000.0 * 60 * 60 * 24 * 30);
				totalTurnaroundTime += monthsDifference;
				count++;
			}
		}
		
		return totalTurnaroundTime;
	}
	
	// Method to calculate average turnaround time for a given stage
	private double calculateAverageTurnaroundTime(Date startDate, Date endDate, int count) {
		if (count == 0)
			return 0.0;
		double totalTime = (endDate.getTime() - startDate.getTime()) / (1000.0 * 60 * 60 * 24 * 30);
		return totalTime / count;
	}
	
	/**
	 * This method handles the HTTP GET request for retrieving the list of patients who have been
	 * transferred out.
	 * 
	 * @param request The HTTP request object.
	 * @param qStartDate The start date for the transferred out patients in the format "yyyy-MM-dd".
	 * @param qEndDate The end date for the transferred out patients in the format "yyyy-MM-dd".
	 * @param filterCategory The filter category for the transferred out patients.
	 * @return A JSON object containing the list of transferred out patients.
	 * @throws ParseException If the start or end date cannot be parsed.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/transferredOut")
	@ResponseBody
	public Object getTransferredOutPatients(HttpServletRequest request, @RequestParam("startDate") String qStartDate,
	        @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		
		Date startDate = dateTimeFormatter.parse(qStartDate);
		Date endDate = dateTimeFormatter.parse(qEndDate);
		
		if (qStartDate != null && qEndDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			endDate = calendar.getTime();
		}
		
		HashSet<Patient> transferredOutPatients = getTransferredOutPatients(startDate, endDate);
		return generatePatientListObj(transferredOutPatients, startDate, endDate);
	}
	
	private static HashSet<Patient> getTransferredOutPatients(Date startDate, Date endDate) {
		
		List<Obs> transferredOutPatientsObs = Context.getObsService().getObservations(null, null,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(TRANSFERRED_OUT_CONCEPT_UUID)),
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(YES_CONCEPT)), null, null, null, null,
		    null, startDate, endDate, false);
		
		HashSet<Patient> transferredOutPatients = new HashSet<>();
		
		for (Obs obs : transferredOutPatientsObs) {
			Person person = obs.getPerson();
			if (person != null) {
				Patient patient = Context.getPatientService().getPatient(person.getPersonId());
				if (patient != null) {
					transferredOutPatients.add(patient);
				}
			}
		}
		
		HashSet<Patient> deceasedPatients = getDeceasedPatientsByDateRange(startDate, endDate);
		transferredOutPatients.removeAll(deceasedPatients);
		
		return transferredOutPatients;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/transferredIn")
	@ResponseBody
	public Object getTransferredInPatients(HttpServletRequest request, @RequestParam("startDate") String qStartDate,
	        @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		
		Date startDate = dateTimeFormatter.parse(qStartDate);
		Date endDate = dateTimeFormatter.parse(qEndDate);
		
		if (qStartDate != null && qEndDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			endDate = calendar.getTime();
		}
		
		HashSet<Patient> transferredInPatients = getTransferredInPatients(startDate, endDate);
		return generatePatientListObj(transferredInPatients, startDate, endDate);
	}
	
	public static HashSet<Patient> getTransferredInPatients(Date startDate, Date endDate) {
		PatientService patientService = Context.getPatientService();
		List<Patient> allPatients = patientService.getAllPatients();
		
		return allPatients.stream()
		        .filter(patient -> patient.getIdentifiers().stream()
		                .anyMatch(identifier -> identifier.getIdentifier().startsWith("TI-")))
		        .collect(Collectors.toCollection(HashSet::new));
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/deceased")
	@ResponseBody
	public Object getDeceasedPatients(HttpServletRequest request, @RequestParam("startDate") String qStartDate,
	        @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		
		Date startDate = dateTimeFormatter.parse(qStartDate);
		Date endDate = dateTimeFormatter.parse(qEndDate);
		
		if (qStartDate != null && qEndDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			endDate = calendar.getTime();
		}
		
		HashSet<Patient> deceasedPatients = getDeceasedPatientsByDateRange(startDate, endDate);
		
		return generatePatientListObj(new HashSet<>(deceasedPatients), startDate, endDate);
	}
	
	private static HashSet<Patient> getDeceasedPatientsByDateRange(Date startDate, Date endDate) {
		List<Obs> deceasedPatientsObs = Context.getObsService().getObservations(null, null,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(DECEASED_CONCEPT_UUID)),
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(YES_CONCEPT)), null, null, null, null,
		    null, startDate, endDate, false);
		
		HashSet<Patient> deadPatients = new HashSet<>();
		
		for (Obs obs : deceasedPatientsObs) {
			Person person = obs.getPerson();
			if (person != null) {
				Patient patient = Context.getPatientService().getPatient(person.getPersonId());
				if (patient != null) {
					deadPatients.add(patient);
				}
			}
		}
		
		return deadPatients;
	}
	
	public enum ClinicalStatus {
		INTERRUPTED_IN_TREATMENT,
		DIED,
		ACTIVE,
		TRANSFERRED_OUT,
		INACTIVE
	}
	
	public static ClinicalStatus determineClinicalStatus(Patient patient, Date startDate, Date endDate) {
		HashSet<Patient> deceasedPatients = getDeceasedPatientsByDateRange(startDate, endDate);
		if (deceasedPatients.contains(patient)) {
			return ClinicalStatus.DIED;
		}
		
		if (hasActiveEncountersOrObservations(patient, startDate, endDate)) {
			return ClinicalStatus.ACTIVE;
		}
		
		HashSet<Patient> transferredOutPatients = getTransferredOutPatients(startDate, endDate);
		if (transferredOutPatients.contains(patient)) {
			return ClinicalStatus.TRANSFERRED_OUT;
		}
		
		if (determineIfPatientIsIIT(patient, startDate, endDate)) {
			return ClinicalStatus.INTERRUPTED_IN_TREATMENT;
		}
		
		return ClinicalStatus.ACTIVE;
	}
	
	private static boolean hasActiveEncountersOrObservations(Patient patient, Date startDate, Date endDate) {
		List<Encounter> activeEncounters = getEncountersByDateRange(
		    Arrays.asList(PERSONAL_FAMILY_HISTORY_ENCOUNTERTYPE_UUID, FOLLOW_UP_FORM_ENCOUNTER_TYPE), startDate, endDate);
		if (activeEncounters.stream().anyMatch(encounter -> encounter.getPatient().equals(patient))) {
			return true;
		}
		
		List<Obs> activeRegimenObs = getObservationsByDateRange(activeEncounters,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(ACTIVE_REGIMEN_CONCEPT_UUID)), startDate,
		    endDate);
		return activeRegimenObs.stream().anyMatch(obs -> obs.getPerson().equals(patient));
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/waterfallAnalysis")
	@ResponseBody
	public Object getWaterfallAnalysis(HttpServletRequest request, @RequestParam("startDate") String qStartDate,
	        @RequestParam("endDate") String qEndDate,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		
		return getWaterfallAnalysisChart(qStartDate, qEndDate);
	}
	
	private Object getWaterfallAnalysisChart(String qStartDate, String qEndDate) throws ParseException {
		Date startDate = dateTimeFormatter.parse(qStartDate);
		Date endDate = dateTimeFormatter.parse(qEndDate);
		
		// Get all active clients for the entire period
		HashSet<Patient> activeClientsEntirePeriod = getActiveClients(startDate, endDate);
		
		// Get active clients for the last 30 days
		Calendar last30DaysCal = Calendar.getInstance();
		last30DaysCal.setTime(endDate);
		last30DaysCal.add(Calendar.DAY_OF_MONTH, -30);
		Date last30DaysStartDate = last30DaysCal.getTime();
		HashSet<Patient> activeClientsLast30Days = getActiveClients(last30DaysStartDate, endDate);
		
		// Exclude active clients from the last 30 days
		activeClientsEntirePeriod.removeAll(activeClientsLast30Days);
		
		// TX_CURR is the total number of active clients excluding the last 30 days
		int txCurrFirstTwoMonths = activeClientsEntirePeriod.size();
		
		// Get active clients for the third month
		HashSet<Patient> activeClientsThirdMonth = getActiveClients(startDate, endDate);
		
		// TX_NEW is the new clients in the third month, excluding those from the first
		// two months
		HashSet<Patient> newClientsThirdMonth = new HashSet<>(activeClientsThirdMonth);
		newClientsThirdMonth.removeAll(activeClientsEntirePeriod);
		int txNewThirdMonth = newClientsThirdMonth.size();
		
		// Other calculations remain unchanged
		HashSet<Patient> transferredInPatientsCurrentQuarter = getTransferredInPatients(startDate, endDate);
		HashSet<Patient> returnToTreatmentPatientsCurrentQuarter = getReturnToTreatmentPatients(startDate, endDate);
		HashSet<Patient> transferredOutPatientsCurrentQuarter = getTransferredOutPatients(startDate, endDate);
		HashSet<Patient> deceasedPatientsCurrentQuarter = new HashSet<>(getDeceasedPatientsByDateRange(startDate, endDate));
		HashSet<Patient> interruptedInTreatmentPatientsCurrentQuarter = getInterruptedInTreatmentPatients(startDate,
		    endDate);
		
		int transferInCurrentQuarter = transferredInPatientsCurrentQuarter.size();
		int txRttCurrentQuarter = returnToTreatmentPatientsCurrentQuarter.size();
		int transferOutCurrentQuarter = transferredOutPatientsCurrentQuarter.size();
		int txDeathCurrentQuarter = deceasedPatientsCurrentQuarter.size();
		
		HashSet<Patient> interruptedInTreatmentLessThan3Months = filterInterruptedInTreatmentPatients(
		    interruptedInTreatmentPatientsCurrentQuarter, 3, false);
		int txMlIitLessThan3MoCurrentQuarter = interruptedInTreatmentLessThan3Months.size();
		
		HashSet<Patient> interruptedInTreatmentMoreThan3Months = filterInterruptedInTreatmentPatients(
		    interruptedInTreatmentPatientsCurrentQuarter, 3, true);
		int txMlIitMoreThan3MoCurrentQuarter = interruptedInTreatmentMoreThan3Months.size();
		
		// Potential TX_CURR
		int potentialTxCurr = txNewThirdMonth + txCurrFirstTwoMonths + transferInCurrentQuarter + txRttCurrentQuarter;
		
		// CALCULATED TX_CURR
		int calculatedTxCurr = potentialTxCurr - transferOutCurrentQuarter - txDeathCurrentQuarter
		        - txMlIitLessThan3MoCurrentQuarter - txMlIitMoreThan3MoCurrentQuarter;
		
		// Prepare the results
		List<Map<String, Object>> waterfallAnalysisList = new ArrayList<>();
		waterfallAnalysisList.add(createResultMap("TX_CURR", txCurrFirstTwoMonths));
		waterfallAnalysisList.add(createResultMap("TX_NEW", txNewThirdMonth));
		waterfallAnalysisList.add(createResultMap("Transfer In", transferInCurrentQuarter));
		waterfallAnalysisList.add(createResultMap("TX_RTT", txRttCurrentQuarter));
		waterfallAnalysisList.add(createResultMap("Potential TX_CURR", potentialTxCurr));
		waterfallAnalysisList.add(createResultMap("Transfer Out", transferOutCurrentQuarter));
		waterfallAnalysisList.add(createResultMap("TX_DEATH", txDeathCurrentQuarter));
		waterfallAnalysisList.add(createResultMap("TX_ML_Self Transfer", 0));
		waterfallAnalysisList.add(createResultMap("TX_ML_Refusal/Stopped", 0));
		waterfallAnalysisList.add(createResultMap("TX_ML_IIT (<3 mo)", txMlIitLessThan3MoCurrentQuarter));
		waterfallAnalysisList.add(createResultMap("TX_ML_IIT (3+ mo)", txMlIitMoreThan3MoCurrentQuarter));
		waterfallAnalysisList.add(createResultMap("CALCULATED TX_CURR", calculatedTxCurr));
		
		// Combine the results
		Map<String, Object> results = new HashMap<>();
		results.put("results", waterfallAnalysisList);
		return results;
	}
	
	private HashSet<Patient> filterInterruptedInTreatmentPatients(HashSet<Patient> patients, int months, boolean moreThan) {
		HashSet<Patient> filteredPatients = new HashSet<>();
		LocalDate currentDate = LocalDate.now();
		
		for (Patient patient : patients) {
			Date enrollmentDate = getInitiationDate(patient);
			if (enrollmentDate != null) {
				LocalDate enrollmentLocalDate = enrollmentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				long monthsOnTreatment = ChronoUnit.MONTHS.between(enrollmentLocalDate, currentDate);
				
				if ((moreThan && monthsOnTreatment >= months) || (!moreThan && monthsOnTreatment < months)) {
					filteredPatients.add(patient);
				}
			}
		}
		
		return filteredPatients;
	}
	
	private Date getInitiationDate(Patient patient) {
		List<Obs> initiationDateObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()),
		    null,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(DATE_OF_ART_INITIATION_CONCEPT_UUID)),
		    null, null, null, null, null, null, null, null, false);
		
		initiationDateObs.sort(Comparator.comparing(Obs::getObsDatetime).reversed());
		
		if (!initiationDateObs.isEmpty()) {
			Obs lastObs = initiationDateObs.get(0);
			Date initiationDate = lastObs.getValueDate();
			if (initiationDate != null) {
				return initiationDate;
			}
		}
		return null;
	}
	
	private Map<String, Object> createResultMap(String key, int value) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put(key, value);
		return resultMap;
	}
	
	private static Double getLastCD4Count(Patient patient) {
		Concept lastCD4CountConcept = Context.getConceptService().getConceptByUuid(LAST_CD4_COUNT_UUID);
		List<Obs> lastCD4CountObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()),
		    null, Collections.singletonList(lastCD4CountConcept), null, null, null, null, null, null, null, null, false);
		
		if (!lastCD4CountObs.isEmpty()) {
			Obs lastcd4Obs = lastCD4CountObs.get(0);
			return lastcd4Obs.getValueNumeric();
		}
		return null;

	}
	
	private static String getTbStatus(Patient patient) {
		Concept tbStatusConcepts = Context.getConceptService().getConceptByUuid(TB_STATUS_CONCEPT_UUID);
		
		List<Obs> tbStatusObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()), null,
		    Collections.singletonList(tbStatusConcepts), null, null, null, null, null, null, null, null, false);
		
		if (!tbStatusObs.isEmpty()) {
			Obs tbStatus = tbStatusObs.get(0);
			return tbStatus.getValueCoded().getName().getName();
		}
		return "";
	}
	
	private static String getARVRegimenDose(Patient patient) {
		Concept arvRegimenDoseConcept = Context.getConceptService().getConceptByUuid(ARV_REGIMEN_DOSE_UUID);
		
		List<Obs> arvRegimenDoseObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()),
		    null, Collections.singletonList(arvRegimenDoseConcept), null, null, null, null, null, null, null, null, false);
		
		if (arvRegimenDoseObs.isEmpty()) {
			System.err.println("No observations found for the concept " + arvRegimenDoseConcept.getName().getName() + ".");
			return "";
		}
		
		Obs arvRegimenDose = arvRegimenDoseObs.get(0);
		
		if (arvRegimenDose.getValueText() != null) {
			return arvRegimenDose.getValueText();
		} else if (arvRegimenDose.getValueCoded() != null) {
			return arvRegimenDose.getValueCoded().getName().getName();
		} else {
			return "";
		}
	}
	
	private static String getWHOClinicalStage(Patient patient) {
		Concept whoClinicalConcept = Context.getConceptService().getConceptByUuid(WHO_CLINICAL_UUID);
		Concept whoClinicalStageIntakeConcept = Context.getConceptService().getConceptByUuid(WHO_CLINICAL_STAGE_INTAKE_UUID);
		
		List<Concept> whoConcepts = Arrays.asList(whoClinicalConcept, whoClinicalStageIntakeConcept);
		
		List<Obs> obsList = Context.getObsService().getObservations(Collections.singletonList(patient), null, whoConcepts,
		    null, null, null, null, null, null, null, null, false);
		
		Obs whoClinicalStageObs = obsList.get(0);
		
		if (whoClinicalStageObs.getValueText() != null) {
			return whoClinicalStageObs.getValueText();
		} else if (whoClinicalStageObs.getValueCoded() != null) {
			return whoClinicalStageObs.getValueCoded().getName().getName();
		} else {
			return "";
		}
	}
	
	private static String getDateVLResultsReceived(Patient patient) {
		Concept dateVLResultsReceivedConcept = Context.getConceptService().getConceptByUuid(DATE_VL_RESULTS_RECEIVED_UUID);
		
		List<Obs> dateVLResultsReceivedObs = Context.getObsService().getObservations(
		    Collections.singletonList(patient.getPerson()), null, Collections.singletonList(dateVLResultsReceivedConcept),
		    null, null, null, null, null, null, null, null, false);
		
		if (!dateVLResultsReceivedObs.isEmpty()) {
			Obs dateVLReceivedObs = dateVLResultsReceivedObs.get(0);
			Date dateVLResultsReceived = dateVLReceivedObs.getValueDate();
			if (dateVLResultsReceived != null) {
				return dateTimeFormatter.format(dateVLResultsReceived);
			}
		}
		return "";
	}
	
	private static String getCHWName(Patient patient) {
		Concept chwNameConcepts = Context.getConceptService().getConceptByUuid(CHW_NAME_UUID);
		
		List<Obs> chwNameObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()), null,
		    Collections.singletonList(chwNameConcepts), null, null, null, null, null, null, null, null, false);
		
		if (!chwNameObs.isEmpty()) {
			Obs chwName = chwNameObs.get(0);
			return chwName.getValueText();
		}
		return "";
	}
	
	private static String getCHWPhone(Patient patient) {
		Concept chwPhoneConcepts = Context.getConceptService().getConceptByUuid(CHW_PHONE_UUID);
		
		List<Obs> chwPhoneObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()), null,
		    Collections.singletonList(chwPhoneConcepts), null, null, null, null, null, null, null, null, false);
		
		if (!chwPhoneObs.isEmpty()) {
			Obs chwPhone = chwPhoneObs.get(0);
			return chwPhone.getValueText();
		}
		return "";
	}
	
	private static String getCHWAddress(Patient patient) {
		Concept chwAddressConcepts = Context.getConceptService().getConceptByUuid(CHW_ADDRESS_UUID);
		
		List<Obs> chwAddressObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()),
		    null, Collections.singletonList(chwAddressConcepts), null, null, null, null, null, null, null, null, false);
		
		if (!chwAddressObs.isEmpty()) {
			Obs chwAddress = chwAddressObs.get(0);
			return chwAddress.getValueText();
		}
		return "";
	}
	
	private static String getVLResults(Patient patient) {
		Concept viralLoadResultsConcept = Context.getConceptService().getConceptByUuid(VIRAL_LOAD_RESULTS_UUID);
		Concept bdlConcept = Context.getConceptService().getConceptByUuid(BDL_CONCEPT_UUID); // Correct variable name
		Concept viralLoadConcept = Context.getConceptService().getConceptByUuid(VIRAL_LOAD_CONCEPT_UUID);

		List<Obs> getVLResultNumericObs = Context.getObsService().getObservations(
		    Collections.singletonList(patient.getPerson()), null, Collections.singletonList(viralLoadConcept), null, null,
		    null, null, 1, null, null, null, false);

		List<Obs> getVLResultObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()),
		    null, Collections.singletonList(viralLoadResultsConcept), null, null, null, null, 1, null, null, null, false);

		List<Obs> allObservations = new ArrayList<>();
		allObservations.addAll(getVLResultNumericObs);
		allObservations.addAll(getVLResultObs);

		allObservations.sort((o1, o2) -> o2.getObsDatetime().compareTo(o1.getObsDatetime()));

		if (!allObservations.isEmpty()) {
			Obs mostRecentObs = allObservations.get(0);

			if (mostRecentObs.getValueNumeric() != null) {
				return mostRecentObs.getValueNumeric().toString();
			} else if (mostRecentObs.getValueText() != null) {
				return mostRecentObs.getValueText();
			} else if (mostRecentObs.getValueCoded() != null) {
				if (mostRecentObs.getValueCoded().equals(bdlConcept)) {
					return "Below Detectable (BDL)";
				} else {
					return mostRecentObs.getValueCoded().getName().getName();
				}
			} else {
				System.err.println("Observation value is neither numeric, text, nor coded.");
			}
		}
		return null;
	}
	
	private static String getVLStatus(Patient patient) {
		String vlResult = getVLResults(patient);
		
		if (vlResult == null) {
			System.err.println("VL result is null for patient: " + patient);
			return "Unknown";
		}
		
		try {
			double vlValue = Double.parseDouble(vlResult);
			
			if (vlValue >= 1000) {
				return "Unsuppressed";
			} else {
				return "Suppressed";
			}
		}
		catch (NumberFormatException e) {
			if ("Below Detectable (BDL)".equalsIgnoreCase(vlResult)) {
				return "Suppressed";
			} else {
				System.err.println("Error parsing VL result or unrecognized value: " + vlResult);
				return "Unknown";
			}
		}
	}
	
	private static Double getBMI(Patient patient) {
		List<Obs> bmiObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()), null,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(BMI_CONCEPT_UUID)), null, null, null,
		    null, 1, null, null, null, false);
		
		if (!bmiObs.isEmpty()) {
			Obs bmiObservation = bmiObs.get(0);
			return bmiObservation.getValueNumeric();
		}
		
		return null;
	}
	
	private static Double getMUAC(Patient patient) {
		List<Obs> muacObs = Context.getObsService().getObservations(Collections.singletonList(patient.getPerson()), null,
		    Collections.singletonList(Context.getConceptService().getConceptByUuid(MUAC_CONCEPT_UUID)), null, null, null,
		    null, 1, null, null, null, false);
		
		if (!muacObs.isEmpty()) {
			Obs muacObservation = muacObs.get(0);
			return muacObservation.getValueNumeric();
		}
		
		return null;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/dashboard/obs")
	@ResponseBody
	public ResponseEntity<Object> getPatientObs(HttpServletRequest request, @RequestParam("patientUuid") String patientUuid,
	        @RequestParam(required = false, value = "filter") filterCategory filterCategory) throws ParseException {
		
		if (StringUtils.isBlank(patientUuid)) {
			return new ResponseEntity<>("You must specify patientUuid in the request!", new HttpHeaders(),
			        HttpStatus.BAD_REQUEST);
		}
		
		Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
		
		if (patient == null) {
			return new ResponseEntity<>("The provided patient was not found in the system!", new HttpHeaders(),
			        HttpStatus.NOT_FOUND);
		}
		
		PatientObservations observations = new PatientObservations();
		
		observations.setEnrollmentDate(getEnrolmentDate(patient));
		observations.setDateOfinitiation(getArtInitiationDate(patient));
		observations.setLastRefillDate(getLastRefillDate(patient));
		observations.setArvRegimen(getARTRegimen(patient));
		observations.setLastCD4Count(getLastCD4Count(patient));
		observations.setTbStatus(getTbStatus(patient));
		observations.setArvRegimenDose(getARVRegimenDose(patient));
		observations.setWhoClinicalStage(getWHOClinicalStage(patient));
		observations.setDateVLResultsReceived(getDateVLResultsReceived(patient));
		observations.setChwName(getCHWName(patient));
		observations.setChwPhone(getCHWPhone(patient));
		observations.setChwAddress(getCHWAddress(patient));
		observations.setVlResults(getVLResults(patient));
		observations.setVlStatus(getVLStatus(patient));
		observations.setBmi(getBMI(patient));
		observations.setMuac(getMUAC(patient));
		
		List<Map<String, String>> identifiersList = new ArrayList<>();
		for (PatientIdentifier identifier : patient.getIdentifiers()) {
			Map<String, String> identifierObj = new HashMap<>();
			identifierObj.put("identifier", identifier.getIdentifier().trim());
			identifierObj.put("identifierType", identifier.getIdentifierType().getName().trim());
			identifiersList.add(identifierObj);
		}
		Date birthdate = patient.getBirthdate();
		String formattedBirthDate = dateTimeFormatter.format(birthdate);
		Date currentDate = new Date();
		long age = (currentDate.getTime() - birthdate.getTime()) / (1000L * 60 * 60 * 24 * 365);
		
		Map<String, Object> responseMap = new LinkedHashMap<>();
		responseMap.put("Name", patient.getPersonName() != null ? patient.getPersonName().toString() : "");
		responseMap.put("uuid", patient.getUuid());
		responseMap.put("age", age);
		responseMap.put("birthdate", formattedBirthDate);
		responseMap.put("sex", patient.getGender());
		responseMap.put("Identifiers", identifiersList);
		responseMap.put("results", Collections.singletonList(observations));
		
		return new ResponseEntity<>(responseMap, new HttpHeaders(), HttpStatus.OK);
	}
	
}
