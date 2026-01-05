package net.minecraft.world.level.saveddata;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;

public abstract class SavedData {
    private boolean dirty;

    public void setDirty() {
        this.setDirty(true);
    }

    public void setDirty(boolean p_77761_) {
        this.dirty = p_77761_;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public record Context(@Nullable ServerLevel level, long worldSeed) {
        public Context(ServerLevel p_394837_) {
            this(p_394837_, p_394837_.getSeed());
        }

        public ServerLevel levelOrThrow() {
            return Objects.requireNonNull(this.level);
        }
    }
}