package org.kcsup.minecraftminigamelib.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.json.JSONException;
import org.json.JSONObject;

public class Util {
    /* Location Json Structure
    {
        "world": String **World name
        "x": double **X position
        "y": double **Y position
        "z": double **Z position
        "yaw": float **Yaw rotation
        "pitch": float **Pitch rotation
    }
     */
    public static JSONObject locationToJson(Location location) {
        if(location == null) return null;

        try {
            JSONObject locationJson = new JSONObject();
            locationJson.put("world", location.getWorld().getName());
            locationJson.put("x", location.getX());
            locationJson.put("y", location.getY());
            locationJson.put("z", location.getZ());
            locationJson.put("yaw", location.getYaw());
            locationJson.put("pitch", location.getPitch());

            return locationJson;
        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Location jsonToLocation(JSONObject jsonObject) {
        if(jsonObject == null) return null;

        try {
            World world = Bukkit.getWorld(jsonObject.getString("world"));
            double x = jsonObject.getDouble("x");
            double y = jsonObject.getDouble("y");
            double z = jsonObject.getDouble("z");
            float yaw = jsonObject.getFloat("yaw");
            float pitch = jsonObject.getFloat("pitch");

            return new Location(world, x, y, z, yaw, pitch);
        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean locationEquals(Location l1, Location l2) {
        return l1.getX() == l2.getX() && l1.getY() == l2.getY() && l1.getZ() == l2.getZ();
    }

    public static boolean isSignMaterial(Material material) {
        return material == Material.SIGN_POST || material == Material.WALL_SIGN;
    }
}
