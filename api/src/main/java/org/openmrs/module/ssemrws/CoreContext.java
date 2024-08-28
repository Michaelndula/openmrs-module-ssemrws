package org.openmrs.module.ssemrws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.ssemrws.api.CoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CoreContext {
	
	protected static final Log log = LogFactory.getLog(CoreContext.class);
	
	private Map<Class<? extends ContentManager>, ContentManager> managers = new HashMap<Class<? extends ContentManager>, ContentManager>();
	
	@Autowired
	private CoreService coreService;
	
	boolean refreshed = false;
	
	/**
	 * Sets the content managers
	 * 
	 * @param managers the managers
	 */
	@Autowired
	public void setManagers(Collection<ContentManager> managers) {
		this.managers = new HashMap<Class<? extends ContentManager>, ContentManager>();
		
		// Re-organize into map of classes to components
		for (ContentManager manager : managers) {
			this.managers.put(manager.getClass(), manager);
		}
	}
	
	/**
	 * Gets the specified manager
	 * 
	 * @return the manager
	 */
	public <T extends ContentManager> T getManager(Class<? extends ContentManager> managerClass) {
		if (!managers.containsKey(managerClass)) {
			throw new IllegalArgumentException("No manager component found for " + managerClass.getName());
		}
		
		return (T) managers.get(managerClass);
	}
	
	/**
	 * Utility method to get the singleton instance from the application context in situations where you
	 * can't use @Autowired or @SpringBean. Use as a last resort.
	 * 
	 * @return the singleton instance
	 */
	public static CoreContext getInstance() {
		return getSingletonComponent(CoreContext.class);
	}
	
	/**
	 * Refresh all content
	 */
	public synchronized void refresh() {
		refreshed = false;
		
		// Sort content managers by priority
		List<ContentManager> sorted = new ArrayList<ContentManager>(managers.values());
		Collections.sort(sorted, new Comparator<ContentManager>() {
			
			@Override
			public int compare(ContentManager cm1, ContentManager cm2) {
				return cm1.getPriority() - cm2.getPriority();
			}
		});
		
		log.info("Refreshing all content managers...");
		
		long start = System.currentTimeMillis();
		
		// Refresh each content manager
		for (ContentManager manager : sorted) {
			coreService.refreshManager(manager);
		}
		
		long time = System.currentTimeMillis() - start;
		
		log.info("Refreshed all content managers in " + time + "ms");
		
		refreshed = true;
	}
	
	/**
	 * Returns whether all content was successfully refreshed
	 * 
	 * @return
	 */
	public boolean isRefreshed() {
		return refreshed;
	}
	
	/**
	 * Fetches a singleton component from the application context
	 * 
	 * @param clazz the class of the component
	 * @param <T> the class of the component
	 * @return the singleton instance
	 * @throws IllegalArgumentException if no such instance exists or more than one instance exists
	 */
	protected static <T> T getSingletonComponent(Class<T> clazz) {
		List<T> all = Context.getRegisteredComponents(clazz);
		if (all.size() == 0) {
			throw new IllegalArgumentException("No such object in the application context");
		}
		// Because of TRUNK-3889, singleton beans get instantiated twice
		// else if (all.size() > 1) {
		// throw new RuntimeException("Object is not a singleton in the application
		// context");
		// }
		else {
			return all.get(0);
		}
	}
}
