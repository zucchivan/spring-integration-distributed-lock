package com.zucchivan.distributedlockpoc.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.dsl.FileInboundChannelAdapterSpec;
import org.springframework.integration.file.dsl.FileWritingMessageHandlerSpec;
import org.springframework.integration.file.dsl.Files;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableIntegration
public class IntegrationContextConfig {

	@Value("${file.input.directory}")
	private String inputDirectory;

	@Value("${file.input.pattern}")
	private String filePattern;

	@Value("${file.archive.directory}")
	private String archiveDirectory;

	@Value("${file.archive.suffix}")
	private String archiveFileSuffix;

	private static final DateTimeFormatter ARCHIVE_FILE_DATE_FORMAT =
			DateTimeFormatter.ofPattern("yyyyMMddhhmmss");

	@InboundChannelAdapter
	public MessageChannel queueChannel() {
		return MessageChannels.queue("queueChannel").get();
	}

	@Bean
	public MessageChannel pubsubChannel() {
		return MessageChannels.publishSubscribe().get();
	}

	@Bean
	public FileInboundChannelAdapterSpec inboundFileAdapter() {
		return Files.inboundAdapter(new File(inputDirectory))
				.autoCreateDirectory(true)
				.preventDuplicates(true)
				.patternFilter(filePattern);
	}

	@Bean
	public FileWritingMessageHandlerSpec outboundFileAdapter() {
		return Files.outboundGateway(new File(archiveDirectory))
				.fileNameGenerator(this::generateArchiveFileName)
				.deleteSourceFiles(true);
	}

	@Bean(name = "archivingFlow")
	public IntegrationFlow archivingFlow() {
		return f -> f.handle(
				Files.outboundGateway(new File(archiveDirectory))
						.fileNameGenerator(this::generateArchiveFileName)
						.deleteSourceFiles(true)
		);
	}

	private String generateArchiveFileName(Message<?> message) {
		return LocalDateTime.now().format(ARCHIVE_FILE_DATE_FORMAT)
				+ message.getHeaders().get(FileHeaders.FILENAME)
				+ archiveFileSuffix;
	}

}
