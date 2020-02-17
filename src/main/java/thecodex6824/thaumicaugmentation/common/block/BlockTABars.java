/**
 *  Thaumic Augmentation
 *  Copyright (c) 2019 TheCodex6824.
 *
 *  This file is part of Thaumic Augmentation.
 *
 *  Thaumic Augmentation is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Thaumic Augmentation is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Thaumic Augmentation.  If not, see <https://www.gnu.org/licenses/>.
 */

package thecodex6824.thaumicaugmentation.common.block;

import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thecodex6824.thaumicaugmentation.api.TAItems;
import thecodex6824.thaumicaugmentation.api.ThaumicAugmentationAPI;
import thecodex6824.thaumicaugmentation.api.block.property.ITABarsType;
import thecodex6824.thaumicaugmentation.common.block.trait.IItemBlockProvider;
import thecodex6824.thaumicaugmentation.common.util.IModelProvider;

public class BlockTABars extends BlockPane implements ITABarsType, IModelProvider<Block>, IItemBlockProvider {

    public BlockTABars() {
        super(Material.IRON, true);
        setHardness(2.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 0);
        setDefaultState(getDefaultState().withProperty(ITABarsType.BARS_TYPE, BarsType.BARS_ANCIENT));
    }
    
    @Override
    public ItemBlock createItemBlock() {
        return new ItemMultiTexture(this, null, ITABarsType.BARS_TYPE.getAllowedValues().stream().map(
                ITABarsType.BARS_TYPE::getName).collect(Collectors.toList()).toArray(new String[ITABarsType.BARS_TYPE.getAllowedValues().size()]));
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NORTH, EAST, WEST, SOUTH, ITABarsType.BARS_TYPE);
    }
    
    @Override
    public Material getMaterial(IBlockState state) {
        return state.getValue(ITABarsType.BARS_TYPE).getMaterial();
    }
    
    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return state.getValue(ITABarsType.BARS_TYPE).getSoundType();
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ITABarsType.BARS_TYPE).getMeta();
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ITABarsType.BARS_TYPE, BarsType.fromMeta(meta));
    }
    
    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(ITABarsType.BARS_TYPE).getMeta();
    }
    
    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab == TAItems.CREATIVE_TAB || tab == CreativeTabs.SEARCH) {
            for (BarsType type : BarsType.values())
                items.add(new ItemStack(this, 1, type.getMeta()));
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels() {
        for (BarsType type : BarsType.values()) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(
                    ThaumicAugmentationAPI.MODID + ":" + type.getName(), "inventory"));
        }
    }
    
}
