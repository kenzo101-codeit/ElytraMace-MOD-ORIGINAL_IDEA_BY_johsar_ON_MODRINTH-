package com.mojang.math;

import java.util.Arrays;
import net.minecraft.Util;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;

public enum SymmetricGroup3 {
    P123(0, 1, 2),
    P213(1, 0, 2),
    P132(0, 2, 1),
    P231(1, 2, 0),
    P312(2, 0, 1),
    P321(2, 1, 0);

    private final int[] permutation;
    private final Matrix3fc transformation;
    private static final int ORDER = 3;
    private static final SymmetricGroup3[][] CAYLEY_TABLE = Util.make(new SymmetricGroup3[values().length][values().length], p_109188_ -> {
        for (SymmetricGroup3 symmetricgroup3 : values()) {
            for (SymmetricGroup3 symmetricgroup31 : values()) {
                int[] aint = new int[3];

                for (int i = 0; i < 3; i++) {
                    aint[i] = symmetricgroup3.permutation[symmetricgroup31.permutation[i]];
                }

                SymmetricGroup3 symmetricgroup32 = Arrays.stream(values()).filter(p_175577_ -> Arrays.equals(p_175577_.permutation, aint)).findFirst().get();
                p_109188_[symmetricgroup3.ordinal()][symmetricgroup31.ordinal()] = symmetricgroup32;
            }
        }
    });

    private SymmetricGroup3(final int p_109176_, final int p_109177_, final int p_109178_) {
        this.permutation = new int[]{p_109176_, p_109177_, p_109178_};
        Matrix3f matrix3f = new Matrix3f().zero();
        matrix3f.set(this.permutation(0), 0, 1.0F);
        matrix3f.set(this.permutation(1), 1, 1.0F);
        matrix3f.set(this.permutation(2), 2, 1.0F);
        this.transformation = matrix3f;
    }

    public SymmetricGroup3 compose(SymmetricGroup3 p_109183_) {
        return CAYLEY_TABLE[this.ordinal()][p_109183_.ordinal()];
    }

    public int permutation(int p_109181_) {
        return this.permutation[p_109181_];
    }

    public Matrix3fc transformation() {
        return this.transformation;
    }
}