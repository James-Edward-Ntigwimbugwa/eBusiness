package tz.business.eCard.services;

import tz.business.eCard.dtos.MessageRequestDto;
import tz.business.eCard.dtos.MessageResponseDto;
import tz.business.eCard.utils.Response;

public interface BulkSmsIntegration {
    Response<MessageResponseDto> sendMessage(MessageRequestDto message);
}
