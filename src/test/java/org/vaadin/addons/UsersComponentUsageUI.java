package org.vaadin.addons;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.text.WordUtils;
import org.vaadin.addonhelpers.AbstractTest;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.vaadin.server.Page;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class UsersComponentUsageUI extends AbstractTest {

    @Override
    public Component getTestComponent() {

        Layout layout = new VerticalLayout();

        TextField textField = new TextField();
        textField.setWidth(250, Unit.PIXELS);

        AutocompleteExtension<DataSource.User> autocompleteExtension =
                new AutocompleteExtension<>(textField);

        autocompleteExtension.setSuggestionGenerator(this::getSuggestions,
                DataSource.User::getName, this::convertCaption);
        layout.addComponent(textField);

        setStyles();

        return layout;
    }

    /**
     * Returns users that have names containing the query string.
     */
    private List<DataSource.User> getSuggestions(String query, int maxCount) {
        return DataSource.getUsers().stream()
                .filter(user -> user.getName().contains(query)).limit(maxCount)
                .collect(Collectors.toList());
    }

    private SafeHtml convertCaption(DataSource.User user, String query) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();

        builder.appendHtmlConstant("<div class='suggestion-container'>");
        builder.append(SafeHtmlUtils.fromTrustedString(
                "<img src='" + user.getPicture() + "' class='userimage'>"));
        builder.appendHtmlConstant("<span class='username'>");
        builder.appendEscaped(WordUtils.capitalizeFully(user.getName(), ' '));
        builder.appendHtmlConstant("</span>");
        builder.appendHtmlConstant("</div>");

        return builder.toSafeHtml();
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
