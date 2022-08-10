package me.miquiis.devlog;

import me.miquiis.devlog.common.data.ModLogManager;
import me.miquiis.devlog.server.network.ModNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Devlog.MOD_ID)
public class Devlog
{
    public static final String MOD_ID = "devlog";

    private ModLogManager modLogManager;
    private static Devlog instance;

    public Devlog() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.addListener(this::onAddResourceListener);
        MinecraftForge.EVENT_BUS.register(this);

        this.modLogManager = new ModLogManager();
    }

    private void onAddResourceListener(AddReloadListenerEvent event) {
        event.addListener(modLogManager);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        instance = this;
        ModNetwork.init();
    }

    public ModLogManager getModLogManager() {
        return modLogManager;
    }

    public static Devlog getInstance() {
        return instance;
    }
}
