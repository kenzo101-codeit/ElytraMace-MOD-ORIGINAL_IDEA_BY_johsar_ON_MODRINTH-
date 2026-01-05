package net.minecraft.client.renderer.block.model;

import com.mojang.math.Quadrant;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record Variant(ResourceLocation modelLocation, Variant.SimpleModelState modelState) implements BlockModelPart.Unbaked {
    public static final MapCodec<Variant> MAP_CODEC = RecordCodecBuilder.mapCodec(
        p_394819_ -> p_394819_.group(
                ResourceLocation.CODEC.fieldOf("model").forGetter(Variant::modelLocation), Variant.SimpleModelState.MAP_CODEC.forGetter(Variant::modelState)
            )
            .apply(p_394819_, Variant::new)
    );
    public static final Codec<Variant> CODEC = MAP_CODEC.codec();

    public Variant(ResourceLocation p_397682_) {
        this(p_397682_, Variant.SimpleModelState.DEFAULT);
    }

    public Variant withXRot(Quadrant p_397734_) {
        return this.withState(this.modelState.withX(p_397734_));
    }

    public Variant withYRot(Quadrant p_392925_) {
        return this.withState(this.modelState.withY(p_392925_));
    }

    public Variant withUvLock(boolean p_394546_) {
        return this.withState(this.modelState.withUvLock(p_394546_));
    }

    public Variant withModel(ResourceLocation p_396162_) {
        return new Variant(p_396162_, this.modelState);
    }

    public Variant withState(Variant.SimpleModelState p_391782_) {
        return new Variant(this.modelLocation, p_391782_);
    }

    public Variant with(VariantMutator p_394270_) {
        return p_394270_.apply(this);
    }

    @Override
    public BlockModelPart bake(ModelBaker p_397047_) {
        return SimpleModelWrapper.bake(p_397047_, this.modelLocation, this.modelState.asModelState());
    }

    @Override
    public void resolveDependencies(ResolvableModel.Resolver p_391294_) {
        p_391294_.markDependency(this.modelLocation);
    }

    @OnlyIn(Dist.CLIENT)
    public record SimpleModelState(Quadrant x, Quadrant y, boolean uvLock) {
        public static final MapCodec<Variant.SimpleModelState> MAP_CODEC = RecordCodecBuilder.mapCodec(
            p_391692_ -> p_391692_.group(
                    Quadrant.CODEC.optionalFieldOf("x", Quadrant.R0).forGetter(Variant.SimpleModelState::x),
                    Quadrant.CODEC.optionalFieldOf("y", Quadrant.R0).forGetter(Variant.SimpleModelState::y),
                    Codec.BOOL.optionalFieldOf("uvlock", false).forGetter(Variant.SimpleModelState::uvLock)
                )
                .apply(p_391692_, Variant.SimpleModelState::new)
        );
        public static final Variant.SimpleModelState DEFAULT = new Variant.SimpleModelState(Quadrant.R0, Quadrant.R0, false);

        public ModelState asModelState() {
            BlockModelRotation blockmodelrotation = BlockModelRotation.by(this.x, this.y);
            return (ModelState)(this.uvLock ? blockmodelrotation.withUvLock() : blockmodelrotation);
        }

        public Variant.SimpleModelState withX(Quadrant p_393583_) {
            return new Variant.SimpleModelState(p_393583_, this.y, this.uvLock);
        }

        public Variant.SimpleModelState withY(Quadrant p_395288_) {
            return new Variant.SimpleModelState(this.x, p_395288_, this.uvLock);
        }

        public Variant.SimpleModelState withUvLock(boolean p_396764_) {
            return new Variant.SimpleModelState(this.x, this.y, p_396764_);
        }
    }
}