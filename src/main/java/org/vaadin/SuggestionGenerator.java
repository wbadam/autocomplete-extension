package org.vaadin;

import java.util.List;

@FunctionalInterface
public interface SuggestionGenerator {
    public List<String> generateSuggestions(String query,
            int numberOfSuggestions);
}
