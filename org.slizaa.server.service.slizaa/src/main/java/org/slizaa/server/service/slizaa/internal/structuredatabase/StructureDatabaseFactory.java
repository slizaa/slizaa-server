package org.slizaa.server.service.slizaa.internal.structuredatabase;

import org.slizaa.core.boltclient.IBoltClientFactory;
import org.slizaa.server.service.backend.IBackendServiceInstanceProvider;
import org.slizaa.server.service.slizaa.IStructureDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class StructureDatabaseFactory {

    /**
     * the spring generated state machine
     */
    @Autowired
    private StateMachineFactory<StructureDatabaseState, StructureDatabaseEvent> _stateMachineFactory;

    private Map<StateMachine<StructureDatabaseState, StructureDatabaseEvent>, StructureDatabaseStateMachineContext> _stateMachine2StructureDatabaseContext = new HashMap<>();

    /**
     * @param id
     * @param databaseDirectory
     * @param serverBackend
     * @param boltClientFactory
     * @return
     */
    public IStructureDatabase newInstance(String id, File databaseDirectory,
                                          IBackendServiceInstanceProvider serverBackend, IBoltClientFactory boltClientFactory) {

        checkNotNull(id);
        checkNotNull(databaseDirectory);
        checkNotNull(serverBackend);
        checkNotNull(boltClientFactory);

        // create the database directory
        if (!databaseDirectory.exists()) {
            databaseDirectory.mkdirs();
        }

        // create the new state machine
        StateMachine<StructureDatabaseState, StructureDatabaseEvent> statemachine = _stateMachineFactory
                .getStateMachine();

        // create the state machine context
        StructureDatabaseStateMachineContext stateMachineContext = new StructureDatabaseStateMachineContext(id, databaseDirectory, serverBackend, boltClientFactory);

        // create the structure database
        StructureDatabaseImpl structureDatabase = new StructureDatabaseImpl(statemachine, stateMachineContext);

        // store the association
        _stateMachine2StructureDatabaseContext.put(statemachine, stateMachineContext);

        // now start...
        statemachine.start();

        // finally return the result
        return structureDatabase;
    }

    /**
     * @param stateMachine
     * @return
     */
    StructureDatabaseStateMachineContext context(StateMachine<StructureDatabaseState, StructureDatabaseEvent> stateMachine) {
        return _stateMachine2StructureDatabaseContext.get(stateMachine);
    }
}