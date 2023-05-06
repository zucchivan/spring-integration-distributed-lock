package com.zucchivan.distributedlockpoc.integration;

import com.zucchivan.distributedlockpoc.model.FilteringContext;
import generated.SampleData;
import org.apache.commons.io.FileUtils;
import org.springframework.integration.file.transformer.AbstractFilePayloadTransformer;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Component
public class FilteringContextTransformer extends AbstractFilePayloadTransformer<FilteringContext> {

	@Override
	protected FilteringContext transformFile(File file) {
		try (InputStream inputStream = FileUtils.openInputStream(file)){
			var source = new StreamSource(inputStream);
			var unmarshalled = (SampleData) jaxbMarshaller().unmarshal(source);

			return new FilteringContext();
		} catch (IOException | XmlMappingException e) {
			/*
			Throwing this generic exception is not a good practice, but it's just a PoC.
			Ideally the exception would be handled in a more specific way, like a retry or a dead letter queue.
			Hopefully this will be refactored soon. =)
			 */
			throw new RuntimeException(e);
		}
	}

	public Unmarshaller jaxbMarshaller() {
		var marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(generated.SampleData.class,
				generated.SampleData.FilteringContext.class);
		return marshaller;
	}
}
