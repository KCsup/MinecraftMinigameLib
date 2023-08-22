package org.kcsup.minecraftminigamelib;

import org.bukkit.GameMode;

public class MinigameConfig {

    public GameMode liveGameMode = GameMode.SURVIVAL;
    public GameMode waitGameMode = GameMode.ADVENTURE;

    public boolean doWorldReset = false;

    public boolean doPvP = true;
    public boolean doPlayerHunger = true;

    // Used if "doPlayerHunger" is set to false
    public int defaultFoodLevel = 19;
}
