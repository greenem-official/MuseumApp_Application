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
public class StartsWithFilter implements FilterRule<String> {

    @Setter
    private String field;

    private String value;

    private TextField input;

    public StartsWithFilter(String field) {
        this.field = field;
    }

    @Override
    public String getTitle() {
        return "Начинается с";
    }

    @Override
    public Predicate<String> buildPredicate() {
        // пустой фильтр → ничего не фильтруем
        if (value == null || value.isBlank()) {
            return s -> true;
        }

        String v = value.toLowerCase();

        return s -> s != null && s.toLowerCase().startsWith(v);
    }

    @Override
    public Node createEditor() {
        input = new TextField();
        input.setPromptText("начинается с…");

        return new HBox(8,
                input
        );
    }

    @Override
    public void extractValueFromEditor() {
        value = input.getText() == null ? "" : input.getText().trim();
    }
}
