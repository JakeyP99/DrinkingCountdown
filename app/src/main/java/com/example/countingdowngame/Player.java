package com.example.countingdowngame;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

class Player implements Serializable {
    private final String photo;
    private String name;
    private Game game;
    private final Set<Settings_WildCard_Probabilities> usedWildCards = new HashSet<>();
    int skips = 0;
    int wildcard = 2;
    private boolean selected;

    public Player(String photo, String name) {
        this.photo = photo;
        this.name = name;
        this.selected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public int getSkipAmount() {
        return skips;
    }

    public int getWildCardAmount() {
        return wildcard;
    }

    public void useWildCard() {
        if (game != null) {
            game.triggerPlayerEvent(new PlayerEvent(this, PlayerEventType.WILD_CARD));
        }
        wildcard--;
    }

    public void useSkip() {
        this.game.triggerPlayerEvent(new PlayerEvent(this, PlayerEventType.SKIP));
        this.skips=wildcard;
    }

    public void addUsedWildCard(Settings_WildCard_Probabilities usedWildCard) {
        usedWildCards.add(usedWildCard);
    }

    public void resetAbilities() {
        this.skips = 0;
        this.wildcard = 2;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}