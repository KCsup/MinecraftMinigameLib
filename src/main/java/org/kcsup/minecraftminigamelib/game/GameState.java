package org.kcsup.minecraftminigamelib.game;

import org.bukkit.ChatColor;

public enum GameState {
    RECRUITING(ChatColor.GREEN),
    COUNTDOWN(ChatColor.AQUA),
    LIVE(ChatColor.RED),
    RESTARTING(ChatColor.YELLOW);

    private final ChatColor color;

    GameState(ChatColor color) {
        this.color = color;
    }

    public ChatColor getColor() {
        return color;
    }
}
