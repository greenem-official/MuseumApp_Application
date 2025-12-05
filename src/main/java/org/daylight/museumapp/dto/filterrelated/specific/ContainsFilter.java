package org.daylight.museumapp.dto.filterrelated.specific;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.daylight.museumapp.dto.filterrelated.FilterRule;

import java.util.function.Predicate;

@Getter
@NoArgsConstructor
public class ContainsFilter implements FilterRule<String> {
    @Setter
    private String field;
    private String value;

    private HBox editorRoot;
    private TextField input;

    public ContainsFilter(String field) {
        this.field = field;
    }

    @Override
    public String getTitle() {
        return "Содержит";
    }

    @Override
    public Predicate<String> buildPredicate() {
        return s -> s != null && s.contains(value);
    }

    @Override
    public Node createEditor() {
        input = new TextField();
        editorRoot = new HBox(8,
                input
        );
        return editorRoot;
    }

    @Override
    public void extractValueFromEditor() {
        value = input.getText();
    }
}
