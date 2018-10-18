package org.slizaa.server.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfig extends ResourceConfig {

  public JerseyConfig() {
    register(HierarchicalTreeController.class);
    register(CORSFilter.class);
    //register(NotFoundExceptionHandler.class);
  }
}