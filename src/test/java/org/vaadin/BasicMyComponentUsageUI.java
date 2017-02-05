package org.vaadin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
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

        AutocompleteExtension<Planet> autocompleteExtension = new AutocompleteExtension<>();
        autocompleteExtension.extend(textField);

        autocompleteExtension.setSuggestionGenerator(this::suggest, null,
                (planet, query) -> {
                    SafeHtmlBuilder r = new SafeHtmlBuilder();

                    query = query.toLowerCase();

                    String n = planet.getName().toLowerCase();
                    int iStart = n.indexOf(query);
                    int iEnd = iStart + query.length();

                    if (iStart >= 0) {
                        r.appendEscaped(planet.getName().substring(0, iStart));
                        r.appendHtmlConstant("<b>");
                        r.appendEscaped(
                                planet.getName().substring(iStart, iEnd));
                        r.appendHtmlConstant("</b>");
                        r.appendEscaped(
                                planet.getName().substring(iEnd, n.length()));
                    } else {
                        r.appendEscaped(planet.getName());
                    }

                    return r.toSafeHtml();
                });

        layout.addComponent(textField);

        return layout;
    }

    private final static List<Planet> PLANETS = new ArrayList<>();

    static {
        int i = 0;
        PLANETS.add(new Planet(++i, "Mercury"));
        PLANETS.add(new Planet(++i, "Venus"));
        PLANETS.add(new Planet(++i, "Earth"));
        PLANETS.add(new Planet(++i, "Mars"));
        PLANETS.add(new Planet(++i, "Jupiter"));
        PLANETS.add(new Planet(++i, "Saturn"));
        PLANETS.add(new Planet(++i, "Uranus"));
        PLANETS.add(new Planet(++i, "Neptune"));
    }

    private List<Planet> suggest(String query, int count) {
        String queryLow = query.toLowerCase();
        return PLANETS.stream()
                .filter(p -> p.getName().toLowerCase().contains(queryLow))
                .limit(count).collect(Collectors.toList());
    }

    private static class Planet {
        private int position;
        private String name;

        public Planet(int position, String name) {
            this.position = position;
            this.name = name;
        }

        public int getPosition() {
            return position;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

}
