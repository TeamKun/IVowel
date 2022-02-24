package net.kunmc.lab.ivowel;

import net.kunmc.lab.ikisugilogger.IkisugiLogger;
import org.bukkit.plugin.java.JavaPlugin;

public final class IVowel extends JavaPlugin {

    @Override
    public void onEnable() {
        IkisugiLogger logger = new IkisugiLogger("IIUI VOWEL");
        logger.setColorType(IkisugiLogger.ColorType.RAINBOW);
        getLogger().info("\n" + logger.create());
    }

    @Override
    public void onDisable() {


    }
}
