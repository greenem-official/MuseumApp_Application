package org.daylight.museumapp.dto.tables;

import lombok.Getter;

@Getter
public class Author {
    private Long id;
    private String name;
    private Integer birthYear;
    private Integer deathYear;
    private String country;
}
