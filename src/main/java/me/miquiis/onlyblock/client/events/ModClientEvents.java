package me.miquiis.onlyblock.client.events;

import me.miquiis.onlyblock.OnlyBlock;
import me.miquiis.onlyblock.client.gui.MinazonScreen;
import me.miquiis.onlyblock.common.containers.MinazonContainer;
import me.miquiis.onlyblock.common.entities.renderer.*;
import me.miquiis.onlyblock.common.items.ModSpawnEgg;
import me.miquiis.onlyblock.common.registries.BlockRegister;
import me.miquiis.onlyblock.common.registries.ContainerRegister;
import me.miquiis.onlyblock.common.registries.EntityRegister;
import me.miquiis.onlyblock.common.registries.TileEntityRegister;
import me.miquiis.onlyblock.common.tileentities.renderer.MoneyPrinterTileRenderer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = OnlyBlock.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void doClientStuff(final FMLClientSetupEvent event)
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityRegister.CUSTOM_FALLING_BLOCK.get(), CustomFallingBlockRenderer::new);

        ClientRegistry.bindTileEntityRenderer(TileEntityRegister.MONEY_PRINTER_TILE_ENTITY.get(), MoneyPrinterTileRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(EntityRegister.XP_ZOMBIE.get(), XPZombieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegister.XP_CREEPER.get(), XPCreeperRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegister.XP_SKELETON.get(), XPSkeletonRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegister.XP_SPIDER.get(), XPSpiderRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegister.XP_ENDERMAN.get(), XPEndermanRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(EntityRegister.AIRDROP.get(), AirdropRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(EntityRegister.XP_CHICKEN.get(), XpChickenRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegister.XP_COW.get(), XpCowRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegister.XP_SHEEP.get(), XpSheepRenderer::new);

        RenderTypeLookup.setRenderLayer(BlockRegister.SERVER_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockRegister.TERMINAL_PANEL.get(), RenderType.getCutout());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticleFactory(ParticleFactoryRegisterEvent event)
    {
//        Minecraft.getInstance().particles.registerFactory(ParticleRegister.EXP_EXPLOSION.get(), ExpExplosionParticle.Factory::new);
    }

    @SubscribeEvent
    public static void onRegisterEntities(final RegistryEvent.Register<EntityType<?>> event)
    {
        ModSpawnEgg.initSpawnEggs();
    }

}
