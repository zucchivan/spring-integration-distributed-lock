package com.zucchivan.distributedlockpoc;

import generated.SampleData;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.dsl.Files;
import org.springframework.messaging.Message;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.xml.transform.StringResult;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

	private static final DateTimeFormatter ARCHIVE_FILE_DATE_FORMAT =
			DateTimeFormatter.ofPattern("yyyyMMddhhmmss");

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
				.handle(dummyFileHandler())
				.handle(Files.outboundGateway(new File(archiveDirectory))
								.fileNameGenerator(this::generateFileName)
								.deleteSourceFiles(true))
				.get();
	}

	@Bean
	public Unmarshaller jaxbMarshaller() {
		var marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(SampleData.class,
				SampleData.AttributeMapList.class,
				SampleData.AttributeMapList.AttributeMap.class);
		return marshaller;
	}

	@Bean
	public Marshaller xstreamMarshaller() {
		return new XStreamMarshaller();
	}

	@Bean
	public GenericHandler<File> dummyFileHandler() {
		return (payload, headers) -> {
			try (InputStream inputStream = FileUtils.openInputStream(payload)){
				var source = new StreamSource(inputStream);
				var unmarshalled = (SampleData) jaxbMarshaller().unmarshal(source);

				var result = new StringResult();
				xstreamMarshaller().marshal(unmarshalled, result);
			} catch (IOException | XmlMappingException e) {
				/*
				Throwing this generic exception is not a good practice, but it's just a PoC.
				Ideally the exception would be handled in a more specific way, like a retry or a dead letter queue.
				Hopefully this will be refactored soon. =)
				 */
				throw new RuntimeException(e);
			}

			return payload;
		};
	}

	public String generateFileName(Message<?> message) {
		return LocalDateTime.now().format(ARCHIVE_FILE_DATE_FORMAT)
				+ message.getHeaders().get(FileHeaders.FILENAME)
				+ archiveFileSuffix;
	}

}
