package net.kunmc.lab.ivowel;

import dev.felnull.fnjl.tuple.FNPair;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ServerHandler implements Listener {

    @EventHandler
    public void onAsyncChatEvent(AsyncChatEvent e) {
        Component cmp = e.originalMessage();
        if (cmp instanceof TextComponent) {
            String raw = ((TextComponent) cmp).content();
            try {
                FNPair<String, String> tx = JapaneseManager.getInstance().convertVowelOnly(raw, Config.noError, JapaneseManager.LeaveType.current());
                String hover = raw + "\n" +
                        "↓\n" +
                        tx.getRight() + "\n" +
                        "↓\n" +
                        tx.getLeft();
                Component txc = Component.text(tx.getLeft());
                if (Config.showHover)
                    txc = txc.hoverEvent(HoverEvent.showText(Component.text(hover)));
                e.message(txc);
            } catch (IllegalStateException ex) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Component.text(ex.getMessage()).color(TextColor.color(0xFF0000)));
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory instanceof AnvilInventory)) {
            return;
        }
        if (event.getRawSlot() != 2)
            return;

        ItemStack item = event.getCurrentItem();
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null)
            return;

        if (!(itemMeta.displayName() instanceof TextComponent))
            return;
        String renameText = ((TextComponent) itemMeta.displayName()).content();
        itemMeta.displayName(convert(renameText));
        item.setItemMeta(itemMeta);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        @NotNull List<Component> lines = e.lines();
        for (int i = 0; i < lines.size(); i++) {
            Component cmp = lines.get(i);
            if (cmp instanceof TextComponent) {
                Component retName;
                try {
                    FNPair<String, String> tx = JapaneseManager.getInstance().convertVowelOnly(((TextComponent) cmp).content(), Config.noError, JapaneseManager.LeaveType.current());
                    retName = Component.text(tx.getLeft());
                } catch (IllegalStateException ex) {
                    retName = Component.text("変換できない文字です").color(TextColor.color(0xFF0000));
                }
                e.line(i, retName);
            }
        }
    }

    @EventHandler
    public void onEditBook(PlayerEditBookEvent e) {
        BookMeta meta = e.getNewBookMeta().clone();
        if (meta.hasTitle() && meta.title() instanceof TextComponent)
            meta.setTitle(convertSt(meta.title()));
        List<Component> lst = meta.pages();
        for (int i = 0; i < lst.size(); i++) {
            Component pg = lst.get(i);
            meta.page(i + 1, convert(pg));
        }
        e.setNewBookMeta(meta);
    }

    private static String convertSt(Component text) {
        return ((TextComponent) convert(text)).content();
    }

    private static Component convert(Component text) {
        if (text instanceof TextComponent)
            return convert(((TextComponent) text).content());
        return text;
    }

    private static Component convert(String text) {
        Component retName;
        try {
            FNPair<String, String> tx = JapaneseManager.getInstance().convertVowelOnly(text, Config.noError, JapaneseManager.LeaveType.current());
            retName = Component.text(tx.getLeft());
        } catch (IllegalStateException ex) {
            retName = Component.text(ex.getMessage()).color(TextColor.color(0xFF0000));
        }
        return retName;
    }
}
