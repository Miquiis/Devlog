package me.miquiis.onlyblock.common.registries;

import me.miquiis.onlyblock.OnlyBlock;
import me.miquiis.onlyblock.common.blocks.*;
import me.miquiis.onlyblock.common.items.EnchantedBlockItem;
import me.miquiis.onlyblock.common.items.EnchantedGrowBlockItem;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.trees.OakTree;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class BlockRegister {

    public static final DeferredRegister<Block> BLOCKS
            = DeferredRegister.create(ForgeRegistries.BLOCKS, OnlyBlock.MOD_ID);

    public static final RegistryObject<Block> ENCHANTED_COBBLESTONE = registerEnchantedBlock("enchanted_cobblestone", () ->
            new Block(AbstractBlock.Properties.create(Material.ROCK).setRequiresTool().hardnessAndResistance(2.0F, 6.0F)),
            5, true
    );

    public static final RegistryObject<Block> ENCHANTED_DIRT = registerEnchantedBlock("enchanted_dirt", () ->
                    new Block(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.DIRT).hardnessAndResistance(0.5F).sound(SoundType.GROUND)),
            1, false
    );

    public static final RegistryObject<Block> ENCHANTED_OAK_PLANKS = registerEnchantedBlock("enchanted_oak_planks", () ->
            new Block(AbstractBlock.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)),
            2, false
    );

    public static final RegistryObject<Block> ENCHANTED_GRASS_PATH = registerEnchantedBlock("enchanted_grass_path", () ->
            new EnchantedGrassPathBlock(AbstractBlock.Properties.create(Material.EARTH).hardnessAndResistance(0.65F).sound(SoundType.PLANT).setBlocksVision(BlockRegister::needsPostProcessing).setSuffocates(BlockRegister::needsPostProcessing))
    );

    public static final RegistryObject<Block> XP_BLOCK = registerBlock("xp_block", () ->
            new XPBlock(AbstractBlock.Properties.create(Material.GLASS).hardnessAndResistance(2f).sound(SoundType.GLASS).setLightLevel((state) -> 15))
    );

    public static final RegistryObject<Block> ENERGY_XP_BLOCK = registerBlock("energy_xp_block", () ->
            new XPBlock(AbstractBlock.Properties.create(Material.GLASS).hardnessAndResistance(2f).sound(SoundType.GLASS).setLightLevel((state) -> 15))
    );


    public static final RegistryObject<Block> XP_MINER = registerBlock("xp_miner", () ->
            new XpMinerBlock(AbstractBlock.Properties.create(Material.ANVIL).hardnessAndResistance(3f).sound(SoundType.ANVIL).notSolid())
    );

    public static final RegistryObject<Block> ENCHANTED_CRAFTING_TABLE = registerEnchantedBlock("enchanted_crafting_table", () ->
            new CobblestoneCraftingTableBlock(AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD))
    );

    public static final RegistryObject<Block> XP_TNT = registerBlock("xp_tnt", () ->
            new XPTNTBlock(AbstractBlock.Properties.create(Material.TNT).zeroHardnessAndResistance().sound(SoundType.PLANT))
    );

    private static boolean needsPostProcessing(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    private static boolean isOpaque(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    private static<T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);

        registerBlockItem(name, toReturn);

        return toReturn;
    }

    private static<T extends Block> RegistryObject<T> registerEnchantedBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerEnchantedBlockItem(name, toReturn);
        return toReturn;
    }

    private static<T extends Block> RegistryObject<T> registerEnchantedBlock(String name, Supplier<T> block, int radius, boolean sphere) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerEnchantedBlockItem(name, toReturn, radius, sphere);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ItemRegister.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().group(ItemGroup.MISC))
        );
    }

    private static <T extends Block> void registerEnchantedBlockItem(String name, RegistryObject<T> block, int radius, boolean sphere) {
        ItemRegister.ITEMS.register(name, () -> new EnchantedGrowBlockItem(block.get(),
                new Item.Properties().group(ItemGroup.MISC), radius, sphere)
        );
    }

    private static <T extends Block> void registerEnchantedBlockItem(String name, RegistryObject<T> block) {
        ItemRegister.ITEMS.register(name, () -> new EnchantedBlockItem(block.get(),
                new Item.Properties().group(ItemGroup.MISC))
        );
    }

    public static void register(IEventBus bus)
    {
        BLOCKS.register(bus);
    }

}
