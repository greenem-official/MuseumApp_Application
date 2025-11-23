package org.daylight.museumapp.dto.tables;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class Collection {
    private Long id;
    private String name;
    private String description;
    private User curator;
    private LocalDate startDate; // TODO maybe change date format and what about conversion
    private LocalDate endDate;
    @JsonManagedReference
    private List<Item> items;
}
