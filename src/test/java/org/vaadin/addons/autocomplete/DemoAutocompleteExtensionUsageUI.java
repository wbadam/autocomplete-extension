package org.vaadin.addons.autocomplete;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.text.WordUtils;
import org.vaadin.addonhelpers.AbstractTest;

import com.vaadin.server.Page;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class DemoAutocompleteExtensionUsageUI extends AbstractTest {

    @Override
    public Component getTestComponent() {

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        // Planets
        Label planetLabel = new Label(
                "Simple text example with planets");

        TextField planetField = new TextField();

        AutocompleteExtension<String> planetExtension = new AutocompleteExtension<>(
                planetField);
        planetExtension.setSuggestionGenerator(this::suggestPlanet);
        planetExtension.addSuggestionSelectListener(event -> {
            System.out.println(event.getSelectedValue());
        });

        // Users
        Label userLabel = new Label(
                "HTML suggestions with users' pictures and names");
        TextField userField = new TextField();
        userField.setWidth(250, Unit.PIXELS);

        AutocompleteExtension<DataSource.User> userSuggestion = new AutocompleteExtension<>(
                userField);
        userSuggestion.setSuggestionGenerator(this::suggestUsers,
                this::convertValueUser, this::convertCaptionUser);

        layout.addComponents(planetLabel, planetField, new Label(), userLabel,
                userField);

        setStyles();

        return layout;
    }

    private List<String> suggestPlanet(String query, int cap) {
        return DataSource.getPlanets().stream()
                .filter(p -> p.toLowerCase().contains(query.toLowerCase()))
                .limit(cap).collect(Collectors.toList());
    }

    private List<DataSource.User> suggestUsers(String query, int cap) {
        return DataSource.getUsers().stream()
                .filter(user -> user.getName().contains(query.toLowerCase()))
                .limit(cap).collect(Collectors.toList());
    }

    private String convertValueUser(DataSource.User user) {
        return WordUtils.capitalizeFully(user.getName(), ' ');
    }

    private String convertCaptionUser(DataSource.User user, String query) {
        return "<div class='suggestion-container'>"
                + "<img src='" + user.getPicture() + "' class='userimage'>"
                + "<span class='username'>"
                + WordUtils.capitalizeFully(user.getName(), ' ')
                .replaceAll("(?i)(" + query + ")", "<b>$1</b>")
                + "</span>"
                + "</div>";
    }

    private void setStyles() {
        Page.Styles styles = Page.getCurrent().getStyles();
        styles.add(".suggestion-container{"
                + "display:flex;"
                + "align-items:center;"
                + "padding-top:2px;"
                + "padding-bottom:2px;"
                + "}");
        styles.add(".userimage{border-radius: 50%;}");
        styles.add(".username{padding-left: 10px;}");
    }
}
