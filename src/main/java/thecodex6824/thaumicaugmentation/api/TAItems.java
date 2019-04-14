/**
 *	Thaumic Augmentation
 *	Copyright (c) 2019 TheCodex6824.
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

package thecodex6824.thaumicaugmentation.api;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

/**
 * Holds all of Thaumic Augmentation's items and the creative tab. The fields will be populated
 * after preInit.
 * @author TheCodex6824
 * 
 */
@ObjectHolder(ThaumicAugmentationAPI.MODID)
public class TAItems {
	
	public static final Item GAUNTLET = null;
	public static final Item MATERIAL = null;
	public static final Item SEAL_COPIER = null;
	public static final Item ARCANE_DOOR = null;
	
	public static CreativeTabs CREATIVE_TAB = new CreativeTabs(ThaumicAugmentationAPI.MODID) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(GAUNTLET);
		}
	};
	
	public static Item[] getAllItems() {
		return new Item[] {GAUNTLET, MATERIAL, SEAL_COPIER, ARCANE_DOOR};
	}
	
}