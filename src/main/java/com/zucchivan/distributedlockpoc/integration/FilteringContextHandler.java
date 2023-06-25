package com.zucchivan.distributedlockpoc.integration;

import com.zucchivan.distributedlockpoc.model.FilteringContext;
import com.zucchivan.distributedlockpoc.model.SampleDataDTO;
import generated.SampleData;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.springframework.integration.core.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class FilteringContextHandler implements GenericHandler<File> {

	@Override
	public SampleDataDTO handle(File payload, MessageHeaders headers) {
		if (!(payload instanceof File file)) {
	        /*
 	            TODO: Add logging & create specialized exception
	        */
			throw new RuntimeException("Message payload is not a FilteringContext!");
		}

		try (InputStream inputStream = new FileInputStream(file)) {
			var jaxbContext = JAXBContext.newInstance(SampleData.class);
			var unmarshaller = jaxbContext.createUnmarshaller();
			var source = new StreamSource(inputStream);
			var unmarshalled = unmarshaller.unmarshal(source, SampleData.class).getValue();
			var filteringContext = new FilteringContext(unmarshalled.getFilteringContext());

			return new SampleDataDTO(filteringContext, file);
		} catch (IOException | JAXBException e) {
	        /*
	        Throwing a generic exception is not recommended, but as this is just a PoC, it's fine.
	        Ideally, the exception should be handled in a more specific way, such as retrying or using a dead letter queue.
	        Please consider refactoring this code to handle exceptions appropriately, or at least throwing a more specialized one.
	         */
			throw new RuntimeException(e);
		}
	}
}
