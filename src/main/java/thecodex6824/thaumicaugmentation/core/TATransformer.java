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

package thecodex6824.thaumicaugmentation.core;

import java.util.ArrayList;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import net.minecraft.launchwrapper.IClassTransformer;
import thecodex6824.thaumicaugmentation.core.transformer.ITransformer;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerBaubleSlotChanged;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerBipedRotationCustomTCArmor;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerBipedRotationVanilla;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerElytraClientCheck;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerElytraServerCheck;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerInfusionLeftoverItems;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerRenderEntities;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerTCRobesElytraFlapping;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerThaumostaticHarnessSprintCheck;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerUpdateElytra;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerWardBlockFireEncouragement;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerWardBlockFlammability;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerWardBlockHardness;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerWardBlockNoEndermanPickup;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerWardBlockNoRabbitSnacking;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerWardBlockNoSheepGrazing;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerWardBlockNoVillagerFarming;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerWardBlockRandomTick;
import thecodex6824.thaumicaugmentation.core.transformer.TransformerWardBlockResistance;

public class TATransformer implements IClassTransformer {

    private static final ArrayList<ITransformer> TRANSFORMERS = new ArrayList<>();
    
    static {
        // required as there is no generic hardness hook for calls outside of player breaking
        TRANSFORMERS.add(new TransformerWardBlockHardness());
        // required as there is no generic resistance hook for calls outside of player breaking and explosions
        TRANSFORMERS.add(new TransformerWardBlockResistance());
        // required to make fire not linger on warded blocks
        TRANSFORMERS.add(new TransformerWardBlockFlammability());
        // required to make fire not make warded blocks catch on fire
        TRANSFORMERS.add(new TransformerWardBlockFireEncouragement());
        // required to cancel random updates for warded blocks
        // scheduled and neighbor updates are handled in event handlers
        TRANSFORMERS.add(new TransformerWardBlockRandomTick());
        
        // required as EntityMobGriefingEvent does not provide a blockpos
        // the position is also not determined and put into the AI fields until the event has already passed, so no reflection
        TRANSFORMERS.add(new TransformerWardBlockNoEndermanPickup());
        // same
        TRANSFORMERS.add(new TransformerWardBlockNoRabbitSnacking());
        // same
        TRANSFORMERS.add(new TransformerWardBlockNoSheepGrazing());
        // same
        TRANSFORMERS.add(new TransformerWardBlockNoVillagerFarming());
        
        // required to cancel sprinting client side immediately
        // using events allows 1-tick sprints, and holding down sprint will allow constant sprinting
        TRANSFORMERS.add(new TransformerThaumostaticHarnessSprintCheck());
        
        // required to allow clients to recognize they are wearing elytra
        TRANSFORMERS.add(new TransformerElytraClientCheck());
        // required to allow the server to recognize players are wearing elytra
        TRANSFORMERS.add(new TransformerElytraServerCheck());
        // required to maintain the elytra flying flag for custom elytra
        TRANSFORMERS.add(new TransformerUpdateElytra());
        
        // required to have custom arm rotations for things like the impulse cannon
        TRANSFORMERS.add(new TransformerBipedRotationVanilla());
        // required to make any ModelBiped transformers work on TC armor at all
        TRANSFORMERS.add(new TransformerBipedRotationCustomTCArmor());
        // fixes annoying robe legging flapping (as if the player is walking) while elytra flying
        TRANSFORMERS.add(new TransformerTCRobesElytraFlapping());
        // required to hook render pass 0 for batch rendering of TEs with shaders
        // RenderWorldLastEvent almost works but it is after render pass 0, so it will draw over transparent blocks
        TRANSFORMERS.add(new TransformerRenderEntities());
        
        // required to fix morphic tool recipe dupe while also allowing container items to be used
        // backup non-coremod mitigation disables container items, which players expressed a desire to keep
        TRANSFORMERS.add(new TransformerInfusionLeftoverItems());
        
        // required to have augments on baubles detected properly without having to loop over them all every tick
        // performance with the looping method is terrible, and a change event should really have been added...
        TRANSFORMERS.add(new TransformerBaubleSlotChanged());
    }
    
    public TATransformer() {}
    
    private boolean isTransformNeeded(String transformedName) {
        if (!ThaumicAugmentationCore.isEnabled())
            return false;
        
        for (ITransformer t : TRANSFORMERS) {
            if (t.isTransformationNeeded(transformedName))
                return true;
        }
        
        return false;
    }
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (isTransformNeeded(transformedName)) {
            ClassNode node = new ClassNode();
            ClassReader reader = new ClassReader(basicClass);
            reader.accept(node, 0);
            
            boolean didSomething = false;
            boolean computeFrames = false;
            for (ITransformer transformer : TRANSFORMERS) {
                if (transformer.isTransformationNeeded(transformedName)) {
                    if (ThaumicAugmentationCore.getExcludedTransformers().contains(transformer.getClass().getName()))
                        ThaumicAugmentationCore.getLogger().info("Excluding transformer {} due to config request", transformer.getClass().getName());
                    else if (!transformer.transform(node, name, transformedName)) {
                        ThaumicAugmentationCore.getLogger().error("A class transformer has failed! This is probably very bad...");
                        ThaumicAugmentationCore.getLogger().error("Class: " + transformedName + ", Transformer: " + transformer.getClass());
                        if (transformer.getRaisedException() != null) {
                            ThaumicAugmentationCore.getLogger().error("Additional information: ", transformer.getRaisedException());
                            throw transformer.getRaisedException();
                        }
                        else
                            throw new RuntimeException();
                    }
                    else {
                        didSomething = true;
                        computeFrames |= transformer.needToComputeFrames();
                    }
                }
            }
            
            if (didSomething) {
                ClassWriter writer = !computeFrames ? new ClassWriter(ClassWriter.COMPUTE_MAXS) : new ClassWriter(ClassWriter.COMPUTE_FRAMES) {
                    @Override
                    protected String getCommonSuperClass(String type1, String type2) {
                        return "java/lang/Object";
                    }
                };
                node.accept(writer);
                ThaumicAugmentationCore.getLogger().info("Successfully transformed class " + transformedName);
                return writer.toByteArray();
            }
        }
        
        return basicClass;
    }
    
}
