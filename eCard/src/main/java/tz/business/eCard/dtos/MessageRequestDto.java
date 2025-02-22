package tz.business.eCard.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageRequestDto {
    private String message;
    private String senderId;
    private String receiversPhoneNumber;
    private  String dateOTPSent;
    private final String voteCode = "MIKUTANO";
}
