package org.daylight.museumapp.dto.tables;

import org.daylight.museumapp.dto.UserRole;

/***
 * WARNING: This is backend-like class, not what the client uses
 * */
public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private String fullName;
    private UserRole role;
}
