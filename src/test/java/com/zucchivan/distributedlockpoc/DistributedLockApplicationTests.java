package com.zucchivan.distributedlockpoc;

import com.zucchivan.distributedlockpoc.integration.FilteringContextTransformer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.messaging.Message;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;

@SpringBootTest
@SpringIntegrationTest
@ContextConfiguration(classes = FilteringContextTransformer.class)
class DistributedLockApplicationTests {

	//private final MockIntegrationContext mockIntegrationContext;

	@Autowired
	private FilteringContextTransformer filteringContextTransformer;

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

}
