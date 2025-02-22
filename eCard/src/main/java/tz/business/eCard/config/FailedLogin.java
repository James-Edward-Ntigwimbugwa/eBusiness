package tz.business.eCard.config;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
public class FailedLogin {
    private int count = 0;
    private LocalDateTime time = LocalDateTime.now();

    @Override
    public String toString() {
        return "Failed Login {count=" + count + ", time=" + time + "}";
    }
}
