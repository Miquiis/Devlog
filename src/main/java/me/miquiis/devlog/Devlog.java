package me.miquiis.devlog;

import me.miquiis.devlog.common.registries.*;
import me.miquiis.devlog.server.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Mod(Devlog.MOD_ID)
public class Devlog
{
    public static final String MOD_ID = "devlog";

    private static Devlog instance;

    public Devlog() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(this::processIMC);

        ParticleRegister.PARTICLES.register(modEventBus);
        EntityRegister.register(modEventBus);
        ContainerRegister.register(modEventBus);
        SoundRegister.register(modEventBus);
        TileEntityRegister.register(modEventBus);
        EffectRegister.register(modEventBus);
        ItemRegister.register(modEventBus);
        BlockRegister.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        instance = this;
        ModNetwork.init();
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {

    }

    private void processIMC(final InterModProcessEvent event)
    {
        try {
            Minecraft.getInstance().getResourceManager().getAllResources(new ResourceLocation("data/devlog", "loot_tables/default.txt")).forEach(iResource -> {
                InputStream inputStream = iResource.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                try {
                    System.out.println("Devlog Debug: " + bufferedReader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            //throw new RuntimeException(e);
        }

        try {
            Minecraft.getInstance().getResourceManager().getAllResources(new ResourceLocation("data:devlog", "loot_tables/default.txt")).forEach(iResource -> {
                InputStream inputStream = iResource.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                try {
                    System.out.println("Devlog Debug: " + bufferedReader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            //throw new RuntimeException(e);
        }

        Minecraft.getInstance().getResourceManager().getAllResourceLocations("loot_tables/default.txt", s -> true).forEach(resourceLocation -> {
            System.out.println("Devlog Debug: " + resourceLocation);
        });

        Minecraft.getInstance().getResourceManager().getResourceNamespaces().forEach(s -> {
            try {
                Minecraft.getInstance().getResourceManager().getAllResources(new ResourceLocation(s, "loot_tables/default.txt")).forEach(iResource -> {
                    if (iResource == null) return;
                    InputStream inputStream = iResource.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    try {
                        System.out.println("Devlog Debug: " + bufferedReader.readLine());
                    } catch (IOException e) {
                        //throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                //throw new RuntimeException(e);
            }
        });
    }

    public static Devlog getInstance() {
        return instance;
    }
}
