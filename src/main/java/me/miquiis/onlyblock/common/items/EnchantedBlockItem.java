package me.miquiis.onlyblock.common.items;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EnchantedBlockItem extends BlockItem {

    public EnchantedBlockItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
