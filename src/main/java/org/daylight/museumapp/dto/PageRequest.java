package org.daylight.museumapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class PageRequest {
    private final int page;
    private final int size;
    private final String sortField;
    private final String sortDir; // "asc" or "desc"
    private final Map<String, String> filters;
}
