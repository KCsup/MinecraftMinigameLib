package org.kcsup.minecraftminigamelib.arena.sign;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kcsup.minecraftminigamelib.Minigame;
import org.kcsup.minecraftminigamelib.arena.Arena;
import org.kcsup.minecraftminigamelib.util.Manager;
import org.kcsup.minecraftminigamelib.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SignManager extends Manager {
    public HashMap<Player, Arena> settingSign;

    public SignManager(Minigame minigame) {
        super(
                minigame,
                "/signs.json",
                new JSONObject().put("signs", new JSONArray())
        );

        settingSign = new HashMap<>();

    }

    @Override
    public void startup() {
        reloadAllSigns();
    }

    /* Sign Data File Structure
    {
        "signs": Object[] **Array to put signs in
    }
     */

    private List<ArenaSign> getSigns() {
        if(getDataFile() == null) return null;


        List<ArenaSign> signs = new ArrayList<>();

        JSONObject file = getDataFile();
        JSONArray jSigns = file.getJSONArray("signs");

        for(Object s : jSigns) {
            ArenaSign sign = jsonToSign((JSONObject) s);
            if(sign != null) signs.add(sign);
        }

        if(!signs.isEmpty()) return signs;
        else return null;

    }

    public ArenaSign getSign(Arena arena) {
        List<ArenaSign> signs = getSigns();
        if(arena == null || signs == null) return null;

        for(ArenaSign sign : getSigns()) {
            if(sign.getArena().getId() == arena.getId()) return sign;
        }

        return null;
    }

    public ArenaSign getSign(Location location) {
        List<ArenaSign> signs = getSigns();
        if(location == null || signs == null) return null;

        for(ArenaSign sign : getSigns()) {
            if(Util.locationEquals(location, sign.getLocation())) return sign;
        }

        return null;
    }

    public void storeSign(ArenaSign sign) {
        if(sign == null) return;


        JSONObject file = getDataFile();
        JSONArray signs = file.getJSONArray("signs");
        JSONObject jsonSign = signToJson(sign);
        signs.put(jsonSign);

        updateDataFile(file);

    }

    public boolean isSign(Location location) {
        List<ArenaSign> signs = getSigns();

        if(location == null || signs == null) return false;

        for(ArenaSign s : signs) {
            if(Util.locationEquals(s.getLocation(), location)) return true;
        }

        return false;
    }

    public void reloadAllSigns() {
        List<ArenaSign> signs = getSigns();

        if(signs == null) return;

        for(ArenaSign s : signs) {
            s.reloadSign();
        }
    }

    /* Sign Json Structure
    {
        "location": Object **The location of the sign
        "arenaId": int **The id of the arena for this sign
    }
     */
    private ArenaSign jsonToSign(JSONObject jsonObject) {
        if(jsonObject == null) return null;


        Location location = Util.jsonToLocation(jsonObject.getJSONObject("location"));
        int arenaId = jsonObject.getInt("arenaId");
        Arena arena = minigame.getArenaManager().getArena(arenaId);
        if(arena == null || location == null) return null;

        return new ArenaSign(location, arena);

    }

    private JSONObject signToJson(ArenaSign sign) {
        if(sign == null) return null;


        Location location = sign.getLocation();
        Arena arena = sign.getArena();
        if(arena == null || location == null) return null;

        JSONObject jsonSign = new JSONObject();
        jsonSign.put("location", Util.locationToJson(location));
        jsonSign.put("arenaId", arena.getId());

        return jsonSign;

    }

    public ItemStack getSignWand() {
        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        wand.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        ItemMeta meta = wand.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Add Arena Sign");
        wand.setItemMeta(meta);

        return wand;
    }
}
