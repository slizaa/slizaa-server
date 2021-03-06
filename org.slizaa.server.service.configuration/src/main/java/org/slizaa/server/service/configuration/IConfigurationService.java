package org.slizaa.server.service.configuration;

import java.io.IOException;

public interface IConfigurationService {

  <T> void store(String configurationIdentifier, T configuration) throws IOException;

  <T> T load(String configurationIdentifier, Class<T> type) throws IOException;
  
  <T> void store(String configurationIdentifier, String fileName, T configuration) throws IOException;

  <T> T load(String configurationIdentifier, String fileName, Class<T> type) throws IOException;
}
