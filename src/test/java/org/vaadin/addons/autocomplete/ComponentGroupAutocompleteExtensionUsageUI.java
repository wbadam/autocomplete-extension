package org.vaadin.addons.autocomplete;

import java.util.List;
import java.util.stream.Collectors;

import org.vaadin.addonhelpers.AbstractTest;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class ComponentGroupAutocompleteExtensionUsageUI extends AbstractTest {

    @Override
    public Component getTestComponent() {

        // First child
        TextField textField1 = new TextField();
        new AutocompleteExtension<String>(textField1).setSuggestionGenerator(this::getSuggestions);

        Button button1 = new Button("Button");

        CssLayout group1 = new CssLayout();
        group1.addComponents(textField1, button1);
        group1.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        // Last child
        TextField textField2 = new TextField();
        new AutocompleteExtension<String>(textField2).setSuggestionGenerator(this::getSuggestions);

        Button button2 = new Button("Button");

        CssLayout group2 = new CssLayout();
        group2.addComponents(button2, textField2);
        group2.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        // Main layout
        Layout layout = new VerticalLayout();
        layout.addComponents(group1, group2);
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
