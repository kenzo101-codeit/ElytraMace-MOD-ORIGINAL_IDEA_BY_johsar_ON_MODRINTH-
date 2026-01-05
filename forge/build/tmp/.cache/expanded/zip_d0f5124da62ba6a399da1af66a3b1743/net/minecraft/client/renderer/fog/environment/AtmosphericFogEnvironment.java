package net.minecraft.client.renderer.fog.environment;

import javax.annotation.Nullable;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AtmosphericFogEnvironment extends AirBasedFogEnvironment {
    private static final int MIN_RAIN_FOG_SKY_LIGHT = 8;
    private static final float RAIN_FOG_START_OFFSET = -160.0F;
    private static final float RAIN_FOG_END_OFFSET = -256.0F;
    private float rainFogMultiplier;

    @Override
    public void setupFog(FogData p_407178_, Entity p_406135_, BlockPos p_407130_, ClientLevel p_410621_, float p_410424_, DeltaTracker p_409623_) {
        Biome biome = p_410621_.getBiome(p_407130_).value();
        float f = p_409623_.getGameTimeDeltaTicks();
        boolean flag = biome.hasPrecipitation();
        float f1 = Mth.clamp((p_410621_.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(p_407130_) - 8.0F) / 7.0F, 0.0F, 1.0F);
        float f2 = p_410621_.getRainLevel(p_409623_.getGameTimeDeltaPartialTick(false)) * f1 * (flag ? 1.0F : 0.5F);
        this.rainFogMultiplier = this.rainFogMultiplier + (f2 - this.rainFogMultiplier) * f * 0.2F;
        p_407178_.environmentalStart = this.rainFogMultiplier * -160.0F;
        p_407178_.environmentalEnd = 1024.0F + -256.0F * this.rainFogMultiplier;
        p_407178_.skyEnd = p_410424_;
        p_407178_.cloudEnd = Minecraft.getInstance().options.cloudRange().get() * 16;
    }

    @Override
    public boolean isApplicable(@Nullable FogType p_407495_, Entity p_406898_) {
        return p_407495_ == FogType.ATMOSPHERIC;
    }
}