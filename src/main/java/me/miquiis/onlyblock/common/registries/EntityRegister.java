package me.miquiis.onlyblock.common.registries;

import me.miquiis.onlyblock.OnlyBlock;
import me.miquiis.onlyblock.common.entities.*;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = OnlyBlock.MOD_ID)
public class EntityRegister {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, OnlyBlock.MOD_ID);

    public static final RegistryObject<EntityType<FakeExperienceOrbEntity>> FAKE_EXPERIENCE_ORB = ENTITIES.register("fake_experience_orb",
            () -> EntityType.Builder.<FakeExperienceOrbEntity>create(FakeExperienceOrbEntity::new, EntityClassification.MISC)
                    .size(0.5F, 0.5F).trackingRange(6).updateInterval(20)
                    .build(new ResourceLocation(OnlyBlock.MOD_ID, "fake_experience_orb").toString())
    );

    public static final RegistryObject<EntityType<DamageableExperienceOrbEntity>> DAMAGEABLE_EXPERIENCE_ORB = ENTITIES.register("damageable_experience_orb",
            () -> EntityType.Builder.<DamageableExperienceOrbEntity>create(DamageableExperienceOrbEntity::new, EntityClassification.MISC)
                    .size(0.5F, 0.5F).trackingRange(6).updateInterval(20)
                    .build(new ResourceLocation(OnlyBlock.MOD_ID, "damageable_experience_orb").toString())
    );

    public static final RegistryObject<EntityType<XpCowEntity>> XP_COW = ENTITIES.register("xp_cow",
            () -> EntityType.Builder.<XpCowEntity>create(XpCowEntity::new, EntityClassification.CREATURE)
                    .size(0.9F, 1.4F)
                    .trackingRange(10)
                    .build(new ResourceLocation(OnlyBlock.MOD_ID, "xp_cow").toString())
    );

    public static final RegistryObject<EntityType<XpSheepEntity>> XP_SHEEP = ENTITIES.register("xp_sheep",
            () -> EntityType.Builder.<XpSheepEntity>create(XpSheepEntity::new, EntityClassification.CREATURE)
                    .size(0.9F, 1.3F)
                    .trackingRange(10)
                    .build(new ResourceLocation(OnlyBlock.MOD_ID, "xp_sheep").toString())
    );

    public static final RegistryObject<EntityType<XpChickenEntity>> XP_CHICKEN = ENTITIES.register("xp_chicken",
            () -> EntityType.Builder.<XpChickenEntity>create(XpChickenEntity::new, EntityClassification.CREATURE)
                    .size(0.4F, 0.7F)
                    .trackingRange(10)
                    .build(new ResourceLocation(OnlyBlock.MOD_ID, "xp_chicken").toString())
    );

    public static void register(IEventBus bus)
    {
        ENTITIES.register(bus);
    }

    @SubscribeEvent
    public static void onEntityRegister(EntityAttributeCreationEvent event)
    {
        event.put(XP_CHICKEN.get(), XpChickenEntity.func_234187_eI_().create());
        event.put(XP_COW.get(), XpCowEntity.registerAttributes().create());
        event.put(XP_SHEEP.get(), XpSheepEntity.registerAttributes().create());
    }

}
