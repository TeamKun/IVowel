package net.kunmc.lab.ivowel;

import dev.felnull.fnjl.tuple.FNPair;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerHandler implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onAsyncChatEvent(AsyncChatEvent e) {
        Component cmp = e.originalMessage();
        if (cmp instanceof TextComponent) {
            String raw = ((TextComponent) cmp).content();
            try {
                FNPair<String, String> tx = JapaneseManager.getInstance().convertVowelOnly(raw, true, JapaneseManager.LeaveType.NOT_CNV, JapaneseManager.LeaveType.MIN);
                String hover = raw + "\n" +
                        "↓\n" +
                        tx.getRight() + "\n" +
                        "↓\n" +
                        tx.getLeft();
                e.message(Component.text(tx.getLeft()).hoverEvent(HoverEvent.showText(Component.text(hover))));
            } catch (IllegalStateException ex) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Component.text(ex.getMessage()).color(TextColor.color(0xFF0000)));
            }
        }
    }
}
