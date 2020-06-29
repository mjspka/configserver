/**
 * 
 */
package com.pmalla.configclient;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author pmalla
 *
 */

@Component
@ConfigurationProperties(prefix="config.client")
public class Configuration {
	private String property;

	public void setProperty(String property) {
		this.property = property;
	}

	public String getProperty() {
		return property;
	}

}
