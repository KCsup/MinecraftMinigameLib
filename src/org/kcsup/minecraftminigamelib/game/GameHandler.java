package org.kcsup.minecraftminigamelib.game;

import org.kcsup.minecraftminigamelib.arena.Arena;

abstract public class GameHandler implements Cloneable {

    public abstract void start(Arena arena);

    public abstract void stop(Arena arena);

    @Override
    public GameHandler clone() {
        try {
            return (GameHandler) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
