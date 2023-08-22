package org.kcsup.minecraftminigamelib.arena;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.kcsup.minecraftminigamelib.game.GameState;

public class Countdown extends BukkitRunnable {
    private final Arena arena;

    private boolean begun;
    private int seconds;

    public Countdown(Arena arena) {
        this.arena = arena;
        begun = false;
    }

    public void begin() {
        seconds = arena.getMinigame().getCountdownSeconds();
        arena.setGameState(GameState.COUNTDOWN);
        begun = true;
        runTaskTimer(arena.getMinigame().getPlugin(), 0, 20);
    }

    public void stop() {
        this.cancel();
        begun = false;
    }

    @Override
    public void run() {
        if(seconds == 0) {
            stop();
            arena.start();
            return;
        }

        if(seconds % 30 == 0 || seconds <= 10) {
            if(seconds == 1) {
                arena.sendMessage(ChatColor.GREEN + "Game will start in 1 second.");
            } else {
                arena.sendMessage(ChatColor.GREEN + "Game will start in " + seconds + " seconds.");
            }
        }

        seconds--;
    }

    public boolean hasBegun() {
        return begun;
    }
}
