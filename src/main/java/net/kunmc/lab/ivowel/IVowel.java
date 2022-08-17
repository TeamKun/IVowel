package net.kunmc.lab.ivowel;

import net.kunmc.lab.ikisugilogger.IkisugiLogger;
import org.bukkit.plugin.java.JavaPlugin;

public final class IVowel extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ServerHandler(), this);

        IkisugiLogger logger = new IkisugiLogger("the ikisugi\nvowel");
        logger.setColorType(IkisugiLogger.ColorType.VOWEL_ONLY);
        logger.setCenter(true);
        getLogger().info(logger.createLn());

        IVCommands.init();
    }

    @Override
    public void onDisable() {


    }
}
