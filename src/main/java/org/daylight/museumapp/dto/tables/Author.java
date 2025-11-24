package org.daylight.museumapp.dto.tables;

import lombok.Getter;
import org.daylight.museumapp.components.annotations.ColumnMeta;
import org.daylight.museumapp.dto.filterrelated.specific.ContainsFilter;
import org.daylight.museumapp.dto.filterrelated.specific.EndsWithFilter;
import org.daylight.museumapp.dto.filterrelated.specific.RangeFilter;
import org.daylight.museumapp.dto.filterrelated.specific.StartsWithFilter;

@Getter
public class Author {
    private Long id;

    @ColumnMeta(
            title = "Название",
            filters = { ContainsFilter.class, StartsWithFilter.class, EndsWithFilter.class }
    )
    private String name;

    @ColumnMeta(
            title = "Год Рождения",
            filters = { RangeFilter.class }
    )
    private Integer birthYear;

    @ColumnMeta(
            title = "Год Смерти",
            filters = { RangeFilter.class }
    )
    private Integer deathYear;

    @ColumnMeta(
            title = "Страна",
            filters = { ContainsFilter.class, StartsWithFilter.class, EndsWithFilter.class }
    )
    private String country;
}
