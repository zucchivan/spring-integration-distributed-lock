package com.zucchivan.distributedlockpoc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.dsl.Files;
import org.springframework.messaging.Message;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@EnableIntegration
@SpringBootApplication
public class DistributedLockApplication {


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

	private final DummyFileHandler dummyFileHandler;

	private static final DateTimeFormatter ARCHIVE_FILE_DATE_FORMAT =
			DateTimeFormatter.ofPattern("yyyyMMddhhmmss");

	public DistributedLockApplication(DummyFileHandler dummyFileHandler) {
		this.dummyFileHandler = dummyFileHandler;
	}

	public static void main(String[] args) {
		SpringApplication.run(DistributedLockApplication.class, args);
	}

	@Bean
	public IntegrationFlow fileProcessingFlow() {
		return IntegrationFlow.from(
						Files.inboundAdapter(new File(inputDirectory))
								.autoCreateDirectory(true)
								.preventDuplicates(true)
								.patternFilter(filePattern),
						e -> e.poller(Pollers.fixedDelay(filePollingDelay)))
				.handle(dummyFileHandler)
				.handle(Files.outboundGateway(new File(archiveDirectory))
								.fileNameGenerator(this::generateFileName)
								.deleteSourceFiles(true))
				.get();
	}

	public String generateFileName(Message<?> message) {
		return LocalDateTime.now().format(ARCHIVE_FILE_DATE_FORMAT)
				+ message.getHeaders().get(FileHeaders.FILENAME)
				+ archiveFileSuffix;
	}

}
