package com.zucchivan.distributedlockpoc;

import generated.SampleData;
import org.apache.commons.io.FileUtils;
import org.springframework.integration.core.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.stereotype.Component;
import org.springframework.xml.transform.StringResult;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Component
public class DummyFileHandler implements GenericHandler<File> {

	@Override
	public Object handle(File payload, MessageHeaders headers) {
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
	}

	public Unmarshaller jaxbMarshaller() {
		var marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(SampleData.class,
				SampleData.AttributeMapList.class,
				SampleData.AttributeMapList.AttributeMap.class);
		return marshaller;
	}

	public Marshaller xstreamMarshaller() {
		return new XStreamMarshaller();
	}

}
