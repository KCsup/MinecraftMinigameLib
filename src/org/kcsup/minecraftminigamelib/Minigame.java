package org.kcsup.minecraftminigamelib;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.kcsup.minecraftminigamelib.arena.ArenaManager;
import org.kcsup.minecraftminigamelib.arena.sign.SignManager;
import org.kcsup.minecraftminigamelib.commands.CustomCommand;
import org.kcsup.minecraftminigamelib.commands.executors.ArenaCommand;
import org.kcsup.minecraftminigamelib.game.GameHandler;
import org.kcsup.minecraftminigamelib.util.Manager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Minigame {
    private final String name, description;
    private final JavaPlugin plugin;

    private ArenaManager arenaManager;
    private SignManager signManager;

    private final int countdownSeconds;
    private final int requiredPlayers;
    private final int maximumPlayers;

    private final Location lobbySpawn;

    private List<Manager> managers;

    private final String commandString;
    private final String[] commandAliases;

    private GameHandler gameHandler;

    public MinigameConfig config;

    public Minigame(String name,
                    String description,
                    String commandString,
                    @Nullable String[] commandAliases,
                    JavaPlugin plugin,
                    int countdownSeconds,
                    int requiredPlayers,
                    int maximumPlayers,
                    Location lobbySpawn,
                    GameHandler gameHandler,
                    MinigameConfig config
    ) {
        this.name = name;
        this.plugin = plugin;
        this.description = description;
        this.commandString = commandString;
        this.commandAliases = commandAliases;

        this.countdownSeconds = countdownSeconds;
        this.requiredPlayers = requiredPlayers;
        this.maximumPlayers = maximumPlayers;
        this.lobbySpawn = lobbySpawn;

        this.gameHandler = gameHandler;

        this.config = config;

        plugin.getConfig().options().copyDefaults();
        plugin.saveDefaultConfig();

        managers = new ArrayList<>();

        // On initiation, the managers are added to the "managers" list via the "Manager" class
        arenaManager = new ArenaManager(this);
        signManager = new SignManager(this);

        plugin.getServer().getPluginManager().registerEvents(new EventListener(this), plugin);

        List<String> aliasesList = null;
        if(commandAliases != null) aliasesList = Arrays.asList(commandAliases);
        CustomCommand arenaCommand = new CustomCommand(commandString, aliasesList);
        arenaCommand.setExecutor(new ArenaCommand(this));

        for(Manager manager : managers) manager.startup();
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCommandString() {
        return commandString;
    }

    public String[] getCommandAliases() {
        return commandAliases;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public List<Manager> getManagers() {
        return managers;
    }

    public int getCountdownSeconds() {
        return countdownSeconds;
    }

    public int getRequiredPlayers() {
        return requiredPlayers;
    }

    public int getMaximumPlayers() {
        return maximumPlayers;
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public SignManager getSignManager() {
        return signManager;
    }

    public GameHandler getGameHandler() {
        return gameHandler;
    }
}
