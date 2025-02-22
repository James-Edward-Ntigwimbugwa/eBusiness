package tz.business.eCard.ServiceImpls;

import org.springframework.stereotype.Service;
import tz.business.eCard.dtos.MessageRequestDto;
import tz.business.eCard.dtos.MessageResponseDto;
import tz.business.eCard.services.BulkSmsIntegration;
import tz.business.eCard.utils.Response;

@Service
public class BulkSmsIntegrationImpl implements BulkSmsIntegration {
    @Override
    public Response<MessageResponseDto> sendMessage(MessageRequestDto message) {
        return null;
    }
}
