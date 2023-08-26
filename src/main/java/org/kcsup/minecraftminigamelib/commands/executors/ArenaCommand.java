package org.kcsup.minecraftminigamelib.commands.executors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.kcsup.minecraftminigamelib.Minigame;
import org.kcsup.minecraftminigamelib.arena.Arena;
import org.kcsup.minecraftminigamelib.game.GameState;

public class ArenaCommand implements CommandExecutor {
    private final Minigame minigame;

    public ArenaCommand(Minigame minigame) {
        this.minigame = minigame;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return false;
        }

        Player player = (Player) sender;

        String usage = getUsage(player.isOp());

        // /{name} create {WAIT_SPAWN_WORLD} {GAME_SPAWN_WORLD}

        if(args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "list":
                case "l":
                    if(minigame.getArenaManager().getArenas().isEmpty()) {
                        player.sendMessage(ChatColor.RED + "There are no available arenas at this time...");
                        return false;
                    }

                    StringBuilder arenaList = new StringBuilder(ChatColor.GREEN + "Current Arenas:");
                    for(Arena arena : minigame.getArenaManager().getArenas()) {
                        // - NAME {ID} [GAMESTATE]
                        arenaList.append("\n- ").append(arena.getName()).append(" {").append(ChatColor.AQUA)
                                .append(arena.getId()).append(ChatColor.GREEN).append("} [")
                                .append(arena.getGameState().getColor()).append(arena.getGameState())
                                .append(ChatColor.GREEN).append("]");
                    }
                    player.sendMessage(arenaList.toString());
                    break;
                case "leave":
                    if(minigame.getArenaManager().isInArena(player)) {
                        Arena arena = minigame.getArenaManager().getArena(player);

                        player.sendMessage(ChatColor.GREEN + "Leaving Arena: " + arena.getName());
                        arena.removePlayer(player);
                    }
                    else player.sendMessage(ChatColor.RED + "You aren't currently in any arena.");
                    break;
                case "reloadall":
                    if(!player.isOp()) {
                        player.sendMessage(ChatColor.RED + "You must be an admin to use this command...");
                        return false;
                    }
                    else if(minigame.getArenaManager().anyArenasLive()) {
                        player.sendMessage(ChatColor.RED + "Cannot reload arenas since one is currently live.");
                        return false;
                    }

                    minigame.getArenaManager().reloadArenas();
                    player.sendMessage(ChatColor.GREEN + "Reloaded arenas.");

                    break;
                default:
                    player.sendMessage(usage);
                    break;
            }
        }
        else if(args.length == 2) {
            Arena arena = minigame.getArenaManager().getArena(args[1]);
            if(arena == null) {
                player.sendMessage(ChatColor.RED + "There is no arena with the Id: " + args[1] + ".");
                return false;
            }

            switch(args[0].toLowerCase()) {
                case "join":
                    if (!arena.canJoin()) {
                        player.sendMessage(ChatColor.RED + "You cannot join ths arena right now." );
                        return false;
                    }

                    player.sendMessage(ChatColor.GREEN + "Joining Arena: " + arena.getName());
                    arena.addPlayer(player);

                    break;
                case "sign":
                    if(!player.isOp()) {
                        player.sendMessage(ChatColor.RED + "You must be an admin to use this command...");
                        return false;
                    }
                    else if(minigame.getSignManager().settingSign.containsKey(player)) {
                        player.sendMessage(ChatColor.RED + "You are already setting up an arena sign.");
                        return false;
                    }

                    ItemStack wand = minigame.getSignManager().getSignWand();

                    if(!player.getInventory().contains(wand)) player.getInventory().addItem(wand);

                    minigame.getSignManager().settingSign.put(player, arena);

                    break;
                case "setwaitspawn":
                    if(!player.isOp()) {
                        player.sendMessage(ChatColor.RED + "You must be an admin to use this command...");
                        return false;
                    }
                    else if(arena.getGameState() != GameState.RECRUITING) {
                        player.sendMessage(ChatColor.RED + "This arena is currently live.");
                        return false;
                    }

                    minigame.getArenaManager().updateArena(arena, null, player.getLocation(), null);
                    minigame.getArenaManager().reloadArena(arena);
                    player.sendMessage(ChatColor.GREEN + "Setting the wait spawn for '" + arena.getName() + "' to your location.");


                    break;
                case "setgamespawn":
                    if(!player.isOp()) {
                        player.sendMessage(ChatColor.RED + "You must be an admin to use this command...");
                        return false;
                    }
                    else if(arena.getGameState() != GameState.RECRUITING) {
                        player.sendMessage(ChatColor.RED + "This arena is currently live.");
                        return false;
                    }

                    minigame.getArenaManager().updateArena(arena, null, null, player.getLocation());
                    minigame.getArenaManager().reloadArena(arena);
                    player.sendMessage(ChatColor.GREEN + "Setting the game spawn for '" + arena.getName() + "' to your location.");

                    break;
                case "reload":
                    if(!player.isOp()) {
                        player.sendMessage(ChatColor.RED + "You must be an admin to use this command...");
                        return false;
                    }
                    else if(arena.getGameState() != GameState.RECRUITING) {
                        player.sendMessage(ChatColor.RED + "This arena is currently live.");
                        return false;
                    }

                    minigame.getArenaManager().reloadArena(arena);
                    player.sendMessage(ChatColor.GREEN + "Reloading arena '" + arena.getName() + "'.");

                    break;
                case "reset":
                    if(!player.isOp()) {
                        player.sendMessage(ChatColor.RED + "You must be an admin to use this command...");
                        return false;
                    }
                    else if(arena.getGameState() == GameState.RESTARTING) {
                        player.sendMessage(ChatColor.RED + "This arena is already resetting, try again later.");
                        return false;
                    }

                    arena.reset();
                    player.sendMessage(ChatColor.GREEN + "Resetting arena '" + arena.getName() + "'.");

                    break;
                default:
                    player.sendMessage(usage);
                    break;
            }
        }
        else if(args.length == 4) {
            switch(args[0].toLowerCase()) {
                case "create":
                    if(!player.isOp()) {
                        player.sendMessage(ChatColor.RED + "You must be an admin to use this command.");
                        return false;
                    }

                    String arenaName = args[1];

                    World waitWorld = Bukkit.getWorld(args[2]);
                    if(waitWorld == null) {
                        player.sendMessage(ChatColor.RED + "Cannot create arena with wait world: " + args[2] + ".");
                        return false;
                    }

                    World gameWorld = Bukkit.getWorld(args[3]);
                    if(gameWorld == null) {
                        player.sendMessage(ChatColor.RED + "Cannot create arena with game world: " + args[3] + ".");
                        return false;
                    }

                    minigame.getArenaManager().storeArena(
                            new Arena(
                                    minigame,
                                    minigame.getArenaManager().getArenas().size(),
                                    arenaName,
                                    waitWorld.getSpawnLocation(),
                                    gameWorld.getSpawnLocation())
                    );
                    break;
                default:
                    player.sendMessage(usage);
                    break;
            }
        }
        else player.sendMessage(usage);

        return false;
    }

    private String getUsage(boolean isOp) {
        String name = minigame.getCommandString();
        String usage = ChatColor.RED + "Usage:\n" +
                "/" + name + " [list/l]\n" +
                "/" + name + " join {ARENA_ID/ARENA_NAME}\n" +
                "/" + name + " leave";

        String opUsage = usage + "\n" +
                "/" + name + " sign {ARENA_ID/ARENA_NAME}\n" +
                "/" + name + " create {ARENA_NAME} {WAIT_SPAWN_WORLD} {GAME_SPAWN_WORLD}\n" +
                "/" + name + " setwaitspawn {ARENA_ID/ARENA_NAME}\n" +
                "/" + name + " setgamespawn {ARENA_ID/ARENA_NAME}\n" +
                "/" + name + " reset {ARENA_ID/ARENA_NAME}\n" +
                "/" + name + " reloadall\n" +
                "/" + name + " reload {ARENA_ID/ARENA_NAME}";

        if(isOp) return opUsage;
        else return usage;
    }
}
