package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Unit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardianParticleModel extends Model<Unit> {
    public GuardianParticleModel(ModelPart p_427037_) {
        super(p_427037_, RenderType::entityCutoutNoCull);
    }
}