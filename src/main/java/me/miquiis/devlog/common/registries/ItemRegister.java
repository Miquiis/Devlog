package me.miquiis.devlog.common.registries;

import me.miquiis.devlog.Devlog;
import net.minecraft.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegister {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Devlog.MOD_ID);

    public static void register(IEventBus bus)
    {
        ITEMS.register(bus);
    }

}
