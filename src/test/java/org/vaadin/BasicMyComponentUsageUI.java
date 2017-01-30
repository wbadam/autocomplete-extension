package org.vaadin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import org.vaadin.addonhelpers.AbstractTest;

public class BasicMyComponentUsageUI extends AbstractTest {

    @Override
    public Component getTestComponent() {

        Layout layout = new VerticalLayout();

        TextField textField = new TextField();

        AutocompleteExtension autocompleteExtension = new AutocompleteExtension();
        autocompleteExtension.extend(textField);

        autocompleteExtension.setSuggestionGenerator(this::suggest);

        layout.addComponent(textField);

        return layout;
    }

    private final static List<String> PLANETS = Arrays
            .asList("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn",
                    "Uranus", "Neptune");

    private List<String> suggest(String query, int count) {
        String queryLow = query.toLowerCase();
        return PLANETS.stream()
                .filter(s -> s.toLowerCase().startsWith(queryLow)).limit(count)
                .collect(Collectors.toList());
    }

}
