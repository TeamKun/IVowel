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
            sender.sendMessage("/vowel <error/lowercase/symbol/hover/hiragana/state>");
        } else if ("error".equals(args[0])) {
            if (Config.noError) {
                Config.noError = false;
                sender.sendMessage("エラー無視を§7無効§rにしました");
            } else {
                Config.noError = true;
                sender.sendMessage("エラー無視を§2有効§rにしました");
            }
        } else if ("lowercase".equals(args[0])) {
            if (Config.leaveLowercase) {
                Config.leaveLowercase = false;
                sender.sendMessage("小文字を残すを§7無効§rにしました");
            } else {
                Config.leaveLowercase = true;
                sender.sendMessage("小文字を残すを§2有効§rにしました");
            }
        } else if ("symbol".equals(args[0])) {
            if (Config.leaveSymbol) {
                Config.leaveSymbol = false;
                sender.sendMessage("記号を残すを§7無効§rにしました");
            } else {
                Config.leaveSymbol = true;
                sender.sendMessage("記号を残すを§2有効§rにしました");
            }
        } else if ("hover".equals(args[0])) {
            if (Config.showHover) {
                Config.showHover = false;
                sender.sendMessage("ホバー表示を§7無効§rにしました");
            } else {
                Config.showHover = true;
                sender.sendMessage("ホバー表示を§2有効§rにしました");
            }
        } else if ("hiragana".equals(args[0])) {
            if (Config.hiraganaOnly) {
                Config.hiraganaOnly = false;
                sender.sendMessage("平仮名のみを§7無効§rにしました");
            } else {
                Config.hiraganaOnly = true;
                sender.sendMessage("平仮名のみを§2有効§rにしました");
            }
        } else if ("state".equals(args[0])) {
            sender.sendMessage("現在の状態");
            sender.sendMessage("エラー無視 - " + (Config.noError ? "§2有効§r" : "§7無効§r"));
            sender.sendMessage("小文字を残す - " + (Config.leaveLowercase ? "§2有効§r" : "§7無効§r"));
            sender.sendMessage("記号を残す - " + (Config.leaveSymbol ? "§2有効§r" : "§7無効§r"));
            sender.sendMessage("ホバー表示 - " + (Config.showHover ? "§2有効§r" : "§7無効§r"));
            sender.sendMessage("平仮名のみ - " + (Config.hiraganaOnly ? "§2有効§r" : "§7無効§r"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (command.getName().equals("vowel")) {
            if (args.length == 1)
                return Arrays.asList("error", "lowercase", "symbol", "hover", "hiragana", "state");
        }
        return Collections.emptyList();
    }
}
