package tz.business.eCard.ServiceImpls;


import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tz.business.eCard.dtos.MessageRequestDto;
import tz.business.eCard.dtos.MessageResponseDto;
import tz.business.eCard.services.BulkSmsIntegration;
import tz.business.eCard.utils.Response;
import tz.business.eCard.utils.ResponseCode;

@Service
public class BulkSmsIntegrationImpl implements BulkSmsIntegration {
    @Value("${account.twilio.phone-number}")
    private String sendersNumber;


    @Override
    public Response<MessageResponseDto> sendMessage(MessageRequestDto message) {
        try {
            System.out.println("Method received========>");

            if (Twilio.getRestClient() == null) {
                throw  new IllegalStateException("Twilio rest client is null");
            }
            PhoneNumber from = new PhoneNumber(sendersNumber);
            PhoneNumber to = new PhoneNumber(message.getReceiversPhoneNumber());

            Message twilioMessage = Message.creator(to, from, message.getMessage()).create();
            MessageResponseDto messageResponseDto = new MessageResponseDto(twilioMessage.getSid(), twilioMessage.getStatus().toString());

            return new Response<>(false, ResponseCode.SUCCESS,"Sms Sent Successfully", messageResponseDto);
        } catch (ApiException e) {
            System.out.println("Error occured========>" + e.getMessage());
            return new Response<>(true, ResponseCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            System.out.println("Error occured========>" + e.getMessage());
            return new Response<>(true, ResponseCode.BAD_REQUEST, e.getMessage());
        }
    }
}
