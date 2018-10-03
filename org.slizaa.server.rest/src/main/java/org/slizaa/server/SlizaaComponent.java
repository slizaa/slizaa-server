package org.slizaa.server;

import org.slizaa.core.boltclient.IBoltClient;
import org.slizaa.core.boltclient.IBoltClientFactory;
import org.slizaa.hierarchicalgraph.core.model.HGRootNode;
import org.slizaa.hierarchicalgraph.graphdb.mapping.service.IMappingService;
import org.slizaa.hierarchicalgraph.graphdb.mapping.spi.ILabelDefinitionProvider;
import org.slizaa.hierarchicalgraph.graphdb.mapping.spi.IMappingProvider;
import org.slizaa.scanner.api.graphdb.IGraphDb;
import org.slizaa.scanner.api.importer.IModelImporter;
import org.slizaa.scanner.contentdefinition.MvnBasedContentDefinitionProvider;
import org.slizaa.server.backend.ISlizaaServerBackend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
@Component
public class SlizaaComponent {

  {
    org.slizaa.hierarchicalgraph.core.model.CustomFactoryStandaloneSupport.registerCustomHierarchicalgraphFactory();
    org.slizaa.hierarchicalgraph.graphdb.model.CustomFactoryStandaloneSupport.registerCustomHierarchicalgraphFactory();
  }

  @Value("${database.directory}")
  private File _databaseDirectory;

  /** - */
  @Autowired
  private ISlizaaServerBackend _slizaaServerBackend;

  /**
   * -
   */
  private ILabelDefinitionProvider _labelDefinitionProvider;

  /**
   * -
   */
  private IGraphDb _graphDb;

  /**
   * -
   */
  private HGRootNode _rootNode;

  /**
   * -
   */
  private IBoltClient _boltClient;

  /**
   * -
   */
  private ExecutorService _executorService;

  /**
   * <p>
   * </p>
   *
   * @return
   */
  public HGRootNode getRootNode() {
    return this._rootNode;
  }

  /**
   * <p>
   * </p>
   */
  @PostConstruct
  public void initialize() {

    //
    try {
      test();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @PreDestroy
  public void dispose() throws InterruptedException {

    //
    this._boltClient.disconnect();
    this._executorService.shutdown();
    this._executorService.awaitTermination(5, TimeUnit.SECONDS);
  }

  /**
   * <p>
   * </p>
   *
   * @throws Exception
   */
  public void test() throws Exception {

    //
    if (this._databaseDirectory.exists() && this._databaseDirectory.list().length > 0) {
      // FUCK ME!
      Thread.currentThread().setContextClassLoader(this._slizaaServerBackend.getCurrentExtensionClassLoader());

      this._slizaaServerBackend.getGraphDbFactory().newGraphDb(5001, this._databaseDirectory).create();
    } else {
      parseAndStartDatabase();
    }

    //
    this._executorService = Executors.newFixedThreadPool(10);
    IBoltClientFactory boltClientFactory = IBoltClientFactory.newInstance(this._executorService);

    //
    this._boltClient = boltClientFactory.createBoltClient("bolt://localhost:5001");
    this._boltClient.connect();

    //
    IMappingService mappingService = IMappingService.createHierarchicalgraphMappingService();
    IMappingProvider mappingProvider = this._slizaaServerBackend.getMappingProviders().get(0);
    this._rootNode = mappingService.convert(mappingProvider, this._boltClient, new SlizaaTestProgressMonitor());

    //
    _labelDefinitionProvider = mappingProvider.getLabelDefinitionProvider();
  }

  /**
   * <p>
   * </p>
   *
   * @throws IOException
   */
  public void parseAndStartDatabase() throws IOException {

    //
    MvnBasedContentDefinitionProvider contentDefinitionProvider = new MvnBasedContentDefinitionProvider();
    contentDefinitionProvider.addArtifact("org.springframework", "spring-core", "5.0.9.RELEASE");
    contentDefinitionProvider.addArtifact("org.springframework", "spring-context", "5.0.9.RELEASE");
    contentDefinitionProvider.addArtifact("org.springframework", "spring-beans", "5.0.9.RELEASE");

    // delete all contained files
    Files.walk(this._databaseDirectory.toPath(), FileVisitOption.FOLLOW_LINKS).sorted(Comparator.reverseOrder())
        .map(Path::toFile).forEach(File::delete);

    // FUCK ME!
    Thread.currentThread().setContextClassLoader(this._slizaaServerBackend.getCurrentExtensionClassLoader());

    IModelImporter modelImporter = this._slizaaServerBackend.getModelImporterFactory().createModelImporter(contentDefinitionProvider,
        this._databaseDirectory, _slizaaServerBackend.getParserFactories(),
        this._slizaaServerBackend.getCypherStatementRegistry().getAllStatements());

    modelImporter.parse(new SlizaaTestProgressMonitor(),
        () -> this._slizaaServerBackend.getGraphDbFactory().newGraphDb(5001, this._databaseDirectory).create());

    //
    _graphDb = modelImporter.getGraphDb();
  }
}
