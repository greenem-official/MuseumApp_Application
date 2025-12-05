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

    private Double min;
    private Double max;

    private TextField minField;
    private TextField maxField;

    public RangeFilter(String field) {
        this.field = field;
    }

    @Override
    public String getTitle() {
        return "В диапазоне";
    }

    @Override
    public Predicate<Number> buildPredicate() {

        // Если оба поля пусты — не фильтруем ничего
        if (min == null && max == null) {
            return n -> true;
        }

        return n -> {
            if (n == null) return false;
            double v = n.doubleValue();

            if (min != null && v < min) return false;
            if (max != null && v > max) return false;

            return true;
        };
    }

    @Override
    public Node createEditor() {
        minField = new TextField();
        maxField = new TextField();

        minField.setPromptText("от");
        maxField.setPromptText("до");

        return new HBox(8,
                minField,
                new Label("—"),
                maxField
        );
    }

    @Override
    public void extractValueFromEditor() {
        String minText = minField.getText().trim();
        String maxText = maxField.getText().trim();

        min = parseOrNull(minText);
        max = parseOrNull(maxText);
    }

    private Double parseOrNull(String txt) {
        if (txt == null || txt.isBlank()) return null;
        try {
            return Double.parseDouble(txt.replace(",", "."));
        } catch (NumberFormatException e) {
            return null; // или можно бросить исключение, если нужно
        }
    }
}
