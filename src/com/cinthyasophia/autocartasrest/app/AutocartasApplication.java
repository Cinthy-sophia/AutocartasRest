package com.cinthyasophia.autocartasrest.app;

import org.glassfish.jersey.server.ResourceConfig;

public class AutocartasApplication extends ResourceConfig {
	
	public AutocartasApplication() {
		packages("com.javahelps.jerseydemo.services");
	}

}
