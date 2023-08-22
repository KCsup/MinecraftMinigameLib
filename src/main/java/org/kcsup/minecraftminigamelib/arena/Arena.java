package org.kcsup.minecraftminigamelib.arena;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.kcsup.minecraftminigamelib.Minigame;
import org.kcsup.minecraftminigamelib.arena.sign.ArenaSign;
import org.kcsup.minecraftminigamelib.game.Game;
import org.kcsup.minecraftminigamelib.game.GameState;

import java.util.ArrayList;
import java.util.List;

public class Arena {
    private final Minigame minigame;

    private final int id;
    private final String name;

    private List<Player> players;

    // Notes of clarification
    private final Location waitSpawn; // Waiting spawn
    private final Location gameSpawn; // Spawn for when the game starts

    private GameState gameState;
    private Countdown countdown;

    private ArenaSign arenaSign;

    private Game game;

    public Arena(Minigame minigame, int id, String name, Location waitSpawn, Location gameSpawn) {
        this.minigame = minigame;
        this.id = id;
        this.name = name;
        players = new ArrayList<>();
        this.waitSpawn = waitSpawn;
        this.gameSpawn = gameSpawn;
        countdown = new Countdown(this);

        arenaSign = null;

        game = new Game(this);

        setGameState(GameState.RECRUITING);
    }

    public void start() {
        setGameState(GameState.LIVE);
        teleportPlayers(gameSpawn);
        game.start();
        sendMessage(ChatColor.GREEN + "--------------------------------------------\n" +
                ChatColor.YELLOW + minigame.getName() + "\n" +
                ChatColor.AQUA + minigame.getDescription() + "\n" +
                ChatColor.GREEN + "--------------------------------------------");

        for(Player player : players) player.setGameMode(minigame.config.liveGameMode);
    }

    public void reset() {
        setGameState(GameState.RESTARTING);

        teleportPlayers(minigame.getLobbySpawn());

        for(Player player : players) {
            player.setGameMode(minigame.config.waitGameMode);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getInventory().clear();
        }
        players.clear();
        countdown = new Countdown(this);
        game = new Game(this);

        // WORLD RESET LOGIC
        if(minigame.config.doWorldReset) {
            String gameWorldName = gameSpawn.getWorld().getName();
            Bukkit.unloadWorld(gameWorldName, false);
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                    minigame.getPlugin(),
                    () -> {
                        World world = Bukkit.createWorld(new WorldCreator(gameWorldName));
                        world.setAutoSave(false);
                    },
                    10*20L
            );
        }
        // End logic

        // Lobby can be open to recruiting when the world is fully loaded
    }

    public void restartCountdown() {
        countdown.stop();
        countdown = new Countdown(this);
        sendMessage(ChatColor.RED + "Waiting for more players.");
        setGameState(GameState.RECRUITING);
    }

    public void teleportPlayers(Location location) {
        for(Player player : players) {
            if(player == null) continue;

            player.teleport(location);
        }
    }

    public void sendMessage(String message) {
        for (Player player : players) {
            if(player == null) continue;

            player.sendMessage(message);
        }
    }

    public void sendSound(Sound sound, float volume, float pitch) {
        for (Player player : players) {
            if(player == null) continue;

            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public void sendTitle(String title, String subtitle) {
        for (Player player : players) {
            if(player == null) continue;

            player.sendTitle(title, subtitle);
        }
    }

    public void addPlayer(Player player) {
        if(players.contains(player) || !canJoin()) return;

        players.add(player);
        player.teleport(waitSpawn);
        sendMessage(ChatColor.GREEN + player.getName() + " has joined!");

        if(hasRequiredPlayers() && !countdown.hasBegun()) countdown.begin();
        else reloadSign();

        player.setHealth(20);
        if(!minigame.config.doPlayerHunger) player.setFoodLevel(minigame.config.defaultFoodLevel);
        player.setGameMode(minigame.config.waitGameMode);
        player.getInventory().clear();
    }

    public void removePlayer(Player player) {
        if(!players.contains(player)) return;

        players.remove(player);
        player.teleport(minigame.getLobbySpawn());

        sendMessage(ChatColor.GREEN + player.getName() + " has quit!");

        if(!hasRequiredPlayers() && gameState.equals(GameState.COUNTDOWN)) restartCountdown();
        else if(players.size() <= 1 && gameState.equals(GameState.LIVE)) reset();
        else reloadSign();
    }

    public String[] getSignLines() {
        if(arenaSign == null) return null;

        String[] lines = new String[4];
        lines[0] = name;
        lines[3] = ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + minigame.getName() + ChatColor.GRAY + "]";

        lines[1] = String.format("%s/%s", players.size(), minigame.getMaximumPlayers());

        ChatColor stateColor;
        switch(gameState) {
            case RECRUITING:
                stateColor = ChatColor.GREEN;
                break;
            case COUNTDOWN:
                stateColor = ChatColor.AQUA;
                break;
            case LIVE:
                stateColor = ChatColor.RED;
                break;
            case RESTARTING:
                stateColor = ChatColor.YELLOW;
                break;
            default:
                stateColor = null;
                break;
        }

        if(stateColor == null) return null;

        lines[2] = stateColor + gameState.toString();

        return lines;
    }

    public Minigame getMinigame() {
        return minigame;
    }

    public int getId() { return id; }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() { return players; }

    public Location getWaitSpawn() {
        return waitSpawn;
    }

    public Location getGameSpawn() {
        return gameSpawn;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean hasRequiredPlayers() { return players.size() >= minigame.getRequiredPlayers(); }

    public boolean isFull() {
        return players.size() >= minigame.getMaximumPlayers();
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;

        reloadSign();
    }

    public ArenaSign getArenaSign() {
        return arenaSign;
    }

    public void setArenaSign(ArenaSign arenaSign) {
        this.arenaSign = arenaSign;
    }

    public void reloadSign() {
        if(arenaSign != null) arenaSign.reloadSign();
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean canJoin() {
        return !(gameState == GameState.LIVE || gameState == GameState.RESTARTING) &&
                !isFull();
    }
}
