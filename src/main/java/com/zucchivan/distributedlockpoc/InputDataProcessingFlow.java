package com.zucchivan.distributedlockpoc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowAdapter;
import org.springframework.integration.dsl.IntegrationFlowDefinition;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.dsl.FileInboundChannelAdapterSpec;
import org.springframework.integration.file.dsl.FileWritingMessageHandlerSpec;
import org.springframework.integration.file.dsl.Files;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class InputDataProcessingFlow extends IntegrationFlowAdapter {

	@Value("${file.input.directory}")
	private String inputDirectory;

	@Value("${file.input.pattern}")
	private String filePattern;

	@Value("${file.archive.directory}")
	private String archiveDirectory;

	@Value("${file.input.polling.delay}")
	private long filePollingDelay;

	@Value("${file.archive.suffix}")
	private String archiveFileSuffix;

	private final FilteringContextTransformer filteringContextTransformer;

	private final ContextPersistenceHandler contextPersistenceHandler;

	private static final DateTimeFormatter ARCHIVE_FILE_DATE_FORMAT =
			DateTimeFormatter.ofPattern("yyyyMMddhhmmss");

	public InputDataProcessingFlow(FilteringContextTransformer filteringContextTransformer,
	                               ContextPersistenceHandler contextPersistenceHandler) {
		this.filteringContextTransformer = filteringContextTransformer;
		this.contextPersistenceHandler = contextPersistenceHandler;
	}

	@Override
	protected IntegrationFlowDefinition<?> buildFlow() {
		return IntegrationFlow.from(
					getInboundFileAdapter(),
					e -> {
						e.id("inputDataProcessingFlowEndpoint");
						e.poller(Pollers.fixedDelay(filePollingDelay));
					}
				)
				.publishSubscribeChannel(
					pubSubConfigurer -> {
						pubSubConfigurer.id("pubSubEndpoint");
						pubSubConfigurer.subscribe(getFilteringContextSubflow());
						pubSubConfigurer.subscribe(flowDefinition -> flowDefinition.handle(getOutboundFileAdapter()));
					}
				);
	}

	private IntegrationFlow getFilteringContextSubflow() {
		return flowDefinition -> flowDefinition.transform(filteringContextTransformer)
				.handle(contextPersistenceHandler);
	}

	private FileInboundChannelAdapterSpec getInboundFileAdapter() {
		return Files.inboundAdapter(new File(inputDirectory))
				.autoCreateDirectory(true)
				.preventDuplicates(true)
				.patternFilter(filePattern);
	}

	private FileWritingMessageHandlerSpec getOutboundFileAdapter() {
		return Files.outboundGateway(new File(archiveDirectory))
				.fileNameGenerator(this::generateArchiveFileName)
				.deleteSourceFiles(true);
	}

	public String generateArchiveFileName(Message<?> message) {
		return LocalDateTime.now().format(ARCHIVE_FILE_DATE_FORMAT)
				+ message.getHeaders().get(FileHeaders.FILENAME)
				+ archiveFileSuffix;
	}
}
