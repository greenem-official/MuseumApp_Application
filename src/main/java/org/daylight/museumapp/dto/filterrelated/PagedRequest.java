package org.daylight.museumapp.dto.filterrelated;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PagedRequest {
    private int page;
    private int size;
    private SortRequest sort;
    private List<FilterRule> filters;
}
