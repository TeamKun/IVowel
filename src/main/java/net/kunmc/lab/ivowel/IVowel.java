package net.kunmc.lab.ivowel;

import net.kunmc.lab.ikisugilogger.IkisugiLogger;
import org.bukkit.plugin.java.JavaPlugin;

public final class IVowel extends JavaPlugin {

    @Override
    public void onEnable() {
        JapaneseManager.getInstance().init();
        getServer().getPluginManager().registerEvents(new ServerHandler(), this);

        IkisugiLogger logger = new IkisugiLogger("the ikisugi\nvowel only");
        logger.setColorType(IkisugiLogger.ColorType.VOWEL_ONLY);
        logger.setCenter(true);
        getLogger().info(logger.createLn());
    }

    @Override
    public void onDisable() {


    }
}
