package org.daylight.museumapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenCheckRequest {
    private String token;
}
