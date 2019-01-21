package org.slizaa.server.graphql.structuredatabase;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import org.slizaa.core.mvnresolver.api.IMvnCoordinate;
import org.slizaa.scanner.contentdefinition.MvnBasedContentDefinitionProvider;
import org.slizaa.server.service.slizaa.ISlizaaService;
import org.slizaa.server.service.slizaa.IStructureDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class StructureDatabaseMutation implements GraphQLMutationResolver {

	//
	@Autowired
	private ISlizaaService slizaaService;

	/**
	 * @param identifier
	 * @return
	 */
	public StructureDatabase newStructureDatabase(String identifier) {

		// create the structure database
		IStructureDatabase structureDatabase = slizaaService.newStructureDatabase(identifier);

		// return the result
		return new StructureDatabase(structureDatabase.getIdentifier());
	}

	public StructureDatabase startStructureDatabase(String identifier) {

		// get the structure database
		// TODO: check exists
		IStructureDatabase structureDatabase = slizaaService.getStructureDatabase(identifier);
		
		// TODO: check state
		structureDatabase.start();

		// return the result
		return new StructureDatabase(structureDatabase.getIdentifier());
	}

	public StructureDatabase stopStructureDatabase(String identifier) {

		// get the structure database
		// TODO: check exists
		IStructureDatabase structureDatabase = slizaaService.getStructureDatabase(identifier);
		
		// TODO: check state
		structureDatabase.stop();

		// return the result
		return new StructureDatabase(structureDatabase.getIdentifier());
	}
	
	public StructureDatabase populateStructureDatabase(String identifier) {

		IStructureDatabase structureDatabase = slizaaService.getStructureDatabase(identifier);

		//
		try {
			structureDatabase.parse(true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// return the result
		return new StructureDatabase(structureDatabase.getIdentifier());
	}

	public List<MvnCoordinate> setMvnBasedContentDefinition(String identifier, List<String> artifactIDs) {

		//
		MvnBasedContentDefinitionProvider mvnBasedContentDefinitionProvider = new MvnBasedContentDefinitionProvider();

		List<MvnCoordinate> result = new ArrayList<>();

		//
		for (String artifactID : artifactIDs) {
			IMvnCoordinate mvnCoordinate = mvnBasedContentDefinitionProvider.addArtifact(artifactID);
			result.add(new MvnCoordinate(mvnCoordinate));
		}

		//
		IStructureDatabase structureDatabase = slizaaService.getStructureDatabase(identifier);
		structureDatabase.setContentDefinitionProvider(mvnBasedContentDefinitionProvider);

		//
		return result;
	}

	public StructureDatabase mapSystem(String databaseId, String mappedSystemId) {

		IStructureDatabase structureDatabase = slizaaService.getStructureDatabase(databaseId);

		//
		try {
			structureDatabase.createNewHierarchicalGraph(mappedSystemId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// return the result
		return new StructureDatabase(structureDatabase.getIdentifier());
	}
}