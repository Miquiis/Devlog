package me.miquiis.devlog.client.events;

import me.miquiis.devlog.Devlog;
import me.miquiis.devlog.client.gui.ModLogScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.io.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Devlog.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onInventoryOpen(GuiOpenEvent event)
    {
        if (event.getGui() instanceof InventoryScreen)
        {
            event.setCanceled(true);
            Minecraft.getInstance().displayGuiScreen(new ModLogScreen());
        }
    }

}
