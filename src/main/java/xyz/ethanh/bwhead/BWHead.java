package xyz.ethanh.bwhead;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import xyz.ethanh.bwhead.commands.LoadStats;

import java.io.File;

@Mod(modid = BWHead.MODID, version = BWHead.VERSION)
public class BWHead {
    public static final String MODID = "bwhead";
    public static final String VERSION = "1.0.0";
    public static final String DEFAULT_KEY = "DEFAULT_KEY";

    public static File configFile;
    public static Configuration config;

    public static Property getAPIKeyProperty() {
        return config.get(Configuration.CATEGORY_CLIENT, "API_Key", DEFAULT_KEY, "Set your API key here or things won't work properly!");
    }

    public static String getAPIKey() {
        return getAPIKeyProperty().getString();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        configFile = event.getSuggestedConfigurationFile();
        config = new Configuration(configFile);

        config.load();
        getAPIKey();
        config.save();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new BWHeadRender());
        ClientCommandHandler.instance.registerCommand(new LoadStats());
    }

}
