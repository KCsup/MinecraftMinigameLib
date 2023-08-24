package org.kcsup.minecraftminigamelib;

import org.kcsup.minecraftminigamelib.game.GameState;
import org.kcsup.minecraftminigamelib.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.kcsup.minecraftminigamelib.arena.Arena;
import org.kcsup.minecraftminigamelib.arena.sign.ArenaSign;
import org.kcsup.minecraftminigamelib.util.Manager;

public class EventListener implements Listener {
    private final Minigame minigame;

    public EventListener(Minigame minigame) {
        this.minigame = minigame;
    }

    @EventHandler
    public void onDisable(PluginDisableEvent disableEvent) {
        for(Manager manager : minigame.getManagers()) manager.shutdown();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if(e.hasBlock()) {
            Block block = e.getClickedBlock();

            if(player.isOp()) {
                if(Util.isSignMaterial(block.getType()) && player.getItemInHand().equals(minigame.getSignManager().getSignWand())
                        && !minigame.getSignManager().isSign(block.getLocation())
                        && minigame.getSignManager().settingSign.containsKey(player)) {
                    e.setCancelled(true);

                    ArenaSign sign = new ArenaSign(block.getLocation(), minigame.getSignManager().settingSign.get(player));
                    player.sendMessage("Storing Sign for Arena: " + minigame.getSignManager().settingSign.get(player).getId());
                    minigame.getSignManager().storeSign(sign);
                    sign.reloadSign();
                    minigame.getSignManager().settingSign.remove(player);
                    player.getInventory().remove(minigame.getSignManager().getSignWand());

                    return;
                }
            }

            if(minigame.getSignManager().isSign(block.getLocation())) {
                e.setCancelled(true);

                ArenaSign sign = minigame.getSignManager().getSign(block.getLocation());
                if(sign == null) return;

                Arena arenaFromSign = sign.getArena();
                if(arenaFromSign == null) return;

                if(arenaFromSign.canJoin()) arenaFromSign.addPlayer(player);
                else player.sendMessage(ChatColor.RED + "You cannot join ths arena right now.");
            }
        }
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player player = (Player) e.getEntity();
            Player damager = (Player) e.getDamager();

            if(minigame.getArenaManager().isInArena(player) &&
                minigame.getArenaManager().isInArena(player) &&
                minigame.getArenaManager().getArena(player).getId() ==
                        minigame.getArenaManager().getArena(damager).getId()) {
                Arena arena = minigame.getArenaManager().getArena(player);

                if(arena.getGameState() != GameState.LIVE) {
                    e.setCancelled(true);
                    return;
                }

                if(!minigame.config.doPvP) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e){
        if(e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();

            if(!minigame.config.doPlayerHunger && minigame.getArenaManager().isInArena(player)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        Arena arena = minigame.getArenaManager().getArena(e.getWorld());

        if(arena != null) {
            System.out.println("Setting \"" + arena.getName() + "\" to Recruiting");
            arena.setGameState(GameState.RECRUITING);
            System.out.println(arena.getGameState());
        }
    }
}
