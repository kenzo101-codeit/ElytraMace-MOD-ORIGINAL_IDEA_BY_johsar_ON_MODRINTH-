package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record ItemPredicate(Optional<HolderSet<Item>> items, MinMaxBounds.Ints count, DataComponentMatchers components) implements Predicate<ItemStack> {
    public static final Codec<ItemPredicate> CODEC = RecordCodecBuilder.create(
        p_389120_ -> p_389120_.group(
                RegistryCodecs.homogeneousList(Registries.ITEM).optionalFieldOf("items").forGetter(ItemPredicate::items),
                MinMaxBounds.Ints.CODEC.optionalFieldOf("count", MinMaxBounds.Ints.ANY).forGetter(ItemPredicate::count),
                DataComponentMatchers.CODEC.forGetter(ItemPredicate::components)
            )
            .apply(p_389120_, ItemPredicate::new)
    );

    public boolean test(ItemStack p_331873_) {
        if (this.items.isPresent() && !p_331873_.is(this.items.get())) {
            return false;
        } else {
            return !this.count.matches(p_331873_.getCount()) ? false : this.components.test((DataComponentGetter)p_331873_);
        }
    }

    public static class Builder {
        private Optional<HolderSet<Item>> items = Optional.empty();
        private MinMaxBounds.Ints count = MinMaxBounds.Ints.ANY;
        private DataComponentMatchers components = DataComponentMatchers.ANY;

        public static ItemPredicate.Builder item() {
            return new ItemPredicate.Builder();
        }

        public ItemPredicate.Builder of(HolderGetter<Item> p_369135_, ItemLike... p_151446_) {
            this.items = Optional.of(HolderSet.direct(p_300947_ -> p_300947_.asItem().builtInRegistryHolder(), p_151446_));
            return this;
        }

        public ItemPredicate.Builder of(HolderGetter<Item> p_367979_, TagKey<Item> p_204146_) {
            this.items = Optional.of(p_367979_.getOrThrow(p_204146_));
            return this;
        }

        public ItemPredicate.Builder withCount(MinMaxBounds.Ints p_151444_) {
            this.count = p_151444_;
            return this;
        }

        public ItemPredicate.Builder withComponents(DataComponentMatchers p_395621_) {
            this.components = p_395621_;
            return this;
        }

        public ItemPredicate build() {
            return new ItemPredicate(this.items, this.count, this.components);
        }
    }
}