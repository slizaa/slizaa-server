package org.slizaa.server.service.extensions.impl;

import org.slizaa.server.service.extensions.IExtension;
import org.slizaa.server.service.extensions.IExtensionService;
import org.slizaa.server.service.extensions.Version;
import org.slizaa.server.service.extensions.mvn.MvnBasedExtension;
import org.slizaa.server.service.extensions.mvn.MvnDependency;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@Component
public class DefaultExtensionService implements IExtensionService {

    /* - */
    private MvnBasedExtension _neo4jExtension = new MvnBasedExtension("Neo4j Slizaa Backend", new Version(1, 0, 0))
            .withDependency(new MvnDependency("org.slizaa.neo4j:org.slizaa.neo4j.importer:1.0.0-SNAPSHOT", "*:org.slizaa.scanner.spi-api", "*:jdk.tools"))
            .withDependency(new MvnDependency("org.slizaa.neo4j:org.slizaa.neo4j.graphdbfactory:1.0.0-SNAPSHOT", "*:org.slizaa.scanner.spi-api"));

    /* - */
    private MvnBasedExtension _jtypeExtension = new MvnBasedExtension("JType Slizaa Extension", new Version(1, 0, 0))
            .withDependency(new MvnDependency("org.slizaa.jtype:org.slizaa.jtype.scanner:1.0.0-SNAPSHOT", "*:org.slizaa.scanner.spi-api"))
            .withDependency(new MvnDependency("org.slizaa.jtype:org.slizaa.jtype.scanner.apoc:1.0.0-SNAPSHOT", "*:org.slizaa.scanner.spi-api"))
            .withDependency(new MvnDependency("org.slizaa.jtype:org.slizaa.jtype.hierarchicalgraph:1.0.0-SNAPSHOT", "*:org.slizaa.scanner.spi-api"));

    /* - */
    private List<IExtension> _extensionList = Arrays.asList(_neo4jExtension, _jtypeExtension);

    /**
     * @return
     */
    @Override
    public List<IExtension> getAvailableExtensions() {
        return Collections.unmodifiableList(_extensionList);
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        //
       new DefaultExtensionService()._neo4jExtension.resolvedArtifactsToInstall().forEach(url -> System.out.println(url.toString()));
       new DefaultExtensionService()._jtypeExtension.resolvedArtifactsToInstall().forEach(url -> System.out.println(url.toString()));
    }
}
