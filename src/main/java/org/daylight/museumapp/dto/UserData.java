package org.daylight.museumapp.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserData {
    private String token;
    private String username;
    private String fullName;
    private UserRole role;
}
