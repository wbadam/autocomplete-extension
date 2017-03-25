package org.vaadin.addons.autocomplete;

import java.util.List;
import java.util.stream.Collectors;

import org.vaadin.addonhelpers.AbstractTest;

import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class GridLayoutAutocompleteExtensionUsageUI extends AbstractTest {

    @Override
    public Component getTestComponent() {

        Layout layout = new VerticalLayout();

        GridLayout gridLayout = new GridLayout(2, 2);
        gridLayout.setSpacing(true);

        TextField textField = new TextField();
        new AutocompleteExtension<String>(textField)
                .setSuggestionGenerator(this::getSuggestions);

        gridLayout.addComponent(textField);
        gridLayout.addComponent(new Label("label1"));
        gridLayout.addComponent(new Label("label2"));
        gridLayout.addComponent(new Label("label3"));

        layout.addComponent(gridLayout);

        return layout;
    }

    /**
     * Returns planets containing the query string.
     */
    private List<String> getSuggestions(String query, int maxCount) {
        return DataSource.getPlanets().stream()
                .filter(s -> s.toLowerCase().contains(query.toLowerCase()))
                .limit(maxCount).collect(Collectors.toList());
    }
}
