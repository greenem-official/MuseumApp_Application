package org.daylight.museumapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenCheckResponse {
    private boolean valid;
    private String message;
}
