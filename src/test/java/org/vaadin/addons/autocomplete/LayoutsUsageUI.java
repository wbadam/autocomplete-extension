package org.vaadin.addons.autocomplete;

import java.util.List;
import java.util.stream.Collectors;

import org.vaadin.addonhelpers.AbstractTest;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LayoutsUsageUI extends AbstractTest {

    @Override
    public Component getTestComponent() {

        Layout layout = new VerticalLayout();

        layout.addComponent(new Label("HorizontalLayout"));
        layout.addComponent(addFields(new HorizontalLayout()));
        layout.addComponent(new Label("VerticalLayout"));
        layout.addComponent(addFields(new VerticalLayout()));
        layout.addComponent(new Label("FormLayout"));
        layout.addComponent(addFields(new FormLayout()));
        layout.addComponent(new Label("CssLayout"));
        layout.addComponent(addFields(new CssLayout()));
        layout.addComponent(new Label("GridLayout"));
        layout.addComponent(addFields(new GridLayout()));

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(layout);
        return panel;
    }

    private Layout addFields(Layout layout) {
        TextField textField1 = new TextField();
        new AutocompleteExtension<String>(textField1).setSuggestionGenerator(this::getSuggestions);

        TextField textField2 = new TextField();
        new AutocompleteExtension<String>(textField2).setSuggestionGenerator(this::getSuggestions);

        layout.addComponents(textField1, textField2);

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
