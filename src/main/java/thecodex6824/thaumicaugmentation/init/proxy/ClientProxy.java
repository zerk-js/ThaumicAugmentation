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

package thecodex6824.thaumicaugmentation.init.proxy;

import java.util.Random;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.common.lib.events.EssentiaHandler.EssentiaSourceFX;
import thaumcraft.common.lib.network.fx.PacketFXShield;
import thecodex6824.thaumicaugmentation.ThaumicAugmentation;
import thecodex6824.thaumicaugmentation.api.TABlocks;
import thecodex6824.thaumicaugmentation.api.TAItems;
import thecodex6824.thaumicaugmentation.api.ThaumicAugmentationAPI;
import thecodex6824.thaumicaugmentation.api.augment.AugmentAPI;
import thecodex6824.thaumicaugmentation.api.augment.CapabilityAugment;
import thecodex6824.thaumicaugmentation.api.augment.CapabilityAugmentableItem;
import thecodex6824.thaumicaugmentation.api.augment.IAugment;
import thecodex6824.thaumicaugmentation.api.augment.IAugmentableItem;
import thecodex6824.thaumicaugmentation.api.augment.builder.IElytraHarnessAugment;
import thecodex6824.thaumicaugmentation.api.augment.builder.caster.CasterAugmentBuilder;
import thecodex6824.thaumicaugmentation.api.augment.builder.caster.ICustomCasterAugment;
import thecodex6824.thaumicaugmentation.api.client.ImpetusRenderingManager;
import thecodex6824.thaumicaugmentation.api.config.TAConfigManager;
import thecodex6824.thaumicaugmentation.api.impetus.node.CapabilityImpetusNode;
import thecodex6824.thaumicaugmentation.api.impetus.node.IImpetusNode;
import thecodex6824.thaumicaugmentation.api.item.CapabilityBiomeSelector;
import thecodex6824.thaumicaugmentation.api.item.CapabilityMorphicTool;
import thecodex6824.thaumicaugmentation.api.item.IBiomeSelector;
import thecodex6824.thaumicaugmentation.api.item.IDyeableItem;
import thecodex6824.thaumicaugmentation.api.tile.IImpetusGate;
import thecodex6824.thaumicaugmentation.api.util.DimensionalBlockPos;
import thecodex6824.thaumicaugmentation.api.warded.storage.CapabilityWardStorage;
import thecodex6824.thaumicaugmentation.api.warded.storage.ClientWardStorageValue;
import thecodex6824.thaumicaugmentation.api.warded.storage.IWardStorage;
import thecodex6824.thaumicaugmentation.api.warded.storage.IWardStorageClient;
import thecodex6824.thaumicaugmentation.api.warded.storage.WardStorageClient;
import thecodex6824.thaumicaugmentation.api.warded.storage.WardStorageServer;
import thecodex6824.thaumicaugmentation.client.event.ClientEventHandler;
import thecodex6824.thaumicaugmentation.client.event.ClientLivingEquipmentChangeEvent;
import thecodex6824.thaumicaugmentation.client.event.RenderEventHandler;
import thecodex6824.thaumicaugmentation.client.fx.FXBlockWardFixed;
import thecodex6824.thaumicaugmentation.client.gui.GUIArcaneTerraformer;
import thecodex6824.thaumicaugmentation.client.gui.GUIAutocaster;
import thecodex6824.thaumicaugmentation.client.gui.GUIWardedChest;
import thecodex6824.thaumicaugmentation.client.model.BuiltInRendererModel;
import thecodex6824.thaumicaugmentation.client.model.CustomCasterAugmentModel;
import thecodex6824.thaumicaugmentation.client.model.MorphicToolModel;
import thecodex6824.thaumicaugmentation.client.model.ProviderModel;
import thecodex6824.thaumicaugmentation.client.model.TAModelLoader;
import thecodex6824.thaumicaugmentation.client.renderer.TARenderHelperClient;
import thecodex6824.thaumicaugmentation.client.renderer.entity.RenderAutocaster;
import thecodex6824.thaumicaugmentation.client.renderer.entity.RenderDimensionalFracture;
import thecodex6824.thaumicaugmentation.client.renderer.entity.RenderFocusShield;
import thecodex6824.thaumicaugmentation.client.renderer.layer.RenderLayerHarness;
import thecodex6824.thaumicaugmentation.client.renderer.texture.TATextures;
import thecodex6824.thaumicaugmentation.client.renderer.tile.ListeningAnimatedTESR;
import thecodex6824.thaumicaugmentation.client.renderer.tile.RenderImpetusMirror;
import thecodex6824.thaumicaugmentation.client.renderer.tile.RenderRiftJar;
import thecodex6824.thaumicaugmentation.client.renderer.tile.RenderRiftMonitor;
import thecodex6824.thaumicaugmentation.client.renderer.tile.RenderRiftMoverOutput;
import thecodex6824.thaumicaugmentation.client.renderer.tile.RenderStarfieldGlass;
import thecodex6824.thaumicaugmentation.client.renderer.tile.RenderVoidRechargePedestal;
import thecodex6824.thaumicaugmentation.client.shader.TAShaderManager;
import thecodex6824.thaumicaugmentation.client.shader.TAShaders;
import thecodex6824.thaumicaugmentation.client.sound.ClientSoundHandler;
import thecodex6824.thaumicaugmentation.common.container.ContainerArcaneTerraformer;
import thecodex6824.thaumicaugmentation.common.container.ContainerAutocaster;
import thecodex6824.thaumicaugmentation.common.entity.EntityAutocaster;
import thecodex6824.thaumicaugmentation.common.entity.EntityAutocasterEldritch;
import thecodex6824.thaumicaugmentation.common.entity.EntityDimensionalFracture;
import thecodex6824.thaumicaugmentation.common.entity.EntityFocusShield;
import thecodex6824.thaumicaugmentation.common.item.ItemCustomCasterEffectProvider;
import thecodex6824.thaumicaugmentation.common.item.ItemCustomCasterStrengthProvider;
import thecodex6824.thaumicaugmentation.common.item.ItemFractureLocator;
import thecodex6824.thaumicaugmentation.common.item.ItemKey;
import thecodex6824.thaumicaugmentation.common.network.PacketAugmentableItemSync;
import thecodex6824.thaumicaugmentation.common.network.PacketBaubleChange;
import thecodex6824.thaumicaugmentation.common.network.PacketBiomeUpdate;
import thecodex6824.thaumicaugmentation.common.network.PacketConfigSync;
import thecodex6824.thaumicaugmentation.common.network.PacketEntityCast;
import thecodex6824.thaumicaugmentation.common.network.PacketFractureLocatorUpdate;
import thecodex6824.thaumicaugmentation.common.network.PacketFullImpetusNodeSync;
import thecodex6824.thaumicaugmentation.common.network.PacketFullWardSync;
import thecodex6824.thaumicaugmentation.common.network.PacketImpetusNodeUpdate;
import thecodex6824.thaumicaugmentation.common.network.PacketImpetusTransaction;
import thecodex6824.thaumicaugmentation.common.network.PacketImpulseBeam;
import thecodex6824.thaumicaugmentation.common.network.PacketImpulseBurst;
import thecodex6824.thaumicaugmentation.common.network.PacketImpulseRailgunProjectile;
import thecodex6824.thaumicaugmentation.common.network.PacketLivingEquipmentChange;
import thecodex6824.thaumicaugmentation.common.network.PacketParticleEffect;
import thecodex6824.thaumicaugmentation.common.network.PacketRiftJarInstability;
import thecodex6824.thaumicaugmentation.common.network.PacketWardUpdate;
import thecodex6824.thaumicaugmentation.common.tile.TileArcaneTerraformer;
import thecodex6824.thaumicaugmentation.common.tile.TileImpetusDiffuser;
import thecodex6824.thaumicaugmentation.common.tile.TileImpetusDrainer;
import thecodex6824.thaumicaugmentation.common.tile.TileImpetusMatrix;
import thecodex6824.thaumicaugmentation.common.tile.TileImpetusMirror;
import thecodex6824.thaumicaugmentation.common.tile.TileRiftJar;
import thecodex6824.thaumicaugmentation.common.tile.TileRiftMonitor;
import thecodex6824.thaumicaugmentation.common.tile.TileRiftMoverOutput;
import thecodex6824.thaumicaugmentation.common.tile.TileStabilityFieldGenerator;
import thecodex6824.thaumicaugmentation.common.tile.TileStarfieldGlass;
import thecodex6824.thaumicaugmentation.common.tile.TileVisRegenerator;
import thecodex6824.thaumicaugmentation.common.tile.TileVoidRechargePedestal;
import thecodex6824.thaumicaugmentation.common.tile.TileWardedChest;
import thecodex6824.thaumicaugmentation.common.util.ITARenderHelper;
import thecodex6824.thaumicaugmentation.common.world.biome.BiomeUtil;
import thecodex6824.thaumicaugmentation.init.GUIHandler.TAInventory;

public class ClientProxy extends ServerProxy {

    @Override
    public IAnimationStateMachine loadASM(ResourceLocation loc, ImmutableMap<String, ITimeValue> params) {
        return ModelLoaderRegistry.loadASM(loc, params);
    }

    @Override
    public ITARenderHelper getRenderHelper() {
        if (renderHelper == null)
            renderHelper = new TARenderHelperClient();

        return renderHelper;
    }
    
    @Override
    public IWardStorage createWardStorageInstance(World world) {
        return world.isRemote ? new WardStorageClient() : new WardStorageServer();
    }
    
    @Override
    public void registerRenderableImpetusNode(IImpetusNode node) {
        ImpetusRenderingManager.registerRenderableNode(node);
    }
    
    @Override
    public boolean deregisterRenderableImpetusNode(IImpetusNode node) {
        return ImpetusRenderingManager.deregisterRenderableNode(node);
    }
    
    @Override
    public boolean isOpenToLAN() {
        return Minecraft.getMinecraft().getIntegratedServer() != null && Minecraft.getMinecraft().getIntegratedServer().getPublic();
    }
    
    @Override
    public boolean isSingleplayer() {
        return Minecraft.getMinecraft().isSingleplayer();
    }
    
    @Override
    public boolean isJumpDown() {
        return Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
    }
    
    @Override
    public boolean isPvPEnabled() {
        return Minecraft.getMinecraft().getIntegratedServer() != null && Minecraft.getMinecraft().getIntegratedServer().isPVPEnabled();
    }
    
    @Override
    public boolean isEntityClientPlayer(Entity e) {
        return e == Minecraft.getMinecraft().player;
    }
    
    @Override
    public Object getClientGUIElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (TAInventory.values()[ID]) {
            case WARDED_CHEST: return new GUIWardedChest(getServerGUIElement(ID, player, world, x, y, z), player.inventory);
            case ARCANE_TERRAFORMER: return new GUIArcaneTerraformer((ContainerArcaneTerraformer) getServerGUIElement(ID, player, world, x, y, z));
            case AUTOCASTER: return new GUIAutocaster((ContainerAutocaster) getServerGUIElement(ID, player, world, x, y, z));
            default: return null;
        }
    }
    
    @Override
    public Object getSealGUI(World world, EntityPlayer player, BlockPos pos, EnumFacing face, ISealEntity seal) {
        return new SealBaseGUI(player.inventory, world, seal);
    }
    
    @Override
    public void handlePacketServer(IMessage message, MessageContext context) {
        if (Minecraft.getMinecraft().getIntegratedServer() == null)
            ThaumicAugmentation.getLogger().warn("A packet was received on the wrong side: " + message.getClass().toString());
        else
            super.handlePacketServer(message, context);
    }
    
    @Override
    public void handlePacketClient(IMessage message, MessageContext context) {
        if (message instanceof PacketParticleEffect)
            handleParticlePacket((PacketParticleEffect) message, context);
        else if (message instanceof PacketConfigSync)
            handleConfigSyncPacket((PacketConfigSync) message, context);
        else if (message instanceof PacketAugmentableItemSync)
            handleAugmentableItemSyncPacket((PacketAugmentableItemSync) message, context);
        else if (message instanceof PacketFullWardSync)
            handleFullWardSyncPacket((PacketFullWardSync) message, context);
        else if (message instanceof PacketWardUpdate)
            handleWardUpdatePacket((PacketWardUpdate) message, context);
        else if (message instanceof PacketFractureLocatorUpdate)
            handleFractureLocatorUpdatePacket((PacketFractureLocatorUpdate) message, context);
        else if (message instanceof PacketEntityCast)
            handleEntityCastPacket((PacketEntityCast) message, context);
        else if (message instanceof PacketFullImpetusNodeSync)
            handleFullImpetusNodeSyncPacket((PacketFullImpetusNodeSync) message, context);
        else if (message instanceof PacketImpetusNodeUpdate)
            handleImpetusNodeUpdatePacket((PacketImpetusNodeUpdate) message, context);
        else if (message instanceof PacketImpetusTransaction)
            handleImpetusTransationPacket((PacketImpetusTransaction) message, context);
        else if (message instanceof PacketRiftJarInstability)
            handleRiftJarInstabilityPacket((PacketRiftJarInstability) message, context);
        else if (message instanceof PacketBiomeUpdate)
            handleBiomeUpdatePacket((PacketBiomeUpdate) message, context);
        else if (message instanceof PacketFXShield)
            handleFXShieldPacket((PacketFXShield) message, context);
        else if (message instanceof PacketImpulseBeam)
            handleImpulseBeamPacket((PacketImpulseBeam) message, context);
        else if (message instanceof PacketImpulseBurst)
            handleImpulseBurstPacket((PacketImpulseBurst) message, context);
        else if (message instanceof PacketImpulseRailgunProjectile)
            handleImpulseRailgunPacket((PacketImpulseRailgunProjectile) message, context);
        else if (message instanceof PacketLivingEquipmentChange)
            handleLivingEquipmentChangePacket((PacketLivingEquipmentChange) message, context);
        else if (message instanceof PacketBaubleChange)
            handleBaubleChangePacket((PacketBaubleChange) message, context);
        else
            ThaumicAugmentation.getLogger().warn("An unknown packet was received and will be dropped: " + message.getClass().toString());
    }
    
    protected void handleParticlePacket(PacketParticleEffect message, MessageContext context) {
        if (FMLClientHandler.instance().getClient().world != null) {
            Random rand = FMLClientHandler.instance().getClient().world.rand;
            double d[] = message.getData();
            switch (message.getEffect()) {
                case VIS_REGENERATOR: {
                    if (d.length == 3) {
                        for (int i = 0; i < rand.nextInt(3) + 3; ++i) {
                            double x = d[0] + rand.nextGaussian() / 4, y = d[1] + rand.nextDouble() / 2, z = d[2] + rand.nextGaussian() / 4;
                            double vX = rand.nextGaussian() / 4, vY = rand.nextDouble() / 2, vZ = rand.nextGaussian() / 4;
                            FXDispatcher.INSTANCE.drawVentParticles(x, y, z, vX, vY, vZ, Aspect.AURA.getColor());
                        }
                    }
                    
                    break;
                }
                case VOID_STREAKS: {
                    if (d.length == 7) {
                        double x1 = d[0], y1 = d[1], z1 = d[2];
                        double x2 = d[3], y2 = d[4], z2 = d[5];
                        float scale = (float) d[6];
                        FXDispatcher.INSTANCE.voidStreak(x1, y1, z1, x2, y2, z2, rand.nextInt(), scale);
                    }
                    
                    break;
                }
                case WARD: {
                    if (d.length == 7) {
                        double x = d[0], y = d[1], z = d[2];
                        int index = (int) d[3];
                        double hitX = d[4], hitY = d[5], hitZ = d[6];
                        FXBlockWardFixed ward = new FXBlockWardFixed(FXDispatcher.INSTANCE.getWorld(), x + 0.5, y + 0.5, z + 0.5, 
                                EnumFacing.byIndex(index), (float) hitX, (float) hitY, (float) hitZ);
                        FMLClientHandler.instance().getClient().effectRenderer.addEffect(ward);
                    }
                    
                    break;
                }
                case POOF: {
                    if (d.length == 5) {
                        double x = d[0], y = d[1], z = d[2];
                        int color = (int) d[3], index = (int) d[4];
                        FXDispatcher.INSTANCE.drawBamf(new BlockPos(x, y, z), color, true, true,
                                EnumFacing.byIndex(index));
                    }
                    
                    break;
                }
                case SMOKE_SPIRAL: {
                    if (d.length == 7) {
                        double x = d[0], y = d[1], z = d[2];
                        float radius = (float) d[3];
                        int start = (int) d[4], minY = (int) d[5], color = (int) d[6];
                        FXDispatcher.INSTANCE.smokeSpiral(x, y, z, radius, start, minY, color);
                    }
                    
                    break;
                }
                case CURLY_WISP: {
                    if (d.length == 3) {
                        double x = d[0], y = d[1], z = d[2];
                        FXDispatcher.INSTANCE.drawCurlyWisp(x, y, z, 0.0, 0.0, 0.0, rand.nextFloat() + 0.1F, 1.0F, 1.0F, 
                                1.0F, 0.45F, null, 1, 0, 0);
                    }
                    
                    break;
                }
                case ESSENTIA_TRAIL: {
                    if (d.length == 7) {
                        int x1 = (int) d[0], y1 = (int) d[1], z1 = (int) d[2], x2 = (int) d[3], 
                                y2 = (int) d[4], z2 = (int) d[5], color = (int) d[6];
                        
                        // this seems kinda sketchy but it's what TC does...
                        String key = x2 + ":" + y2 + ":" + z2 + ":" + x1 + ":" + y1 + ":" + z1 + ":" + color;
                        if (!EssentiaHandler.sourceFX.containsKey(key)) {
                            EssentiaHandler.sourceFX.put(key, new EssentiaSourceFX(new BlockPos(x2, y2, z2),
                                    new BlockPos(x1, y1, z1), color, 15));
                        }
                    }
                    
                    break;
                }
                case EXPLOSION: {
                    if (d.length == 3) {
                        double x = d[0], y = d[1], z = d[2];
                        Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, false,
                                x, y, z, 0, 0, 0);
                    }
                    
                    break;
                }
                case SPARK: {
                    if (d.length == 5) {
                        double x = d[0], y = d[1], z = d[2];
                        float size = (float) d[3];
                        int color = (int) d[4];
                        getRenderHelper().renderSpark(Minecraft.getMinecraft().world, x, y, z, size, color, false);
                    }
                    
                    break;
                }
                case FIRE: {
                    if (d.length == 5) {
                        double x = d[0], y = d[1], z = d[2];
                        float size = (float) d[3];
                        int color = (int) d[4];
                        float r = ((color >> 16) & 0xFF) / 255.0F;
                        float g = ((color >> 8) & 0xFF) / 255.0F;
                        float b = (color & 0xFF) / 255.0F;
                        FXDispatcher.INSTANCE.drawFireMote((float) x, (float) y, (float) z, 0, 0, 0, r, g, b, 0.75F, size);
                    }
                    
                    break;
                }
                case FIRE_EXPLOSION: {
                    if (d.length == 5) {
                        double x = d[0], y = d[1], z = d[2];
                        float size = (float) d[3];
                        int color = (int) d[4];
                        float r = ((color >> 16) & 0xFF) / 255.0F;
                        float g = ((color >> 8) & 0xFF) / 255.0F;
                        float b = (color & 0xFF) / 255.0F;
                        for (int i = 0; i < 16; ++i) {
                            FXDispatcher.INSTANCE.drawFireMote((float) x, (float) y, (float) z, (rand.nextFloat() - rand.nextFloat()) / 10.0F,
                                    (rand.nextFloat() - rand.nextFloat()) / 10.0F, (rand.nextFloat() - rand.nextFloat()) / 10.0F, r, g, b, 0.75F, size);
                        }
                    }
                    
                    break;
                }
                case GENERIC_SPHERE: {
                    if (d.length == 5) {
                        double x = d[0], y = d[1], z = d[2];
                        float size = (float) d[3];
                        int color = (int) d[4];
                        float r = ((color >> 16) & 0xFF) / 255.0F;
                        float g = ((color >> 8) & 0xFF) / 255.0F;
                        float b = (color & 0xFF) / 255.0F;
                        FXGeneric fx = new FXGeneric(Minecraft.getMinecraft().world, x, y, z, 0, 0, 0);
                        fx.setRBGColorF(r, g, b);
                        fx.setAlphaF(0.9F, 0.0F);
                        fx.setGridSize(64);
                        fx.setParticles(264, 8, 1);
                        fx.setScale(size);
                        fx.setLayer(1);
                        fx.setLoop(true);
                        fx.setRotationSpeed(rand.nextFloat(), rand.nextBoolean() ? 1.0F : -1.0F);
                        ParticleEngine.addEffect(Minecraft.getMinecraft().world, fx);
                    }
                    
                    break;
                }
                case SPLASH_BATCH: {
                    if (d.length % 3 == 0) {
                        for (int i = 0; i < d.length; i += 3) {
                            double x = d[i], y = d[i + 1], z = d[i + 2];
                            World world = Minecraft.getMinecraft().world;
                            world.spawnParticle(EnumParticleTypes.WATER_SPLASH, false, x, y, z, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.5,
                                    (world.rand.nextFloat() - world.rand.nextFloat()) * 0.5, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.5);
                        }
                    }
                    
                    break;
                }
                case SMOKE_LARGE: {
                    if (d.length == 3) {
                        double x = d[0], y = d[1], z = d[2];
                        Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, false,
                                x, y, z, 0, 0.05, 0);
                    }
                    
                    break;
                }
                case FIRE_MULTIPLE_RAND: {
                    if (d.length == 5) {
                        double x = d[0], y = d[1], z = d[2];
                        float size = (float) d[3];
                        int color = (int) d[4];
                        float r = ((color >> 16) & 0xFF) / 255.0F;
                        float g = ((color >> 8) & 0xFF) / 255.0F;
                        float b = (color & 0xFF) / 255.0F;
                        for (int i = 0; i < rand.nextInt(4) + 3; ++i) {
                            FXDispatcher.INSTANCE.drawFireMote((float) x + (rand.nextFloat() - rand.nextFloat()),
                                    (float) y + (rand.nextFloat() - rand.nextFloat()), (float) z + (rand.nextFloat() - rand.nextFloat()),
                                    0, 0, 0, r, g, b, 0.75F, size);
                        }
                    }
                    
                    break;
                }
             
                default: {break;}
            }
        }
    }
    
    protected void handleConfigSyncPacket(PacketConfigSync message, MessageContext context) {
        TAConfigManager.sync(context.side, message.getBuffer());
    }
    
    protected void handleAugmentableItemSyncPacket(PacketAugmentableItemSync message, MessageContext context) {
        Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.getEntityID());
        if (entity != null) {
            int i = 0;
            for (Function<Entity, Iterable<ItemStack>> func : AugmentAPI.getAugmentableItemSources()) {
                for (ItemStack stack : func.apply(entity)) {
                    if (i == message.getItemIndex()) {
                        if (stack.hasCapability(CapabilityAugmentableItem.AUGMENTABLE_ITEM, null)) {
                            stack.getCapability(CapabilityAugmentableItem.AUGMENTABLE_ITEM, null).readSyncNBT(message.getTagCompound());
                            return;
                        }
                    }
                    
                    ++i;
                }
            }
        }
    }
    
    protected void handleFullWardSyncPacket(PacketFullWardSync message, MessageContext context) {
        NBTTagCompound tag = message.getTag();
        World world = Minecraft.getMinecraft().world;
        int chunkX = tag.getInteger("x"), chunkZ = tag.getInteger("z");
        if (world.isBlockLoaded(new BlockPos(chunkX << 4, 0, chunkZ << 4)) && world.getChunk(chunkX, chunkZ).hasCapability(CapabilityWardStorage.WARD_STORAGE, null)) {
            if (world.getChunk(chunkX, chunkZ).getCapability(CapabilityWardStorage.WARD_STORAGE, null) instanceof IWardStorageClient) {
                IWardStorageClient storage = (IWardStorageClient) world.getChunk(chunkX, chunkZ).getCapability(CapabilityWardStorage.WARD_STORAGE, null);
                storage.deserializeNBT(tag);
            }
        }
    }
    
    protected void handleWardUpdatePacket(PacketWardUpdate message, MessageContext context) {
        BlockPos pos = new BlockPos(message.getX(), message.getY(), message.getZ());
        World world = Minecraft.getMinecraft().world;
        if (world.isBlockLoaded(pos) && world.getChunk(pos).hasCapability(CapabilityWardStorage.WARD_STORAGE, null)) {
            if (world.getChunk(pos).getCapability(CapabilityWardStorage.WARD_STORAGE, null) instanceof IWardStorageClient) {
                IWardStorageClient storage = (IWardStorageClient) world.getChunk(pos).getCapability(CapabilityWardStorage.WARD_STORAGE, null);
                storage.setWard(pos, ClientWardStorageValue.fromID(message.getStatus()));
            }
        }
    }
    
    protected void handleFractureLocatorUpdatePacket(PacketFractureLocatorUpdate message, MessageContext context) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof ItemFractureLocator) {
                if (!stack.hasTagCompound())
                    stack.setTagCompound(new NBTTagCompound());
                
                stack.getTagCompound().setBoolean("found", message.wasFractureFound());
                if (message.wasFractureFound())
                    stack.getTagCompound().setIntArray("pos", new int[] {message.getX(), message.getY(), message.getZ()});
            }
        }
    }
    
    protected void handleEntityCastPacket(PacketEntityCast message, MessageContext context) {
        RenderEventHandler.onEntityCast(message.getEntityID());
    }
    
    protected void handleFullImpetusNodeSyncPacket(PacketFullImpetusNodeSync message, MessageContext context) {
        NBTTagCompound tag = message.getTag();
        BlockPos pos = message.getNode();
        World world = Minecraft.getMinecraft().world;
        if (world.isBlockLoaded(pos)) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null) {
                IImpetusNode node = tile.getCapability(CapabilityImpetusNode.IMPETUS_NODE, null);
                if (node != null)
                    node.readSyncNBT(tag);
            }
        }
    }
    
    protected void handleImpetusNodeUpdatePacket(PacketImpetusNodeUpdate message, MessageContext context) {
        World world = Minecraft.getMinecraft().world;
        if (world.isBlockLoaded(message.getNode())) {
            TileEntity tile = world.getTileEntity(message.getNode());
            if (tile != null) {
                IImpetusNode node = tile.getCapability(CapabilityImpetusNode.IMPETUS_NODE, null);
                if (node != null) {
                    DimensionalBlockPos dest = message.getDest();
                    if (dest.getDimension() == world.provider.getDimension() && world.isBlockLoaded(dest.getPos())) {
                        TileEntity destTile = world.getTileEntity(dest.getPos());
                        if (destTile != null) {
                            IImpetusNode destNode = destTile.getCapability(CapabilityImpetusNode.IMPETUS_NODE, null);
                            if (destNode != null) {
                                if (message.isOutput()) {
                                    if (message.shouldRemove())
                                        node.removeOutput(destNode);
                                    else
                                        node.addOutput(destNode);
                                }
                                else {
                                    if (message.shouldRemove())
                                        node.removeInput(destNode);
                                    else
                                        node.addInput(destNode);
                                }
                                
                                return;
                            }
                        }
                    }
                    
                    if (message.isOutput()) {
                        if (message.shouldRemove())
                            node.removeOutputLocation(message.getDest());
                        else
                            node.addOutputLocation(message.getDest());
                    }
                    else {
                        if (message.shouldRemove())
                            node.removeInputLocation(message.getDest());
                        else
                            node.addInputLocation(message.getDest());
                    }
                }
            }
        }
    }
    
    protected void handleImpetusTransationPacket(PacketImpetusTransaction message, MessageContext context) {
        RenderEventHandler.onImpetusTransaction(message.getPositions());
    }
    
    protected void handleRiftJarInstabilityPacket(PacketRiftJarInstability message, MessageContext context) {
        World world = Minecraft.getMinecraft().world;
        if (world.isBlockLoaded(message.getPosition())) {
            TileEntity tile = world.getTileEntity(message.getPosition());
            if (tile instanceof TileRiftJar)
                ((TileRiftJar) tile).setRiftStability(message.getStability());
        }
    }
    
    protected void handleBiomeUpdatePacket(PacketBiomeUpdate message, MessageContext context) {
        BiomeUtil.setBiome(Minecraft.getMinecraft().world, new BlockPos(message.getX(), 64, message.getZ()), Biome.getBiome(message.getBiome()));
    }
    
    protected void handleFXShieldPacket(PacketFXShield message, MessageContext context) {
        message.onMessage(message, context);
    }
    
    protected void handleImpulseBeamPacket(PacketImpulseBeam message, MessageContext context) {
        Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.getEntityID());
        if (entity instanceof EntityLivingBase)
            RenderEventHandler.onImpulseBeam((EntityLivingBase) entity, message.shouldStopBeam());
    }
    
    protected void handleImpulseBurstPacket(PacketImpulseBurst message, MessageContext context) {
        World world = Minecraft.getMinecraft().world;
        Entity entity = world.getEntityByID(message.getEntityID());
        if (entity instanceof EntityLivingBase) {
            Vec3d p = getRenderHelper().estimateImpulseCannonFiringPoint((EntityLivingBase) entity,
                    Minecraft.getMinecraft().getRenderPartialTicks());
            Vec3d v = message.getVelocity();
            for (int i = 0; i < 3; ++i) {
                FXGeneric fx = new FXGeneric(world, p.x, p.y, p.z, v.x, v.y, v.z);
                fx.setMaxAge(10);
                fx.setRBGColorF(0.35F, 0.35F, 0.65F);
                fx.setAlphaF(0.85F);
                fx.setGridSize(64);
                fx.setParticles(264, 8, 1);
                fx.setScale(1.0F);
                fx.setLayer(1);
                fx.setLoop(true);
                fx.setNoClip(false);
                fx.setRotationSpeed(world.rand.nextFloat(), world.rand.nextBoolean() ? 1.0F : -1.0F);
                ParticleEngine.addEffect(world, fx);
                p = p.add(v);
            }
        }
    }
    
    protected void handleImpulseRailgunPacket(PacketImpulseRailgunProjectile message, MessageContext context) {
        World world = Minecraft.getMinecraft().world;
        Entity entity = world.getEntityByID(message.getEntityID());
        if (entity instanceof EntityLivingBase) {
            Vec3d p = getRenderHelper().estimateImpulseCannonFiringPoint((EntityLivingBase) entity,
                    Minecraft.getMinecraft().getRenderPartialTicks());
            Vec3d v = message.getVelocity();
            FXGeneric fx = new FXGeneric(world, p.x, p.y, p.z, v.x, v.y, v.z);
            fx.setMaxAge(10);
            fx.setRBGColorF(0.35F, 0.35F, 0.65F);
            fx.setAlphaF(0.85F);
            fx.setGridSize(64);
            fx.setParticles(264, 8, 1);
            fx.setScale(2.5F);
            fx.setLayer(1);
            fx.setLoop(true);
            fx.setNoClip(false);
            fx.setRotationSpeed(world.rand.nextFloat(), world.rand.nextBoolean() ? 1.0F : -1.0F);
            ParticleEngine.addEffect(world, fx);
        }
    }
    
    protected void handleLivingEquipmentChangePacket(PacketLivingEquipmentChange message, MessageContext context) {
        Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.getEntityID());
        if (entity instanceof EntityLivingBase) {
            ClientEventHandler.onClientEquipmentChange(new ClientLivingEquipmentChangeEvent((EntityLivingBase) entity,
                    message.getSlot(), message.getStack()));
        }
    }
    
    protected void handleBaubleChangePacket(PacketBaubleChange message, MessageContext context) {
        Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.getEntityID());
        if (entity instanceof EntityLivingBase) {
            // this is my internal code so faking args like this is fine (they are unused atm anyway)
            ClientEventHandler.onClientEquipmentChange(new ClientLivingEquipmentChangeEvent((EntityLivingBase) entity,
                    EntityEquipmentSlot.HEAD, ItemStack.EMPTY));
        }
    }

    @Override
    public void preInit() {
        super.preInit();
        RenderingRegistry.registerEntityRenderingHandler(EntityDimensionalFracture.class, new IRenderFactory<EntityDimensionalFracture>() {
            @Override
            public Render<EntityDimensionalFracture> createRenderFor(RenderManager manager) {
                return new RenderDimensionalFracture(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityFocusShield.class, new IRenderFactory<EntityFocusShield>() {
            @Override
            public Render<EntityFocusShield> createRenderFor(RenderManager manager) {
                return new RenderFocusShield(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityAutocaster.class, new IRenderFactory<EntityAutocaster>() {
            @Override
            public Render<? super EntityAutocaster> createRenderFor(RenderManager manager) {
                return new RenderAutocaster<>(manager, false);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityAutocasterEldritch.class, new IRenderFactory<EntityAutocasterEldritch>() {
            @Override
            public Render<? super EntityAutocasterEldritch> createRenderFor(RenderManager manager) {
                return new RenderAutocaster<>(manager, true);
            }
        });
        
        TAModelLoader loader = new TAModelLoader();
        loader.registerLoader(new ProviderModel.Loader(new ResourceLocation("ta_special", "models/item/strength_provider"),
                () -> CasterAugmentBuilder.getAllStrengthProviders(), stack -> ItemCustomCasterStrengthProvider.getProviderID(stack)));
        loader.registerLoader(new ProviderModel.Loader(new ResourceLocation("ta_special", "models/item/effect_provider"),
                () -> CasterAugmentBuilder.getAllEffectProviders(), stack -> ItemCustomCasterEffectProvider.getProviderID(stack)));
        loader.registerLoader(new CustomCasterAugmentModel.Loader());
        loader.registerLoader(new MorphicToolModel.Loader());
        loader.registerLoader(new BuiltInRendererModel.Loader());
        ModelLoaderRegistry.registerLoader(loader);
    }

    @Override
    public void init() {
        super.init();
        ClientSoundHandler.init();
        ClientRegistry.bindTileEntitySpecialRenderer(TileVisRegenerator.class, new ListeningAnimatedTESR<>());
        ClientRegistry.bindTileEntitySpecialRenderer(TileWardedChest.class, new ListeningAnimatedTESR<>());
        ClientRegistry.bindTileEntitySpecialRenderer(TileImpetusDrainer.class, new ListeningAnimatedTESR<>());
        ClientRegistry.bindTileEntitySpecialRenderer(TileImpetusDiffuser.class, new ListeningAnimatedTESR<>());
        ClientRegistry.bindTileEntitySpecialRenderer(TileImpetusMatrix.class, new ListeningAnimatedTESR<>());
        ClientRegistry.bindTileEntitySpecialRenderer(TileRiftJar.class, new RenderRiftJar());
        ClientRegistry.bindTileEntitySpecialRenderer(TileRiftMoverOutput.class, new RenderRiftMoverOutput());
        ClientRegistry.bindTileEntitySpecialRenderer(TileVoidRechargePedestal.class, new RenderVoidRechargePedestal());
        ClientRegistry.bindTileEntitySpecialRenderer(TileImpetusMirror.class, new RenderImpetusMirror());
        ClientRegistry.bindTileEntitySpecialRenderer(TileRiftMonitor.class, new RenderRiftMonitor());
        ClientRegistry.bindTileEntitySpecialRenderer(TileStabilityFieldGenerator.class, new ListeningAnimatedTESR<>());
        ClientRegistry.bindTileEntitySpecialRenderer(TileStarfieldGlass.class, new RenderStarfieldGlass());
        registerItemColorHandlers();
        registerBlockColorHandlers();
        for (RenderPlayer render : Minecraft.getMinecraft().getRenderManager().getSkinMap().values())
            render.addLayer(new RenderLayerHarness(render));
    }

    @Override
    public void postInit() {
        super.postInit();
        TATextures.setupTextures();
        if (TAShaderManager.shouldUseShaders()) {
            TAShaders.FRACTURE = TAShaderManager.registerShader(new ResourceLocation(ThaumicAugmentationAPI.MODID, "fracture"));
            TAShaders.EMPTINESS_SKY = TAShaderManager.registerShader(new ResourceLocation(ThaumicAugmentationAPI.MODID, "emptiness_sky"));
            TAShaders.FLUX_RIFT = TAShaderManager.registerShader(new ResourceLocation(ThaumicAugmentationAPI.MODID, "ender"));
            TAShaders.MIRROR = TAShaderManager.registerShader(new ResourceLocation(ThaumicAugmentationAPI.MODID, "mirror"));
        }
    }

    private static void registerItemColorHandlers() {
        ItemColors registerTo = Minecraft.getMinecraft().getItemColors();
        registerTo.registerItemColorHandler(new IItemColor() {
            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                if (tintIndex == 1 && stack.getItem() instanceof ICaster && ((ICaster) stack.getItem()).getFocus(stack) != null)
                    return ((ItemFocus) ((ICaster) stack.getItem()).getFocus(stack)).getFocusColor(((ICaster) stack.getItem()).getFocusStack(stack));
                else if (tintIndex == 2 && stack.getItem() instanceof IDyeableItem)
                    return ((IDyeableItem) stack.getItem()).getDyedColor(stack);

                return -1;
            }
        }, TAItems.GAUNTLET);

        registerTo.registerItemColorHandler(new IItemColor() {
            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                if (tintIndex == 1 && stack.getItem() instanceof ItemKey)
                    return ((ItemKey) stack.getItem()).getKeyColor(stack);

                return -1;
            }
        }, TAItems.KEY);

        registerTo.registerItemColorHandler(new IItemColor() {
            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                if (tintIndex == 1 && stack.getItem() instanceof IDyeableItem)
                    return ((IDyeableItem) stack.getItem()).getDyedColor(stack);

                return -1;
            }
        }, TAItems.VOID_BOOTS);
        
        registerTo.registerItemColorHandler(new IItemColor() {
            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                if (tintIndex == 1 && stack.getCapability(CapabilityAugment.AUGMENT, null) instanceof ICustomCasterAugment) {
                    ICustomCasterAugment augment = (ICustomCasterAugment) stack.getCapability(CapabilityAugment.AUGMENT, null);
                    return CasterAugmentBuilder.getStrengthProvider(CasterAugmentBuilder.getStrengthProviderID(augment.getStrengthProvider())).calculateTintColor(augment);
                }
                return -1;
            }
        }, TAItems.AUGMENT_CUSTOM);
        
        registerTo.registerItemColorHandler(new IItemColor() {
            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                if (tintIndex == 1 && stack.getItem() instanceof ItemFractureLocator)
                    return ((ItemFractureLocator) stack.getItem()).getTintColor(stack);
                
                return -1;
            }
        }, TAItems.FRACTURE_LOCATOR);
        
        registerTo.registerItemColorHandler(new IItemColor() {
            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                ItemStack display = stack.getCapability(CapabilityMorphicTool.MORPHIC_TOOL, null).getDisplayStack();
                if (!display.isEmpty())
                    return Minecraft.getMinecraft().getItemColors().colorMultiplier(display, tintIndex);
                else
                    return -1;
            }
        }, TAItems.MORPHIC_TOOL);
        
        registerTo.registerItemColorHandler(new IItemColor() {
            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                IBiomeSelector selected = stack.getCapability(CapabilityBiomeSelector.BIOME_SELECTOR, null);
                if (tintIndex == 1 && selected != null) {
                    Biome biome = Biome.REGISTRY.getObject(selected.getBiomeID());
                    if (biome != null)
                        return biome.getGrassColorAtPos(Minecraft.getMinecraft().player.getPosition());
                    else if (selected.getBiomeID().equals(IBiomeSelector.RESET))
                        return 0xFF1493;
                }
                
                return -1;
            }
        }, TAItems.BIOME_SELECTOR);
        
        registerTo.registerItemColorHandler(new IItemColor() {
            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                if (tintIndex == 0 && stack.getItem() instanceof ItemFocus)
                    return ((ItemFocus) stack.getItem()).getFocusColor(stack);
                
                return -1;
            }
        }, TAItems.FOCUS_ANCIENT);
        
        registerTo.registerItemColorHandler(new IItemColor() {
            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                if (tintIndex == 0) {
                    IAugmentableItem item = stack.getCapability(CapabilityAugmentableItem.AUGMENTABLE_ITEM, null);
                    if (item != null) {
                        for (ItemStack augment : item.getAllAugments()) {
                            IAugment a = augment.getCapability(CapabilityAugment.AUGMENT, null);
                            if (a instanceof IElytraHarnessAugment && ((IElytraHarnessAugment) a).isCosmetic())
                                return ((IElytraHarnessAugment) a).getCosmeticItemTint();
                        }
                    }
                }
                
                return -1;
            }
        }, TAItems.ELYTRA_HARNESS);
        
        registerTo.registerItemColorHandler(new IItemColor() {
            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                if (tintIndex == 1)
                    return 0x990099;
                
                return -1;
            }
        }, TABlocks.IMPETUS_GATE);
    }
    
    private static void registerBlockColorHandlers() {
        BlockColors registerTo = Minecraft.getMinecraft().getBlockColors();
        IBlockColor terraformer = new IBlockColor() {
            @Override
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos,
                    int tintIndex) {
                
                if (tintIndex == 0 && world != null && pos != null) {
                    TileEntity tile = world.getTileEntity(pos);
                    if (tile instanceof TileArcaneTerraformer) {
                        IItemHandler inv = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                        if (inv != null) {
                            ItemStack stack = inv.getStackInSlot(0);
                            IBiomeSelector item = stack.getCapability(CapabilityBiomeSelector.BIOME_SELECTOR, null);
                            if (item != null) {
                                if (item.getBiomeID().equals(IBiomeSelector.EMPTY) || item.getBiomeID().equals(IBiomeSelector.RESET))
                                    return -1;
                                else {
                                    Biome biome = Biome.REGISTRY.getObject(item.getBiomeID());
                                    if (biome != null)
                                        return biome.getGrassColorAtPos(pos);
                                }
                            }
                        }
                    }
                }
                
                return -1;
            }
        };
        registerTo.registerBlockColorHandler(terraformer, TABlocks.ARCANE_TERRAFORMER);
        
        IBlockColor gate = new IBlockColor() {
            @Override
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos,
                    int tintIndex) {
                
                if (tintIndex == 1 && world != null && pos != null) {
                    TileEntity tile = world.getTileEntity(pos);
                    if (tile instanceof IImpetusGate) {
                        if (((IImpetusGate) tile).isInRedstoneMode()) {
                            if (world instanceof World && ((World) world).isBlockPowered(pos))
                                return 0xCC0000;
                            else
                                return 0x550000;
                        }
                        else {
                            int level = ((IImpetusGate) tile).getManualLimitLevel();
                            if (level > -1 && level < 16) {
                                int component = (int) (level / 15.0 * 153.0) & 0xFF;
                                return (component << 16) | component;
                            }
                        }
                    }
                }
                
                return -1;
            }
        };
        registerTo.registerBlockColorHandler(gate, TABlocks.IMPETUS_GATE);
    }

}
