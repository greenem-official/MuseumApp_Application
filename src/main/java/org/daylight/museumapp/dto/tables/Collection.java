package org.daylight.museumapp.dto.tables;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import org.daylight.museumapp.components.annotations.ColumnMeta;
import org.daylight.museumapp.dto.filterrelated.specific.ContainsFilter;
import org.daylight.museumapp.dto.filterrelated.specific.EndsWithFilter;
import org.daylight.museumapp.dto.filterrelated.specific.RangeFilter;
import org.daylight.museumapp.dto.filterrelated.specific.StartsWithFilter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class Collection {
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

    private User curator;

    @ColumnMeta(
            title = "Год Создания",
            filters = { RangeFilter.class }
    )
    private LocalDate startDate;

    @ColumnMeta(
            title = "Год Завершения",
            filters = { RangeFilter.class }
    )
    private LocalDate endDate;

    @JsonManagedReference
    private List<Item> items;
}
