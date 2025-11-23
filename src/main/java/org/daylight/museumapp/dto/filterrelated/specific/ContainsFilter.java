package org.daylight.museumapp.dto.filterrelated.specific;

import lombok.Getter;
import org.daylight.museumapp.dto.filterrelated.FilterRule;

@Getter
public class ContainsFilter implements FilterRule {
    private String field;
    private String value;
}
