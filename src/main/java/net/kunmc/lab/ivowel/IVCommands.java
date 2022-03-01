package net.kunmc.lab.ivowel;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IVCommands implements TabExecutor {
    public static void init() {
        Bukkit.getPluginCommand("vowel").setExecutor(new IVCommands());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equals("vowel")) {
            return false;
        }
        if (args.length == 0) {
            sender.sendMessage("/vowel <error/lowercase/symbol/hover>");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (command.getName().equals("vowel")) {
            if (args.length == 1)
                return Arrays.asList("error", "lowercase", "symbol", "hover");
        }
        return Collections.emptyList();
    }
}
