package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public record LocalCoordinates(double left, double up, double forwards) implements Coordinates {
    public static final char PREFIX_LOCAL_COORDINATE = '^';

    @Override
    public Vec3 getPosition(CommandSourceStack p_119912_) {
        Vec2 vec2 = p_119912_.getRotation();
        Vec3 vec3 = p_119912_.getAnchor().apply(p_119912_);
        float f = Mth.cos((vec2.y + 90.0F) * (float) (Math.PI / 180.0));
        float f1 = Mth.sin((vec2.y + 90.0F) * (float) (Math.PI / 180.0));
        float f2 = Mth.cos(-vec2.x * (float) (Math.PI / 180.0));
        float f3 = Mth.sin(-vec2.x * (float) (Math.PI / 180.0));
        float f4 = Mth.cos((-vec2.x + 90.0F) * (float) (Math.PI / 180.0));
        float f5 = Mth.sin((-vec2.x + 90.0F) * (float) (Math.PI / 180.0));
        Vec3 vec31 = new Vec3(f * f2, f3, f1 * f2);
        Vec3 vec32 = new Vec3(f * f4, f5, f1 * f4);
        Vec3 vec33 = vec31.cross(vec32).scale(-1.0);
        double d0 = vec31.x * this.forwards + vec32.x * this.up + vec33.x * this.left;
        double d1 = vec31.y * this.forwards + vec32.y * this.up + vec33.y * this.left;
        double d2 = vec31.z * this.forwards + vec32.z * this.up + vec33.z * this.left;
        return new Vec3(vec3.x + d0, vec3.y + d1, vec3.z + d2);
    }

    @Override
    public Vec2 getRotation(CommandSourceStack p_119915_) {
        return Vec2.ZERO;
    }

    @Override
    public boolean isXRelative() {
        return true;
    }

    @Override
    public boolean isYRelative() {
        return true;
    }

    @Override
    public boolean isZRelative() {
        return true;
    }

    public static LocalCoordinates parse(StringReader p_119907_) throws CommandSyntaxException {
        int i = p_119907_.getCursor();
        double d0 = readDouble(p_119907_, i);
        if (p_119907_.canRead() && p_119907_.peek() == ' ') {
            p_119907_.skip();
            double d1 = readDouble(p_119907_, i);
            if (p_119907_.canRead() && p_119907_.peek() == ' ') {
                p_119907_.skip();
                double d2 = readDouble(p_119907_, i);
                return new LocalCoordinates(d0, d1, d2);
            } else {
                p_119907_.setCursor(i);
                throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(p_119907_);
            }
        } else {
            p_119907_.setCursor(i);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(p_119907_);
        }
    }

    private static double readDouble(StringReader p_119909_, int p_119910_) throws CommandSyntaxException {
        if (!p_119909_.canRead()) {
            throw WorldCoordinate.ERROR_EXPECTED_DOUBLE.createWithContext(p_119909_);
        } else if (p_119909_.peek() != '^') {
            p_119909_.setCursor(p_119910_);
            throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext(p_119909_);
        } else {
            p_119909_.skip();
            return p_119909_.canRead() && p_119909_.peek() != ' ' ? p_119909_.readDouble() : 0.0;
        }
    }
}