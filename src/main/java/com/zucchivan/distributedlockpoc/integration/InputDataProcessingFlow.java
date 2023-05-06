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

	private final FileWritingMessageHandlerSpec outboundFileAdapter;


	public InputDataProcessingFlow(FilteringContextTransformer filteringContextTransformer,
	                               ContextPersistenceHandler contextPersistenceHandler,
	                               FileInboundChannelAdapterSpec inboundFileAdapter,
	                               FileWritingMessageHandlerSpec outboundFileAdapter) {
		this.filteringContextTransformer = filteringContextTransformer;
		this.contextPersistenceHandler = contextPersistenceHandler;
		this.inboundFileAdapter = inboundFileAdapter;
		this.outboundFileAdapter = outboundFileAdapter;
	}

	@Override
	protected IntegrationFlowDefinition<?> buildFlow() {
		return IntegrationFlow.from(
					inboundFileAdapter,
					e -> {
						e.id("inputDataProcessingFlowEndpoint");
						e.poller(Pollers.fixedDelay(filePollingDelay));
					}
				)
				.publishSubscribeChannel(
					pubSubConfigurer -> {
						pubSubConfigurer.id("pubSubEndpoint");
						pubSubConfigurer.subscribe(getFilteringContextSubflow());
						pubSubConfigurer.subscribe(flowDefinition -> flowDefinition.handle(outboundFileAdapter));
					}
				);
	}

	private IntegrationFlow getFilteringContextSubflow() {
		return flowDefinition -> flowDefinition
				.transform(filteringContextTransformer)
				.handle(contextPersistenceHandler);
	}
}
