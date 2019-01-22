package org.slizaa.server.service.slizaa.internal.hierarchicalgraph;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slizaa.hierarchicalgraph.core.model.HGRootNode;
import org.slizaa.scanner.contentdefinition.MvnBasedContentDefinitionProvider;
import org.slizaa.server.service.slizaa.IHierarchicalGraph;
import org.slizaa.server.service.slizaa.IStructureDatabase;
import org.slizaa.server.service.slizaa.internal.AbstractSlizaaServiceTest;
import org.slizaa.server.service.slizaa.internal.SlizaaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

public class StructureDatabaseTest extends AbstractSlizaaServiceTest {

	@Autowired
	private SlizaaServiceImpl _slizaaService;

	private IStructureDatabase _structureDatabase;

	@Before
	public void setUp() throws Exception {

		String STRUCTURE_DATABASE_NAME = "HURZ";

		if (!_slizaaService.getBackendService().hasInstalledExtensions()) {
			_slizaaService.getBackendService().installExtensions(_slizaaService.getExtensionService().getExtensions());
		}

		if (!_slizaaService.hasStructureDatabase(STRUCTURE_DATABASE_NAME)) {

			// create a new database
			_structureDatabase = _slizaaService.newStructureDatabase(STRUCTURE_DATABASE_NAME);

			// configure
			MvnBasedContentDefinitionProvider mvnBasedContentDefinitionProvider = new MvnBasedContentDefinitionProvider();
			mvnBasedContentDefinitionProvider
					.addArtifact("org.springframework.statemachine:spring-statemachine-core:2.0.3.RELEASE");
			_structureDatabase.setContentDefinitionProvider(mvnBasedContentDefinitionProvider);

			// and parse
			_structureDatabase.parse(true);
		}
		//
		else {
			_structureDatabase = _slizaaService.getStructureDatabase(STRUCTURE_DATABASE_NAME);
			_structureDatabase.start();
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getIdentifier() {

		IHierarchicalGraph hierarchicalGraph = _structureDatabase.createNewHierarchicalGraph("HG");

		HGRootNode rootNode = hierarchicalGraph.getRootNode();

		System.out.println(rootNode.getChildren());
	}
}