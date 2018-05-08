package org.vaadin.addons.autocomplete;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.text.WordUtils;
import org.vaadin.addonhelpers.AbstractTest;

import com.google.common.base.Strings;
import com.vaadin.server.Page;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class SizeFullUsageUI extends AbstractTest {

    @Override
    public Component getTestComponent() {
        Panel panel = new Panel();
        FormLayout layout = new FormLayout();
        panel.setContent(layout);

        TextField fullSizeField = new TextField("Full size");
        fullSizeField.setSizeFull();
        AutocompleteExtension<String> fullSizeExtension = new AutocompleteExtension<>(fullSizeField);
        fullSizeExtension.setSuggestionGenerator(this::suggestPlanet);
        fullSizeExtension.addSuggestionSelectListener(event -> {
            event.getSelectedItem().ifPresent(Notification::show);
        });

        TextField fullSizeFieldWithoutExtension = new TextField("Full size without extension");
        fullSizeFieldWithoutExtension.setSizeFull();

        layout.addComponent(fullSizeField);
        layout.addComponent(fullSizeFieldWithoutExtension);

        return panel;
    }

    private List<String> suggestPlanet(String query, int cap) {
        return DataSource.getPlanets().stream()
                .filter(p -> p.toLowerCase().contains(query.toLowerCase()))
                .limit(cap).collect(Collectors.toList());
    }
}
