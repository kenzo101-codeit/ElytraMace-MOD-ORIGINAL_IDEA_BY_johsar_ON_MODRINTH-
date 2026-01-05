package net.minecraft.client.renderer.fog.environment;

import javax.annotation.Nullable;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DimensionOrBossFogEnvironment extends AirBasedFogEnvironment {
    @Override
    public void setupFog(FogData p_410097_, Entity p_407749_, BlockPos p_408186_, ClientLevel p_410528_, float p_408804_, DeltaTracker p_406200_) {
        p_410097_.environmentalStart = p_408804_ * 0.05F;
        p_410097_.environmentalEnd = Math.min(p_408804_, 192.0F) * 0.5F;
        p_410097_.skyEnd = p_410097_.environmentalEnd;
        p_410097_.cloudEnd = p_410097_.environmentalEnd;
    }

    @Override
    public boolean isApplicable(@Nullable FogType p_407928_, Entity p_407982_) {
        return p_407928_ == FogType.DIMENSION_OR_BOSS;
    }
}