package org.daylight.museumapp.dto.filterrelated;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javafx.scene.Node;
import org.daylight.museumapp.dto.filterrelated.specific.ContainsFilter;
import org.daylight.museumapp.dto.filterrelated.specific.EqualsFilter;
import org.daylight.museumapp.dto.filterrelated.specific.RangeFilter;

import java.util.function.Predicate;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RangeFilter.class, name = "range"),
        @JsonSubTypes.Type(value = EqualsFilter.class, name = "equals"),
        @JsonSubTypes.Type(value = ContainsFilter.class, name = "contains"),
//        @JsonSubTypes.Type(value = GreaterFilter.class, name = "gt"),
//        @JsonSubTypes.Type(value = LessFilter.class, name = "lt")
})
public interface FilterRule<T> {
    String getField();
    void setField(String field);

    String getTitle();
    Predicate<T> buildPredicate();
    Node createEditor();
    void extractValueFromEditor();
}
