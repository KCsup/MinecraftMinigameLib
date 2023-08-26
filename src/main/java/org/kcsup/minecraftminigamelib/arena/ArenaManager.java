package org.kcsup.minecraftminigamelib.arena;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kcsup.minecraftminigamelib.Minigame;
import org.kcsup.minecraftminigamelib.game.GameState;
import org.kcsup.minecraftminigamelib.util.Manager;
import org.kcsup.minecraftminigamelib.util.Util;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArenaManager extends Manager {

    private List<Arena> arenas;

    public ArenaManager(Minigame mingame) {
        super(
                mingame,
                "/arenaData.json",
                new JSONObject().put("arenas", new JSONArray())
        );

        arenas = new ArrayList<>();
    }

    @Override
    public void startup() {
        reloadArenas();
    }

    @Override
    public void shutdown() {
        for(Arena arena : getArenas()) arena.reset();
    }

    public void reloadArenas() {
        if(anyArenasLive()) return;

        arenas.clear();

        JSONObject file = getDataFile();
        JSONArray arenasJson = file.getJSONArray("arenas");
        for(int i = 0; i < arenasJson.length(); i++) {
            JSONObject arenaJson = arenasJson.getJSONObject(i);

            Arena arena = jsonToArena(arenaJson);
            if(arena != null) arenas.add(arena);
        }
    }

    public void reloadArena(Arena arena) {
        if(arena == null || !arenas.contains(arena) || arena.getGameState() != GameState.RECRUITING) return;

        arenas.remove(arena);

        JSONObject file = getDataFile();
        JSONArray arenasJson = file.getJSONArray("arenas");
        for(int i = 0; i < arenasJson.length(); i++) {
            JSONObject arenaJsonObject = arenasJson.getJSONObject(i);

            if(!(Objects.equals(arena.getName(), arenaJsonObject.getString("name")) ||
                    arena.getId() == arenaJsonObject.getInt("id"))) continue;

            Arena reloadedArena = jsonToArena(arenaJsonObject);
            if(reloadedArena != null) arenas.add(reloadedArena);
            break;
        }
    }

    public List<Arena> getArenas() { return arenas; }

    public boolean isInArena(Player player) {
        for(Arena arena : arenas) {
            if(arena.getPlayers().contains(player)) return true;
        }

        return false;
    }

    public boolean isInLiveGame(Player player) {
        for(Arena arena : arenas) {
            if(arena.getPlayers().contains(player) && arena.getGameState() == GameState.LIVE) return true;
        }

        return false;
    }

    public Arena getArena(Player player) {
        for(Arena arena : arenas) {
            if(arena.getPlayers().contains(player)) return arena;
        }

        return null;
    }

    public Arena getArena(int id) {
        for(Arena arena : arenas) {
            if(id == arena.getId()) return arena;
        }

        return null;
    }

    public Arena getArena(String name) {
        if(StringUtils.isNumeric(name)) return getArena(Integer.parseInt(name));

        for(Arena arena : arenas) {
            if(Objects.equals(name, arena.getName())) return arena;
        }

        return null;
    }

    public Arena getArena(World world) {
        for(Arena arena : arenas) {
            if(world.getName().equals(arena.getGameSpawn().getWorld().getName())) {
                return arena;
            }
        }

        return null;
    }

    public boolean isArenaGameWorld(World world) {
        return getArena(world) != null;
    }

    public boolean anyArenasLive() {
        for(Arena arena : arenas) {
            if (arena.getGameState() != GameState.RECRUITING) {
                return true;
            }
        }

        return false;
    }
    

    public void storeArena(Arena arena) {
        if(arena == null) return;

        JSONObject file = getDataFile();
        JSONArray arenas = file.getJSONArray("arenas");
        JSONObject jArena = arenaToJson(arena);
        arenas.put(jArena);

        updateDataFile(file);
    }

    public void updateArena(
            Arena arena,
            @Nullable String newName,
            @Nullable Location newWaitSpawn,
            @Nullable Location newGameSpawn
    ) {
        if(arena == null) return;

        JSONObject file = getDataFile();
        JSONArray arenasJson = file.getJSONArray("arenas");
        for(int i = 0; i < arenasJson.length(); i++) {
            JSONObject arenaJsonObject = arenasJson.getJSONObject(i);

            if(!(Objects.equals(arena.getName(), arenaJsonObject.getString("name")) ||
                    arena.getId() == arenaJsonObject.getInt("id"))) continue;

            if(newWaitSpawn != null) {
                Location currentWaitSpawn = Util.jsonToLocation(arenaJsonObject.getJSONObject("waitSpawn"));
                if(!Util.locationEquals(currentWaitSpawn, newWaitSpawn))
                    arenaJsonObject.put("waitSpawn", Util.locationToJson(newWaitSpawn));
            }

            if(newGameSpawn != null) {
                Location currentGameSpawn = Util.jsonToLocation(arenaJsonObject.getJSONObject("gameSpawn"));
                if(!Util.locationEquals(currentGameSpawn, newGameSpawn))
                    arenaJsonObject.put("gameSpawn", Util.locationToJson(newGameSpawn));
            }

            if(newName != null && !arenaJsonObject.getString("name").equals(newName))
                arenaJsonObject.put("name", newName);

            break;
        }

        updateDataFile(file);
    }

    /*
        {
            "name": String,
            "id": int,
            "waitSpawn": JSONObject (Bukkit Location),
            "gameSpawn": JSONObject (Bukkit Location),
        }
     */
    private JSONObject arenaToJson(Arena arena) {
        if(arena == null) return null;


        JSONObject arenaJson = new JSONObject();
        arenaJson.put("name", arena.getName());
        arenaJson.put("id", arena.getId());
        arenaJson.put("waitSpawn", Util.locationToJson(arena.getWaitSpawn()));
        arenaJson.put("gameSpawn", Util.locationToJson(arena.getGameSpawn()));

        return arenaJson;

    }


    private Arena jsonToArena(JSONObject jsonObject) {
        if (jsonObject == null) return null;


        String name = jsonObject.getString("name");
        int id = jsonObject.getInt("id");
        Location spawn = Util.jsonToLocation(jsonObject.getJSONObject("waitSpawn"));
        Location gameSpawn = Util.jsonToLocation(jsonObject.getJSONObject("gameSpawn"));

        return new Arena(minigame, id, name, spawn, gameSpawn);

    }
}
