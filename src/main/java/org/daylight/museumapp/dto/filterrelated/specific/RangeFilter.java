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
public class RangeFilter implements FilterRule<Number> {
    @Setter
    private String field;
    @Setter
    private Number min;
    @Setter
    private Number max;

    public RangeFilter(String field) {
        this.field = field;
    }

    @Override
    public String getTitle() {
        return "В диапазоне";
    }

    @Override
    public Predicate<Number> buildPredicate() {
        return n -> n.doubleValue() >= min.doubleValue() && n.doubleValue() <= max.doubleValue();
    }

    @Override
    public Node createEditor() {
        HBox box = new HBox(8);
        box.getChildren().addAll(
                new TextField(),
                new Label(" до "),
                new TextField()
        );
        return box;
    }
}
