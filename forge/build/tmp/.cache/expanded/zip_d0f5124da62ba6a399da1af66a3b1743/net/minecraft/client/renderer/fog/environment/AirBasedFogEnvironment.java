package net.minecraft.client.renderer.fog.environment;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public abstract class AirBasedFogEnvironment extends FogEnvironment {
    @Override
    public int getBaseColor(ClientLevel p_408248_, Camera p_406520_, int p_408570_, float p_408253_) {
        float f = Mth.clamp(Mth.cos(p_408248_.getTimeOfDay(p_408253_) * (float) (Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);
        BiomeManager biomemanager = p_408248_.getBiomeManager();
        Vec3 vec3 = p_406520_.getPosition().subtract(2.0, 2.0, 2.0).scale(0.25);
        Vec3 vec31 = p_408248_.effects()
            .getBrightnessDependentFogColor(
                CubicSampler.gaussianSampleVec3(
                    vec3, (p_408867_, p_408720_, p_406163_) -> Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(p_408867_, p_408720_, p_406163_).value().getFogColor())
                ),
                f
            );
        float f1 = (float)vec31.x();
        float f2 = (float)vec31.y();
        float f3 = (float)vec31.z();
        if (p_408570_ >= 4) {
            float f4 = Mth.sin(p_408248_.getSunAngle(p_408253_)) > 0.0F ? -1.0F : 1.0F;
            Vector3f vector3f = new Vector3f(f4, 0.0F, 0.0F);
            float f5 = p_406520_.getLookVector().dot(vector3f);
            if (f5 > 0.0F && p_408248_.effects().isSunriseOrSunset(p_408248_.getTimeOfDay(p_408253_))) {
                int i = p_408248_.effects().getSunriseOrSunsetColor(p_408248_.getTimeOfDay(p_408253_));
                f5 *= ARGB.alphaFloat(i);
                f1 = Mth.lerp(f5, f1, ARGB.redFloat(i));
                f2 = Mth.lerp(f5, f2, ARGB.greenFloat(i));
                f3 = Mth.lerp(f5, f3, ARGB.blueFloat(i));
            }
        }

        int j = p_408248_.getSkyColor(p_406520_.getPosition(), p_408253_);
        float f10 = ARGB.redFloat(j);
        float f11 = ARGB.greenFloat(j);
        float f12 = ARGB.blueFloat(j);
        float f6 = 0.25F + 0.75F * p_408570_ / 32.0F;
        f6 = 1.0F - (float)Math.pow(f6, 0.25);
        f1 += (f10 - f1) * f6;
        f2 += (f11 - f2) * f6;
        f3 += (f12 - f3) * f6;
        float f7 = p_408248_.getRainLevel(p_408253_);
        if (f7 > 0.0F) {
            float f8 = 1.0F - f7 * 0.5F;
            float f9 = 1.0F - f7 * 0.4F;
            f1 *= f8;
            f2 *= f8;
            f3 *= f9;
        }

        float f13 = p_408248_.getThunderLevel(p_408253_);
        if (f13 > 0.0F) {
            float f14 = 1.0F - f13 * 0.5F;
            f1 *= f14;
            f2 *= f14;
            f3 *= f14;
        }

        return ARGB.colorFromFloat(1.0F, f1, f2, f3);
    }
}