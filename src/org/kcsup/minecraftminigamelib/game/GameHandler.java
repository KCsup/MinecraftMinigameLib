package org.kcsup.minecraftminigamelib.game;

import org.bukkit.plugin.java.JavaPlugin;
import org.kcsup.minecraftminigamelib.arena.Arena;

abstract public class GameHandler {

    private JavaPlugin plugin;

    public GameHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void start(Arena arena);

    public abstract void stop(Arena arena);
}
