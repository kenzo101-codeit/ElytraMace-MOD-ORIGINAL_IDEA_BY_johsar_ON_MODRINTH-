package net.minecraft.client.input;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record MouseButtonInfo(int button, int modifiers) implements InputWithModifiers {
    @Override
    public int input() {
        return this.button;
    }

    @Override
    public int modifiers() {
        return this.modifiers;
    }
}