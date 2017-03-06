package org.vaadin.addons;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.text.WordUtils;
import org.vaadin.addonhelpers.AbstractTest;

import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class AsyncAutocompleteExtensionUsageUI extends AbstractTest {

    @Override
    public Component getTestComponent() {

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        // Planets
        Label planetLabel = new Label("Async example with planets");

        TextField planetField = new TextField();

        AutocompleteExtension<String> planetExtension = new AutocompleteExtension<>(
                planetField);
        planetExtension.setSuggestionGenerator(this::suggestPlanet);

        TextField listSizeField = new TextField();
        Button listSizeButton = new Button("Set list size", e -> {
            try {
                Integer newSize = Integer.parseInt(listSizeField.getValue());
                planetExtension.setSuggestionListSize(newSize);
            } catch (NumberFormatException nfe) {
                System.err.println("Not a number");
            }
        });
        HorizontalLayout setFieldLayout = new HorizontalLayout(listSizeField,
                listSizeButton);

        layout.addComponents(planetLabel, planetField, new Label(),
                setFieldLayout, new Button("poll", e -> {}));

        return layout;
    }

    /**
     * Asynchronous generator function
     */
    private void suggestPlanet(String query, int cap,
            Consumer<List<String>> callback) {
        Runnable querySimulation = () -> {
            // Simulates db latency
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            callback.accept(DataSource.getPlanets().stream()
                    .filter(p -> p.toLowerCase().contains(query.toLowerCase()))
                    .limit(cap).collect(Collectors.toList()));

//            getUI().access(() -> {
//                setPollInterval(-1);
//            });
        };
        
//        getUI().setPollInterval(300);
        // Start query simulation on another thread
        new Thread(querySimulation).start();
    }

//    /**
//     * Synchronous generator function
//     */
//    private List<String> suggestPlanet(String query, int cap) {
//        return DataSource.getPlanets().stream()
//                .filter(p -> p.toLowerCase().contains(query.toLowerCase()))
//                .limit(cap).collect(Collectors.toList());
//    }
}
