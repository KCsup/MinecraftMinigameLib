package org.kcsup.minecraftminigamelib.arena.sign;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.kcsup.minecraftminigamelib.arena.Arena;
import org.kcsup.minecraftminigamelib.util.Util;

public class ArenaSign {

    private final Location location;
    private final Arena arena;
    private final Sign sign;

    public ArenaSign(Location location, Arena arena) {
        this.location = location;
        this.arena = arena;

        if(Util.isSignMaterial(location.getBlock().getType())) {
            sign = (Sign) this.location.getBlock().getState();
            reloadSign();
        }
        else sign = null;
    }

    public void reloadSign() {
        if(arena == null) return;

        if(arena.getArenaSign() == null) arena.setArenaSign(this);
        reloadSign(arena.getSignLines());
    }

    public void reloadSign(String[] lines) {
        if(sign == null || lines == null) return;

        for(int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if(line != null) sign.setLine(i, ChatColor.translateAlternateColorCodes('&', line));
            else {
                sign.setLine(i, "");
            }
        }
        sign.update();
    }

    public Location getLocation() {
        return location;
    }

    public Arena getArena() {
        return arena;
    }
}
