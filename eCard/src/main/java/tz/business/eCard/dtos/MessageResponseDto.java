package tz.business.eCard.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MessageResponseDto {
    private String message;
    private String code;
}
