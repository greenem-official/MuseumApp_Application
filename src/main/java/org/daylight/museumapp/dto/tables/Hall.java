package org.daylight.museumapp.dto.tables;

import lombok.Getter;
import org.daylight.museumapp.components.annotations.ColumnMeta;
import org.daylight.museumapp.dto.filterrelated.specific.ContainsFilter;
import org.daylight.museumapp.dto.filterrelated.specific.EndsWithFilter;
import org.daylight.museumapp.dto.filterrelated.specific.StartsWithFilter;

@Getter
public class Hall {
    private Long id;

    @ColumnMeta(
            title = "Название",
            filters = { ContainsFilter.class, StartsWithFilter.class, EndsWithFilter.class }
    )
    private String name;

    private Integer floor;

    @ColumnMeta(
            title = "Описание",
            filters = { ContainsFilter.class, StartsWithFilter.class, EndsWithFilter.class }
    )
    private String description;
}
