package com.zucchivan.distributedlockpoc.integration;

import com.zucchivan.distributedlockpoc.model.FilteringContext;
import com.zucchivan.distributedlockpoc.model.SampleDataDTO;
import com.zucchivan.distributedlockpoc.repository.FilteringContextRepository;
import org.springframework.integration.handler.AbstractMessageProducingHandler;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class SampleDataPersistenceHandler extends AbstractMessageProducingHandler {
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

    filteringContextRepository.save(sampleDataDTO.context());
  }
}
