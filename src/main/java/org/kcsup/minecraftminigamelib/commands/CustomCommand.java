package org.kcsup.minecraftminigamelib.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;

public class CustomCommand extends BukkitCommand {

    private CommandExecutor commandExecutor;

    public CustomCommand(String command, @Nullable List<String> aliases) {
        super(command);

        if(aliases != null) this.setAliases(aliases);

        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            commandMap.register(command, this);
        } catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        boolean status = false;

        try {
            status = this.commandExecutor.onCommand(sender, this, commandLabel, args);
        }
        catch (Exception e) {
            sender.getServer().getLogger().info("Unhandled exception (" + e + ") in custom command: " + getName() + ".");
        }

        return status;
    }

    public void setExecutor(CommandExecutor executor) {
        this.commandExecutor = executor;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }
}
