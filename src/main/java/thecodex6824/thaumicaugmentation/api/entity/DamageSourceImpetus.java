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

package thecodex6824.thaumicaugmentation.api.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class DamageSourceImpetus extends DamageSource {

    protected Entity source;
    protected Vec3d pos;
    
    public DamageSourceImpetus(@Nullable Vec3d sourcePos) {
        super("impetus");
        pos = sourcePos;
    }
    
    public DamageSourceImpetus(Entity damageSource, @Nullable Vec3d sourcePos) {
        super("impetus");
        source = damageSource;
        pos = sourcePos;
    }
    
    @Override
    @Nullable
    public Entity getTrueSource() {
        return source;
    }
    
    @Override
    @Nullable
    public Vec3d getDamageLocation() {
        return pos;
    }
    
    @Override
    public ITextComponent getDeathMessage(EntityLivingBase entity) {
        if (source != null) {
            return new TextComponentTranslation("thaumicaugmentation.text.damage.impetus_attack",
                    entity != null ? entity.getDisplayName() : "Something", source.getDisplayName());
        }
        else {
            return new TextComponentTranslation("thaumicaugmentation.text.damage.impetus",
                    entity != null ? entity.getDisplayName() : "Something");
        }
    }
    
}
