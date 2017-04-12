package org.vaadin.addons.autocomplete;

import java.util.List;
import java.util.stream.Collectors;

import org.vaadin.addonhelpers.AbstractTest;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class AddRemoveUsageUI extends AbstractTest {

    private TextField textField;
    private AutocompleteExtension<String> extension;

    @Override
    public Component getTestComponent() {

        VerticalLayout layout = new VerticalLayout();

        textField = createField();
        extension = createExtension(textField);

        Button removeExtension = new Button("Remove extension",
                e -> extension.remove());

        Button addExtension = new Button("Add extension",
                e -> extension = createExtension(textField));

        Button removeField = new Button("Remove field",
                e -> layout.removeComponent(textField));

        Button addField = new Button("Add field",
                e -> layout.addComponentAsFirst(textField));

        layout.addComponents(textField,
                new HorizontalLayout(removeExtension, addExtension),
                new HorizontalLayout(removeField, addField));

        return layout;
    }

    private TextField createField() {
        return new TextField();
    }

    private AutocompleteExtension<String> createExtension(TextField field) {
        AutocompleteExtension<String> extension = new AutocompleteExtension<>(
                field);
        extension.setSuggestionGenerator(this::suggestPlanet);
        extension.addSuggestionSelectListener(event -> {
            event.getSelectedItem().ifPresent(Notification::show);
        });
        return extension;
    }

    private List<String> suggestPlanet(String query, int cap) {
        return DataSource.getPlanets().stream()
                .filter(p -> p.toLowerCase().contains(query.toLowerCase()))
                .limit(cap).collect(Collectors.toList());
    }
}
