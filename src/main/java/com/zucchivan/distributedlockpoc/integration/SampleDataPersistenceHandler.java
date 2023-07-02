package com.zucchivan.distributedlockpoc.integration;

import com.zucchivan.distributedlockpoc.model.SampleDataDTO;
import com.zucchivan.distributedlockpoc.repository.FilteringContextRepository;
import generated.SampleData;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.integration.handler.AbstractMessageProducingHandler;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.xml.stream.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class SampleDataPersistenceHandler extends AbstractMessageProducingHandler {

  private static final int BATCH_SIZE = 2;
  private final FilteringContextRepository filteringContextRepository;

  public SampleDataPersistenceHandler(FilteringContextRepository filteringContextRepository) {
    this.filteringContextRepository = filteringContextRepository;
  }

  @Override
  protected void handleMessageInternal(Message<?> message) {
    if (!(message.getPayload() instanceof SampleDataDTO sampleDataDTO)) {
      /*
        TODO: Add logging & create specialized exception
      */
      throw new RuntimeException("Message payload is not a SampleDataDTO!");
    }

    try {
      processXmlStreaming(sampleDataDTO.file(), sampleDataDTO.context().getId());
    } catch (IOException | XMLStreamException | JAXBException e) {
      /*
        TODO: fail gracefully
      */
      throw new RuntimeException(e);
    }
  }

  public void processXmlStreaming(File file, int filteringContextId) throws IOException, XMLStreamException, JAXBException {
    try (InputStream inputStream = new FileInputStream(file)) {
      XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
      XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);

      List<SampleData.AttributeMapList.AttributeMap> attributeMapsBatch = new ArrayList<>();
      int count = 0;

      while (xmlStreamReader.hasNext()) {
        int event = xmlStreamReader.next();

        if (event == XMLStreamConstants.START_ELEMENT && "AttributeMap".equals(xmlStreamReader.getLocalName())) {
          SampleData.AttributeMapList.AttributeMap attributeMap = parseAttributeMap(xmlStreamReader);
          attributeMapsBatch.add(attributeMap);
          count++;

          if (count >= BATCH_SIZE) {
            saveAttributeMapsToDatabase(attributeMapsBatch);
            attributeMapsBatch.clear();
            count = 0;
          }
        }
      }

      if (!attributeMapsBatch.isEmpty()) {
        saveAttributeMapsToDatabase(attributeMapsBatch);
      }
    }
  }

  private SampleData.AttributeMapList.AttributeMap parseAttributeMap(XMLStreamReader reader) throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(SampleData.AttributeMapList.AttributeMap.class);
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

    JAXBElement<SampleData.AttributeMapList.AttributeMap> jaxbElement = unmarshaller.unmarshal(reader, SampleData.AttributeMapList.AttributeMap.class);
    return jaxbElement.getValue();
  }

  private void saveAttributeMapsToDatabase(List<SampleData.AttributeMapList.AttributeMap> attributeMapsBatch) {
    /*
      TODO: implement =)
    */
  }
}