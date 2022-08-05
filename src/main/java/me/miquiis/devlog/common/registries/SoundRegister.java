package me.miquiis.devlog.common.registries;

import me.miquiis.devlog.Devlog;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundRegister {

    public static final DeferredRegister<SoundEvent> SOUND_EVENT = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Devlog.MOD_ID);

    private static RegistryObject<SoundEvent> registerSoundEvent(String name)
    {
        return SOUND_EVENT.register(name, () -> new SoundEvent(new ResourceLocation(Devlog.MOD_ID, name)));
    }

    public static void register(IEventBus bus)
    {
        SOUND_EVENT.register(bus);
    }

}
