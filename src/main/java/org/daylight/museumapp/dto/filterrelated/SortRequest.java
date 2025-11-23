package org.daylight.museumapp.dto.filterrelated;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SortRequest {
    private String field;
    private String dir;
}
