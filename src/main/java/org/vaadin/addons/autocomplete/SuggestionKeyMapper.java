package org.vaadin.addons.autocomplete;

import java.util.LinkedList;

import com.vaadin.server.KeyMapper;

/**
 * KeyMapper implementation that keeps reference to suggestion items within a
 * given size limit.
 */
public class SuggestionKeyMapper<V> extends KeyMapper<V> {

    private int cacheSize;
    private LinkedList<String> keys = new LinkedList<>();

    SuggestionKeyMapper() {
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    @Override
    public String key(V o) {
        String key = super.key(o);

        // Add key as last element
        keys.remove(key);
        keys.add(key);

        return key;
    }

    @Override
    public void remove(V removeobj) {
        super.remove(removeobj);

        // remove keys that don't contain value any more
        keys.removeIf(s -> !containsKey(s));
    }

    @Override
    public void removeAll() {
        super.removeAll();

        keys.clear();
    }

    /**
     * Remove oldest items that exceed {@code getCacheSize()} size limit.
     */
    public void removeOverflow() {
        while (keys.size() > cacheSize) {
            String removedKey = keys.removeFirst();

            super.remove(get(removedKey));
        }
    }
}
