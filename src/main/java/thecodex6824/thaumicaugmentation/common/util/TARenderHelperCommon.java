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

package thecodex6824.thaumicaugmentation.common.util;

import net.minecraft.world.World;
import thecodex6824.thaumicaugmentation.api.util.FluxRiftReconstructor;

public class TARenderHelperCommon implements ITARenderHelper {

    @Override
    public void renderGlowingSphere(World world, double x, double y, double z, int color) {}
    
    @Override
    public void renderBurst(World world, double x, double y, double z, float size, int color) {}
    
    @Override
    public void renderSpark(World world, double x, double y, double z, float size, int color, boolean colorAlpha) {}
    
    @Override
    public void renderArc(World world, double x, double y, double z, double dx, double dy, double dz, int color, double height) {}
    
    @Override
    public void renderFluxRift(FluxRiftReconstructor rift, int stability, float partialTicks, int tessLevel,
            boolean ignoreGoggles) {}

}
