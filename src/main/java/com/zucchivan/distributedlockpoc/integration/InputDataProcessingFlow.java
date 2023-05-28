package com.zucchivan.distributedlockpoc.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowAdapter;
import org.springframework.integration.dsl.IntegrationFlowDefinition;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.FileInboundChannelAdapterSpec;
import org.springframework.integration.file.dsl.FileWritingMessageHandlerSpec;
import org.springframework.stereotype.Component;

@Component
public class InputDataProcessingFlow extends IntegrationFlowAdapter {


	@Value("${file.input.polling.delay}")
	private long filePollingDelay;

	private final FilteringContextTransformer filteringContextTransformer;

	private final ContextPersistenceHandler contextPersistenceHandler;

	private final FileInboundChannelAdapterSpec inboundFileAdapter;

	private final FileWritingMessageHandlerSpec archivingHandler;

	public InputDataProcessingFlow(FilteringContextTransformer filteringContextTransformer,
	                               ContextPersistenceHandler contextPersistenceHandler,
	                               FileInboundChannelAdapterSpec inboundFileAdapter,
	                               FileWritingMessageHandlerSpec archivingHandler) {
		this.filteringContextTransformer = filteringContextTransformer;
		this.contextPersistenceHandler = contextPersistenceHandler;
		this.inboundFileAdapter = inboundFileAdapter;
		this.archivingHandler = archivingHandler;
	}

	@Override
	protected IntegrationFlowDefinition<?> buildFlow() {
		return IntegrationFlow.from(
					inboundFileAdapter,
					e -> {
						e.id("dataProcessingFlowEndpoint");
						e.poller(Pollers.fixedDelay(filePollingDelay));
					}
				)
				.publishSubscribeChannel(
					pubsub -> {
						pubsub.id("pubSubEndpoint");
						pubsub.subscribe(flowDefinition -> flowDefinition
								.transform(filteringContextTransformer)
								.handle(contextPersistenceHandler));
						pubsub.subscribe(subflow -> subflow.handle(archivingHandler));
					}
				);
	}

}
