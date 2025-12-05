package org.daylight.museumapp.dto.filterrelated.specific;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.daylight.museumapp.dto.filterrelated.FilterRule;

import java.util.function.Predicate;

@Getter
@NoArgsConstructor
public class EqualsFilter<T> implements FilterRule<T> {
    @Setter
    private String field;
    @Setter
    private Object value;

    public EqualsFilter(String field) {
        this.field = field;
    }

    @Override
    public String getTitle() {
        return "Совпадает с";
    }

    @Override
    public Predicate<T> buildPredicate() {
        return null;
    }

    @Override
    public Node createEditor() {
        HBox box = new HBox(8);
        box.getChildren().addAll(
                new Label(" (не реализовано) ")
        );
        return box;
    }

    @Override
    public void extractValueFromEditor() {

    }
}
