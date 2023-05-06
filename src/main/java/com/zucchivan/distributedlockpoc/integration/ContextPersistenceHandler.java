package com.zucchivan.distributedlockpoc.integration;

import com.zucchivan.distributedlockpoc.model.FilteringContext;
import com.zucchivan.distributedlockpoc.repository.FilteringContextRepository;
import org.springframework.integration.handler.AbstractMessageProducingHandler;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class ContextPersistenceHandler extends AbstractMessageProducingHandler {
  private final FilteringContextRepository filteringContextRepository;

  public ContextPersistenceHandler(FilteringContextRepository filteringContextRepository) {
    this.filteringContextRepository = filteringContextRepository;
  }

  @Override
  protected void handleMessageInternal(Message<?> message) {
    if (!(message.getPayload() instanceof FilteringContext context)) {
      /*
        TODO: Add logging & create specialized exception
       */
      throw new RuntimeException("Message payload is not a FilteringContext!");
    }

    filteringContextRepository.save(context);
  }
}
