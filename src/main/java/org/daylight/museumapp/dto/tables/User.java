package org.daylight.museumapp.dto.tables;

import lombok.Getter;
import org.daylight.museumapp.dto.UserRole;

/***
 * WARNING: This is backend-like class, not what the client uses
 * */
@Getter
public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private String fullName;
    private UserRole role;
}
