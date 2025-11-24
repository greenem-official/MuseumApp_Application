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
public class EndsWithFilter implements FilterRule<String> {
    @Setter
    private String field;
    @Setter
    private String value;

    public EndsWithFilter(String field) {
        this.field = field;
    }

    @Override
    public String getTitle() {
        return "Заканчивается на";
    }

    @Override
    public Predicate<String> buildPredicate() {
        return s -> s != null && s.endsWith(value);
    }

    @Override
    public Node createEditor() {
        HBox box = new HBox(8);
        box.getChildren().addAll(
                new Label(" заканчивается на "),
                new TextField()
        );
        return box;
    }
}
