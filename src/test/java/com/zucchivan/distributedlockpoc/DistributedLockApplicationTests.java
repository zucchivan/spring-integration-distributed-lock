package com.zucchivan.distributedlockpoc;

import com.zucchivan.distributedlockpoc.integration.FilteringContextTransformer;
import com.zucchivan.distributedlockpoc.integration.InputDataProcessingFlow;
import com.zucchivan.distributedlockpoc.integration.IntegrationContextConfig;
import com.zucchivan.distributedlockpoc.model.FilteringContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.test.context.MockIntegrationContext;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.integration.test.mock.MockIntegration.messageArgumentCaptor;
import static org.springframework.integration.test.mock.MockIntegration.mockMessageHandler;

@SpringBootTest
@SpringIntegrationTest(noAutoStartup = {"inputDataProcessingFlow"})
@ContextConfiguration(classes = {
		IntegrationContextConfig.class,
		InputDataProcessingFlow.class
})
@TestPropertySource(
		properties = {
				"file.input.pattern=*.xml",
				"file.input.polling.delay=1000",
				"file.archive.suffix=.archive"
		})
class DistributedLockApplicationTests {

	@Mock
	FilteringContextTransformer filteringContextTransformer;

	@TempDir
	public static File inboxDir;
	@TempDir public static File archiveDir;

	@Autowired
	InputDataProcessingFlow processingFlow;

	private final MockIntegrationContext mockIntegrationContext;

	DistributedLockApplicationTests(MockIntegrationContext mockIntegrationContext) {
		this.mockIntegrationContext = mockIntegrationContext;
	}

	@BeforeAll
	public static void setup() {
		System.setProperty("file.input.directory", inboxDir.getAbsolutePath());
		System.setProperty("file.archive.directory", archiveDir.getAbsolutePath());
	}

	@BeforeEach
	public void cleanUp() {
		Mockito.when(filteringContextTransformer.transform(any())).thenReturn(any());
		cleanupDirectory(inboxDir);
		cleanupDirectory(archiveDir);
	}

	@Test
	// @Timeout(5)
	void shouldArchiveFileAfterHappyFlow() throws IOException, InterruptedException {
		/* Given */
		ArgumentCaptor<Message<?>> captor = messageArgumentCaptor();
		CountDownLatch receiveLatch = new CountDownLatch(3);
		// MessageHandler mockMessageHandler =
		//    mockMessageHandler(captor).handleNext(m -> receiveLatch.countDown());
		MessageHandler mockMessageHandler =
				mockMessageHandler().handleNextAndReply(Function.identity());
		;
		// this.mockIntegrationContext.substituteMessageHandlerFor(
		//    "fileProcessingTransformer", mockMessageHandler);
		// this.mockIntegrationContext.substituteMessageHandlerFor(
		//    "ApprovalContextTransformer", mockMessageHandler);
		this.mockIntegrationContext.substituteMessageHandlerFor("pubSubEndpoint", mockMessageHandler);//pubSubBridgeChannel
		var testFileSample = new ClassPathResource("sampleInputData.xml").getFile();

		/* When */
		processingFlow
				.getInputChannel()
				.send(MessageBuilder.withPayload(testFileSample).build());
		// await 1 second for the file to be archived
		Assertions.assertTrue(receiveLatch.await(10, TimeUnit.SECONDS));

		Assertions.assertEquals(1, Objects.requireNonNull(archiveDir.list()).length);
	}

	@Test
	void contextLoads() {
		Assertions.assertTrue(true);
	}

	@Test
	void basicFlowTest() {
		Message<?> message = MessageBuilder
				.withPayload(new File("resources/sampleInputData.xml"))
				.build();

		//dummyFileHandler.handle(new File("resources/sampleInputData.xml"), null);

		Assertions.assertTrue(true);

/*		var flow = IntegrationFlow
				.from(MockIntegration.mockMessageSource(new File("sampleInputData.xml")))
				.handle(dummyFileHandler)
				.get();*/
	}

	private static void cleanupDirectory(File dir) {
		for (File file : Objects.requireNonNull(dir.listFiles())) {
			if (file.isDirectory()) {
				cleanupDirectory(file);
			}
			file.delete();
		}
	}
}
