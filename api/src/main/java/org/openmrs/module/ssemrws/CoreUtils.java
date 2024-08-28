package org.openmrs.module.ssemrws;

import org.openmrs.GlobalProperty;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.ssemrws.app.AppRestrictedDescriptor;
import org.openmrs.util.OpenmrsUtil;

import java.util.*;

public class CoreUtils {
	
	/**
	 * Merges multiple collections into a list with natural ordering of elements
	 * 
	 * @param collections the collections
	 * @param <T> the element type
	 * @return the merged list
	 */
	public static <T extends Comparable> List<T> merge(Collection<T>... collections) {
		Set<T> merged = new TreeSet<T>();
		
		for (Collection<T> list : collections) {
			for (T element : list) {
				merged.add(element);
			}
		}
		
		return new ArrayList(merged);
	}
	
	/**
	 * Calculates the earliest date of two given dates, ignoring null values
	 * 
	 * @param d1 the first date
	 * @param d2 the second date
	 * @return the earliest date value
	 * @should return null if both dates are null
	 * @should return non-null date if one date is null
	 * @should return earliest date of two non-null dates
	 */
	public static Date earliest(Date d1, Date d2) {
		return OpenmrsUtil.compareWithNullAsLatest(d1, d2) >= 0 ? d2 : d1;
	}
	
	/**
	 * Calculates the latest date of two given dates, ignoring null values
	 * 
	 * @param d1 the first date
	 * @param d2 the second date
	 * @return the latest date value
	 * @should return null if both dates are null
	 * @should return non-null date if one date is null
	 * @should return latest date of two non-null dates
	 */
	public static Date latest(Date d1, Date d2) {
		return OpenmrsUtil.compareWithNullAsEarliest(d1, d2) >= 0 ? d1 : d2;
	}
	
	/**
	 * Add days to an existing date
	 * 
	 * @param date the date
	 * @param days the number of days to add (negative to subtract days)
	 * @return the new date
	 * @should shift the date by the number of days
	 */
	public static Date dateAddDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}
	
	/**
	 * Checks access to the given entity from the given app
	 * 
	 * @param entity the entity
	 * @param app the app
	 * @throws APIAuthenticationException if access is not allowed
	 */
	public static void checkAccess(AppRestrictedDescriptor entity, AppDescriptor app) {
		if (entity.getApps() != null && !entity.getApps().contains(app)) {
			throw new APIAuthenticationException("Entity " + entity.getId() + " cannot be accessed from " + app.getLabel());
		}
	}
	
	/**
	 * Sets an untyped global property
	 * 
	 * @param property the property name
	 * @param value the property value
	 */
	public static void setGlobalProperty(String property, String value) {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(property);
		if (gp == null) {
			gp = new GlobalProperty();
			gp.setProperty(property);
		}
		gp.setPropertyValue(value);
		Context.getAdministrationService().saveGlobalProperty(gp);
	}
}
