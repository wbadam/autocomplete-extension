package org.vaadin.addons.autocomplete;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class DataSource {
    private final static List<User> USERS = new ArrayList<>(50);

    static {
        try {
            URL randomuser = new URL(
                    "https://randomuser.me/api/?inc=gender,name,picture&results=50");
            InputStreamReader reader = new InputStreamReader(
                    randomuser.openConnection().getInputStream());

            JsonElement json = new JsonParser().parse(reader);

            json.getAsJsonObject().get("results").getAsJsonArray()
                    .forEach(result -> {
                        JsonObject name = result.getAsJsonObject().get("name")
                                .getAsJsonObject();
                        JsonObject pict = result.getAsJsonObject()
                                .get("picture").getAsJsonObject();

                        USERS.add(new User(
                                name.get("first").getAsString() + " " + name
                                        .get("last").getAsString(),
                                pict.get("thumbnail").getAsString()));
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class User {
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

    private final static List<String> PLANETS = Arrays
            .asList("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn",
                    "Uranus", "Neptune");

    /**
     * Returns a list of users for testing purposes.
     *
     * @return List of users. Returned list is unmodifiable.
     */
    static List<User> getUsers() {
        return Collections.unmodifiableList(USERS);
    }

    /**
     * Returns the list of planets as string
     *
     * @return List of planets. Unmodifiable.
     */
    static List<String> getPlanets() {
        return Collections.unmodifiableList(PLANETS);
    }
}
