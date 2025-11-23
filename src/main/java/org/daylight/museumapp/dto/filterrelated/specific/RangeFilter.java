package org.daylight.museumapp.dto.filterrelated.specific;

import lombok.Getter;
import org.daylight.museumapp.dto.filterrelated.FilterRule;

@Getter
public class RangeFilter implements FilterRule {
    private String field;
    private Object from;
    private Object to;
}
