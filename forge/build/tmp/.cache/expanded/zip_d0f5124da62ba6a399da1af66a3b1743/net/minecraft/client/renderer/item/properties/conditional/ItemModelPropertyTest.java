package net.minecraft.client.renderer.item.properties.conditional;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@FunctionalInterface
@OnlyIn(Dist.CLIENT)
public interface ItemModelPropertyTest {
    boolean get(ItemStack p_395889_, @Nullable ClientLevel p_393910_, @Nullable LivingEntity p_393617_, int p_396904_, ItemDisplayContext p_395957_);
}