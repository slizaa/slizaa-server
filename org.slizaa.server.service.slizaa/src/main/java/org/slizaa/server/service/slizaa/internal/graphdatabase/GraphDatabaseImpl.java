package org.slizaa.server.service.slizaa.internal.graphdatabase;

import org.slizaa.scanner.spi.contentdefinition.IContentDefinitionProvider;
import org.slizaa.server.service.slizaa.IHierarchicalGraph;
import org.slizaa.server.service.slizaa.GraphDatabaseState;
import org.slizaa.server.service.slizaa.IGraphDatabase;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 *
 */
public class GraphDatabaseImpl implements IGraphDatabase {

	public static final String START_DATABASE_AFTER_PARSING = "START_DATABASE_AFTER_PARSING";

	/** the state machine **/
	private StateMachine<GraphDatabaseState, GraphDatabaseTrigger> _stateMachine;

	/**
	 * the state machine context
	 **/
	private GraphDatabaseStateMachineContext _stateMachineContext;

	/**
	 * @param stateMachine
	 * @param stateMachineContext
	 */
	GraphDatabaseImpl(StateMachine<GraphDatabaseState, GraphDatabaseTrigger> stateMachine,
			GraphDatabaseStateMachineContext stateMachineContext) {

		this._stateMachine = checkNotNull(stateMachine);
		this._stateMachineContext = checkNotNull(stateMachineContext);
		this._stateMachineContext.setStructureDatabase(this);
	}

	@Override
	public String getIdentifier() {
		return _stateMachineContext.getIdentifier();
	}

	@Override
	public int getPort() {
		return _stateMachineContext.getPort();
	}
	
	/**
	 * @param contentDefinitionProvider
	 */
	@Override
	public void setContentDefinitionProvider(IContentDefinitionProvider contentDefinitionProvider) {
		_stateMachineContext.setContentDefinitionProvider(contentDefinitionProvider);
	}

	/**
	 * @return
	 */
	@Override
	public boolean hasContentDefinitionProvider() {
		return _stateMachineContext.hasContentDefinitionProvider();
	}

	@Override
	public IHierarchicalGraph createNewHierarchicalGraph(String identifier) {

		checkState(GraphDatabaseState.RUNNING.equals(this._stateMachine.getState().getId()),
				"Database is not running:  %s", this._stateMachine.getState().getId());

		IHierarchicalGraph result =  _stateMachineContext.createHierarchicalGraph(identifier);
		_stateMachineContext.storeConfiguration();
		return result;
	}

	@Override
	public void disposeHierarchicalGraph(String identifier) {

		checkState(GraphDatabaseState.RUNNING.equals(this._stateMachine.getState().getId()),
				"Database is not running:  %s", this._stateMachine.getState().getId());

		_stateMachineContext.disposeHierarchicalGraph(identifier);
	}

	@Override
	public IHierarchicalGraph getHierarchicalGraph(String identifier) {

		if (!GraphDatabaseState.RUNNING.equals(this._stateMachine.getState().getId())) {
			return null;
		}

		return _stateMachineContext.getHierarchicalGraph(identifier);
	}

	@Override
	public List<IHierarchicalGraph> getHierarchicalGraphs() {

		if (!GraphDatabaseState.RUNNING.equals(this._stateMachine.getState().getId())) {
			return Collections.emptyList();
		}

		return _stateMachineContext.getHierarchicalGraphs();
	}

	@Override
	public void parse(boolean startDatabase) throws IOException {

		//
		Message<GraphDatabaseTrigger> triggerMessage = MessageBuilder.withPayload(GraphDatabaseTrigger.PARSE)
				.setHeader(START_DATABASE_AFTER_PARSING, startDatabase).build();

		trigger(triggerMessage);
	}

	@Override
	public void start() {
		trigger(GraphDatabaseTrigger.START);
	}

	@Override
	public void stop() {
		trigger(GraphDatabaseTrigger.STOP);
	}

	@Override
	public boolean isRunning() {
		return GraphDatabaseState.RUNNING.equals(this._stateMachine.getState().getId());
	}

	@Override
	public void dispose() {
		if (!GraphDatabaseState.TERMINATED.equals(this._stateMachine.getState().getId())) {
			trigger(GraphDatabaseTrigger.TERMINATE);
		}
	}

	@Override
	public GraphDatabaseState getState() {
		return _stateMachine.getState().getId();
	}
	
	private void trigger(Message<GraphDatabaseTrigger> triggerMessage) {

		if (!this._stateMachine.sendEvent(triggerMessage)) {
			throw new IllegalStateException(String.format("Trigger '%s' not accepted in state '%s'.",
					triggerMessage.getPayload(), this._stateMachine.getState().getId().name()));
		}
		_stateMachineContext.storeConfiguration();
	}

	private void trigger(GraphDatabaseTrigger trigger) {

		if (!this._stateMachine.sendEvent(trigger)) {
			throw new IllegalStateException(String.format("Trigger '%s' not accepted in state '%s'.", trigger,
					this._stateMachine.getState().getId().name()));
		}
		_stateMachineContext.storeConfiguration();
	}
}