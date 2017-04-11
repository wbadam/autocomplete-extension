# Autocomplete Extension

Autocomplete Extension is a Vaadin add-on for TextField in Vaadin 8.

## Details

This extension adds autocomplete capability to the TextField component.
Suggestions appear below the text field according to the user query and can be selected using the keyboard or mouse.

Suggestions can be any HTML making it possible to create complex visual representation such as icons, bold text etc.

Any Java object can represent a suggestion item and converters provide a caption (to be displayed on the UI) and value (to add to the text field) for each. This way it is possible to reuse the existing data that comes from the database.

## Usage

- Create an `AutocompleteExtension` and pass the text field to be extended as parameter
  - The generic parameter is the type of a suggestion item that can be any Java object
- Set a suggestion generator
  - Implementation of `SuggestionGenerator` interface which is a `BiFunction` with
  - user query and number of suggestions as parameters and
  - list of suggestions as return value
- Optionally set caption converter and value converter
  - Caption is the visual representation of a suggestion item
    - `SuggestionCaptionConverter` is a `BiFunction` with
    - a suggestion item and the user query as parameters and
    - (HTML) string as return value (this is displayed as a suggestion item)
  - Value is set for the text field when the suggestion item is selected
    - `SuggestionValueConverter` is a `Function` with
    - a suggestion item as parameter and
    - the value as return value

### Examples

#### Simple text

```Java
TextField planetField = new TextField();

// Apply extension and set suggestion generator
AutocompleteExtension<String> planetExtension = new AutocompleteExtension<>(
        planetField);
planetExtension.setSuggestionGenerator(this::suggestPlanet);

// Notify when suggestion is selected
planetExtension.addSuggestionSelectListener(event -> {
    event.getSelectedItem().ifPresent(Notification::show);
});

// ...

// Suggestion generator function, returns a list of suggestions for a user query
private List<String> suggestPlanet(String query, int cap) {
    return DataSource.getPlanets().stream().filter(p -> p.contains(query))
        .limit(cap).collect(Collectors.toList());
}
```

#### HTML

```Java
        TextField userField = new TextField();
        userField.setWidth(250, Unit.PIXELS);

        AutocompleteExtension<DataSource.User> userSuggestion =
                new AutocompleteExtension<>(userField);
        userSuggestion.setSuggestionGenerator(
                this::suggestUsers,
                this::convertValueUser,
                this::convertCaptionUser);
        userSuggestion.setSuggestionDelay(200);  // Wait 200 milliseconds until server call when typing

    // ...

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

// ...

public class User {
    private String name;
    private String picture;

    public User(String name, String picture) {
        this.name = name;
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }
}
```
