package me.miquiis.devlog.common.registries;

import me.miquiis.devlog.Devlog;
import net.minecraft.potion.Effect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectRegister {

    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, Devlog.MOD_ID);

    public static void register(IEventBus bus)
    {
        EFFECTS.register(bus);
    }

}
