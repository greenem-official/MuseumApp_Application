package org.daylight.museumapp.dto.filterrelated;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class SortRequest {
    private String field;
    private String dir;
}
