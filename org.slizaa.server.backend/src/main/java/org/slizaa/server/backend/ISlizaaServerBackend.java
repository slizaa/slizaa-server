package org.slizaa.server.backend;

import org.slizaa.hierarchicalgraph.graphdb.mapping.spi.IMappingProvider;
import org.slizaa.scanner.api.cypherregistry.ICypherStatementRegistry;
import org.slizaa.scanner.api.graphdb.IGraphDbFactory;
import org.slizaa.scanner.api.importer.IModelImporterFactory;
import org.slizaa.scanner.spi.parser.IParserFactory;

import java.util.List;

/**
 *
 */
public interface ISlizaaServerBackend {

  ClassLoader getCurrentExtensionClassLoader();

  List<IExtension> getInstalledExtensions();

  List<IExtension> getAvailableExtensions();

  void installExtensions(IExtension... extensions);

  void installExtensions(List<IExtension> extensions);

  void addSlizaaServerBackendListener(ISlizaaServerBackendListener listener);

  void removeSlizaaServerBackendListener(ISlizaaServerBackendListener listener);

  /**
   *
   * @return
   */
  boolean isConfigured();

  /**
   *
   * @return
   */
  boolean hasModelImporterFactory();

  /**
   *
   * @return
   */
  IModelImporterFactory getModelImporterFactory();

  /**
   *
   * @return
   */
  boolean hasGraphDbFactory();

  /**
   *
   * @return
   */
  IGraphDbFactory getGraphDbFactory();

  /**
   *
   * @return
   */
  ICypherStatementRegistry getCypherStatementRegistry();

  /**
   *
   * @return
   */
  List<IParserFactory> getParserFactories();

  /**
   *
   * @return
   */
  List<IMappingProvider> getMappingProviders();
}
