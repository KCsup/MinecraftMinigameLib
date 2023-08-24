package org.kcsup.minecraftminigamelib.util;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class Region {
    private final double lowerX, upperX, lowerY, upperY, lowerZ, upperZ;

    public Region(
            double x1,
            double x2,
            double y1,
            double y2,
            double z1,
            double z2) {
        if(x1 > x2) {
            this.lowerX = x2;
            this.upperX = x1;
        }
        else {
            this.lowerX = x1;
            this.upperX = x2;
        }

        if(y1 > y2) {
            this.lowerY = y2;
            this.upperY = y1;
        }
        else {
            this.lowerY = y1;
            this.upperY = y2;
        }

        if(z1 > z2) {
            this.lowerZ = z2;
            this.upperZ = z1;
        }
        else {
            this.lowerZ = z1;
            this.upperZ = z2;
        }
    }

    public Region(ConfigurationSection configurationSection) {
        double x1 = configurationSection.getInt("x1");
        double x2 = configurationSection.getInt("x2");
        double y1 = configurationSection.getInt("y1");
        double y2 = configurationSection.getInt("y2");
        double z1 = configurationSection.getInt("z1");
        double z2 = configurationSection.getInt("z2");

        if(x1 > x2) {
            this.lowerX = x2;
            this.upperX = x1;
        }
        else {
            this.lowerX = x1;
            this.upperX = x2;
        }

        if(y1 > y2) {
            this.lowerY = y2;
            this.upperY = y1;
        }
        else {
            this.lowerY = y1;
            this.upperY = y2;
        }

        if(z1 > z2) {
            this.lowerZ = z2;
            this.upperZ = z1;
        }
        else {
            this.lowerZ = z1;
            this.upperZ = z2;
        }
    }

    public boolean isInRegion(Location location) {
        return location.getX() >= lowerX &&
                location.getX() <= upperX &&
                location.getY() >= lowerY &&
                location.getY() <= upperY &&
                location.getZ() >= lowerZ &&
                location.getZ() <= upperZ;
    }

}
