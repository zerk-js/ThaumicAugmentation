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

package thecodex6824.thaumicaugmentation.common.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thecodex6824.thaumicaugmentation.api.PlayerMovementAbilityManager;
import thecodex6824.thaumicaugmentation.api.ThaumicAugmentationAPI;
import thecodex6824.thaumicaugmentation.api.item.IArmorReduceFallDamage;

@EventBusSubscriber(modid = ThaumicAugmentationAPI.MODID)
public class PlayerEventHandler {

	@SubscribeEvent
	public static void onJump(LivingEvent.LivingJumpEvent event) {
		if (event.getEntity() instanceof EntityPlayer)
			PlayerMovementAbilityManager.onJump((EntityPlayer) event.getEntity());
	}
	
	@SubscribeEvent
	public static void onTick(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntity() instanceof EntityPlayer)
			PlayerMovementAbilityManager.tick((EntityPlayer) event.getEntity());
	}
	
	@SubscribeEvent
	public static void onFallFirst(LivingAttackEvent event) {
		// damage can't be reduced to non-zero here, but cancelling it removes the screen shake and damage sound
		if (event.getSource() == DamageSource.FALL) {
			float damage = event.getAmount();
			for (ItemStack stack : event.getEntityLiving().getArmorInventoryList()) {
				if (stack.getItem() instanceof IArmorReduceFallDamage) {
					damage = ((IArmorReduceFallDamage) stack.getItem()).getNewFallDamage(stack, damage, event.getEntityLiving().fallDistance);
				}
			}
			
			damage = Math.max(0.0F, damage);
			if (damage < 1.0F) 
				event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void onFallDamage(LivingHurtEvent event) {
		// this is needed to actually reduce damage if it's not 0
		if (event.getSource() == DamageSource.FALL) {
			float damage = event.getAmount();
			for (ItemStack stack : event.getEntityLiving().getArmorInventoryList()) {
				if (stack.getItem() instanceof IArmorReduceFallDamage) {
					damage = ((IArmorReduceFallDamage) stack.getItem()).getNewFallDamage(stack, damage, event.getEntityLiving().fallDistance);
				}
			}
			
			damage = Math.max(0.0F, damage);
			if (damage < 1.0F) {
				event.setAmount(0.0F);
				event.setCanceled(true);
			}
			else
				event.setAmount(damage);
		}
	}
	
	@SubscribeEvent
	public static void onFallSound(PlaySoundAtEntityEvent event) {
		if (event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			if (player.getLastDamageSource() == DamageSource.FALL && event.getSound() == SoundEvents.ENTITY_PLAYER_BIG_FALL ||
					event.getSound() == SoundEvents.ENTITY_PLAYER_SMALL_FALL) {
				boolean shouldSilenceFall = false;
				for (ItemStack stack : ((EntityPlayer) event.getEntity()).getArmorInventoryList()) {
					if (stack.getItem() instanceof IArmorReduceFallDamage) {
						shouldSilenceFall = true;
						break;
					}
				}
				
				if (shouldSilenceFall)
					event.setCanceled(true);
			}
		}
	}
	
}