package org.openmrs.module.ssemrws.program;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.ResultUtil;
import org.openmrs.module.ssemrws.ContentManager;
import org.openmrs.module.ssemrws.calculation.CalculationUtils;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProgramManager implements ContentManager {
	
	protected static final Log log = LogFactory.getLog(ProgramManager.class);
	
	private Map<String, ProgramDescriptor> programs = new LinkedHashMap<String, ProgramDescriptor>();
	
	/**
	 * @see org.openmrs.module.ssemrws.ContentManager#getPriority()
	 */
	@Override
	public int getPriority() {
		return 60;
	}
	
	/**
	 * @see org.openmrs.module.ssemrws.ContentManager#refresh()
	 */
	@Override
	public synchronized void refresh() {
		programs.clear();
		
		List<ProgramDescriptor> descriptors = Context.getRegisteredComponents(ProgramDescriptor.class);
		
		// Sort by identifier descriptor order
		Collections.sort(descriptors);
		
		for (ProgramDescriptor descriptor : descriptors) {
			if (programs.containsKey(descriptor.getTargetUuid())) {
				throw new RuntimeException("Program " + descriptor.getTargetUuid() + " already registered");
			}
			
			programs.put(descriptor.getTargetUuid(), descriptor);
			
			log.debug("Registered program '" + descriptor.getTarget().getName() + "' (" + descriptor.getTargetUuid() + ")");
		}
	}
	
	/**
	 * Gets all program descriptors
	 * 
	 * @return the program descriptors
	 */
	public Collection<ProgramDescriptor> getAllProgramDescriptors() {
		return programs.values();
	}
	
	/**
	 * Gets the program descriptor for the given program
	 * 
	 * @param program the program
	 * @return the program descriptor
	 */
	public org.openmrs.module.ssemrws.program.ProgramDescriptor getProgramDescriptor(Program program) {
		return programs.get(program.getUuid());
	}
	
	/**
	 * Gets program descriptors for all programs which the given patient has ever been enrolled in
	 * 
	 * @param patient the patient
	 * @return the program descriptors
	 */
	public Collection<ProgramDescriptor> getPatientPrograms(Patient patient) {
		Collection<ProgramDescriptor> everIn = new LinkedHashSet<ProgramDescriptor>();
		
		ProgramWorkflowService pws = Context.getProgramWorkflowService();
		for (PatientProgram pp : pws.getPatientPrograms(patient, null, null, null, null, null, false)) {
			ProgramDescriptor descriptor = getProgramDescriptor(pp.getProgram());
			if (descriptor != null && descriptor.isEnabled()) {
				everIn.add(descriptor);
			}
		}
		
		return everIn;
	}
	
	/**
	 * Gets program descriptors for all programs which the given patient is eligible for
	 * 
	 * @param patient the patient
	 * @return the program descriptors
	 */
	public Collection<ProgramDescriptor> getPatientEligiblePrograms(Patient patient) {
		List<ProgramDescriptor> eligibleFor = new ArrayList<ProgramDescriptor>();
		
		for (ProgramDescriptor descriptor : programs.values()) {
			if (descriptor.isEnabled() && isPatientEligibleFor(patient, descriptor.getTarget())) {
				eligibleFor.add(descriptor);
			}
		}
		
		return eligibleFor;
	}
	
	/**
	 * Checks whether patient is eligible to enroll in the given program
	 * 
	 * @param patient the patient
	 * @param program the program
	 * @return true if patient can enroll
	 */
	public boolean isPatientEligibleFor(Patient patient, Program program) {
		ProgramDescriptor descriptor = getProgramDescriptor(program);
		
		Class<? extends PatientCalculation> clazz = descriptor.getEligibilityCalculation();
		
		PatientCalculation calculation = CalculationUtils.instantiateCalculation(clazz, null);
		
		CalculationResult result = Context.getService(PatientCalculationService.class).evaluate(patient.getId(),
		    calculation);
		return ResultUtil.isTrue(result);
	}
	
	/**
	 * Gets program descriptors for all programs which the given patient is currently enrolled in
	 * 
	 * @param patient the patient
	 * @return the program descriptors
	 */
	public Collection<ProgramDescriptor> getPatientActivePrograms(Patient patient) {
		return (Collection<ProgramDescriptor>) getPatientActivePrograms(patient, new Date());
	}
	
	/**
	 * Gets program descriptors for all programs which the given patient was enrolled in on given date
	 * 
	 * @param patient the patient
	 * @param onDate the date
	 * @return the program descriptors
	 */
	public Collection<ProgramDescriptor> getPatientActivePrograms(Patient patient, Date onDate) {
		List<ProgramDescriptor> activeIn = new ArrayList<ProgramDescriptor>();
		
		ProgramWorkflowService pws = Context.getProgramWorkflowService();
		for (PatientProgram pp : pws.getPatientPrograms(patient, null, null, null, null, null, false)) {
			if (pp.getActive(onDate)) {
				ProgramDescriptor descriptor = getProgramDescriptor(pp.getProgram());
				if (descriptor != null && descriptor.isEnabled()) {
					activeIn.add(descriptor);
				}
			}
		}
		
		return activeIn;
	}
	
	/**
	 * Gets all enrollments for the given patient in the given program, in chronological order. Regular
	 * service method doesn't guarantee order.
	 * 
	 * @param patient the patient
	 * @param program the program
	 * @return the enrollments
	 */
	public List<PatientProgram> getPatientEnrollments(Patient patient, Program program) {
		List<PatientProgram> enrollments = Context.getProgramWorkflowService().getPatientPrograms(patient, program, null,
		    null, null, null, false);
		
		// Sort by enrollment date ascending
		Collections.sort(enrollments, new Comparator<PatientProgram>() {
			
			@Override
			public int compare(PatientProgram pp1, PatientProgram pp2) {
				return OpenmrsUtil.compare(pp1.getDateEnrolled(), pp2.getDateEnrolled());
			}
		});
		
		return enrollments;
	}
}
