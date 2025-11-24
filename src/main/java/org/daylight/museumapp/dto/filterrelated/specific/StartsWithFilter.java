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
    @Setter
    private String value;

    public StartsWithFilter(String field) {
        this.field = field;
    }

    @Override
    public String getTitle() {
        return "Начинается с";
    }

    @Override
    public Predicate<String> buildPredicate() {
        return s -> s != null && s.startsWith(value);
    }

    @Override
    public Node createEditor() {
        HBox box = new HBox(8);
        box.getChildren().addAll(
                new Label(" начинается с "),
                new TextField()
        );
        return box;
    }
}
