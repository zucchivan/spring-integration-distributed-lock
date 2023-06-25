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

	private final FilteringContextHandler filteringContextHandler;

	private final SampleDataPersistenceHandler sampleDataPersistenceHandler;

	private final FileInboundChannelAdapterSpec inboundFileAdapter;

	private final FileWritingMessageHandlerSpec archivingHandler;

	public InputDataProcessingFlow(FilteringContextHandler filteringContextHandler,
	                               SampleDataPersistenceHandler sampleDataPersistenceHandler,
	                               FileInboundChannelAdapterSpec inboundFileAdapter,
	                               FileWritingMessageHandlerSpec archivingHandler) {
		this.filteringContextHandler = filteringContextHandler;
		this.sampleDataPersistenceHandler = sampleDataPersistenceHandler;
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
								.handle(filteringContextHandler)
								.handle(sampleDataPersistenceHandler));
						pubsub.subscribe(subflow -> subflow.handle(archivingHandler));
					}
				);
	}

}
