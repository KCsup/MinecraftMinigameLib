package org.kcsup.minecraftminigamelib.game;

import org.kcsup.minecraftminigamelib.arena.Arena;

public class Game {
    private final Arena arena;

    public Game(Arena arena) {
        this.arena = arena;
    }

    public void start() {
        arena.getMinigame().getGameHandler().start(arena);
    }

    public void stop() {
        arena.getMinigame().getGameHandler().stop(arena);
    }
}
