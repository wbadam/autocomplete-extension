package org.vaadin;

import java.util.List;
import java.util.stream.Collectors;

import org.vaadin.addonhelpers.AbstractTest;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class UsersComponentUsageUI extends AbstractTest {

    @Override
    public Component getTestComponent() {

        Layout layout = new VerticalLayout();

        TextField textField = new TextField();

        AutocompleteExtension<DataSource.User> autocompleteExtension = new AutocompleteExtension<>(
                textField);

        autocompleteExtension.setSuggestionGenerator(this::getSuggestions,
                DataSource.User::getName, this::convertCaption);
        layout.addComponent(textField);

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

        builder.appendHtmlConstant(
                "<div style='display:flex; align-items:center; padding-top:2px; padding-bottom:2px;'>");
        builder.append(SafeHtmlUtils
                .fromTrustedString("<img src='" + user.getPicture() + "'>"));
        builder.appendHtmlConstant("<span style='padding-left: 10px'>");
        builder.appendEscaped(user.getName());
        builder.appendHtmlConstant("</span>");
        builder.appendHtmlConstant("</div>");

        return builder.toSafeHtml();
    }
}
