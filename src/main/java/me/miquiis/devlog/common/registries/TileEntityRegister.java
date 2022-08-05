package me.miquiis.devlog.common.registries;

import me.miquiis.devlog.Devlog;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityRegister {

    public static DeferredRegister<TileEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Devlog.MOD_ID);

    public static void register(IEventBus eventBus) { TILE_ENTITIES.register(eventBus);}

}
