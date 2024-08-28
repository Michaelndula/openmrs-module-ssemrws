package org.openmrs.module.ssemrws;

public class UiResource {
	
	private String provider;
	
	private String path;
	
	/**
	 * Constructs a UiResource from a string in the format <provider>:<path>
	 * 
	 * @param providerAndPath the string value
	 */
	public UiResource(String providerAndPath) {
		String[] components = providerAndPath.split(":");
		
		if (components.length != 2) {
			throw new IllegalArgumentException("UI resource " + providerAndPath + " is not formatted as <provider>:<path>");
		}
		
		this.provider = components[0];
		this.path = components[1];
	}
	
	/**
	 * Constructs a UiResource from the given provider and path values
	 * 
	 * @param provider the provider
	 * @param path the path
	 */
	public UiResource(String provider, String path) {
		this.provider = provider;
		this.path = path;
	}
	
	/**
	 * Gets the resource provider
	 * 
	 * @return the provider name
	 */
	public String getProvider() {
		return provider;
	}
	
	/**
	 * Gets the resource path
	 * 
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return provider + ":" + path;
	}
	
	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		
		UiResource that = (UiResource) o;
		
		return path.equals(that.path) && provider.equals(that.provider);
	}
	
	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return path.hashCode() + 31 * provider.hashCode();
	}
}
