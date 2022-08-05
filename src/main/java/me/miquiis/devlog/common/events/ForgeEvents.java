package me.miquiis.devlog.common.events;

import me.miquiis.devlog.Devlog;
import me.miquiis.devlog.server.commands.ModCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Devlog.MOD_ID)
public class ForgeEvents {

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event)
    {
        new ModCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

}
