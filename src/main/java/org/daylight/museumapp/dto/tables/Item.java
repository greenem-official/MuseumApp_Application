package org.daylight.museumapp.dto.tables;

import org.daylight.museumapp.components.annotations.ColumnMeta;
import org.daylight.museumapp.dto.filterrelated.specific.ContainsFilter;
import org.daylight.museumapp.dto.filterrelated.specific.EndsWithFilter;
import org.daylight.museumapp.dto.filterrelated.specific.RangeFilter;
import org.daylight.museumapp.dto.filterrelated.specific.StartsWithFilter;

public class Item {
    private Long id;

    @ColumnMeta(
            title = "Название",
            filters = { ContainsFilter.class, StartsWithFilter.class, EndsWithFilter.class }
    )
    private String name;

    @ColumnMeta(
            title = "Описание",
            filters = { ContainsFilter.class, StartsWithFilter.class, EndsWithFilter.class }
    )
    private String description;

    @ColumnMeta(
            title = "Год",
            filters = { RangeFilter.class }
    )
    private Integer year;
    private String condition;
    private Collection collection;
    private Author author;
    private Hall hall;
}
