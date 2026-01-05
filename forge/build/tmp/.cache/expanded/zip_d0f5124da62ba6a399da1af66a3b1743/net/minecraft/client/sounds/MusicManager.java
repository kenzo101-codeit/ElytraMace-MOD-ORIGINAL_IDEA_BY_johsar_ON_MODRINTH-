package net.minecraft.client.sounds;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.OptionEnum;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MusicManager {
    private static final int STARTING_DELAY = 100;
    private final RandomSource random = RandomSource.create();
    private final Minecraft minecraft;
    @Nullable
    private SoundInstance currentMusic;
    private MusicManager.MusicFrequency gameMusicFrequency;
    private float currentGain = 1.0F;
    private int nextSongDelay = 100;
    private boolean toastShown = false;

    public MusicManager(Minecraft p_120182_) {
        this.minecraft = p_120182_;
        this.gameMusicFrequency = p_120182_.options.musicFrequency().get();
    }

    public void tick() {
        MusicInfo musicinfo = this.minecraft.getSituationalMusic();
        float f = musicinfo.volume();
        if (this.currentMusic != null && this.currentGain != f) {
            boolean flag = this.fadePlaying(f);
            if (!flag) {
                return;
            }
        }

        Music music = musicinfo.music();
        if (music == null) {
            this.nextSongDelay = Math.max(this.nextSongDelay, 100);
        } else {
            if (this.currentMusic != null) {
                if (musicinfo.canReplace(this.currentMusic)) {
                    this.minecraft.getSoundManager().stop(this.currentMusic);
                    this.nextSongDelay = Mth.nextInt(this.random, 0, music.minDelay() / 2);
                }

                if (!this.minecraft.getSoundManager().isActive(this.currentMusic)) {
                    this.currentMusic = null;
                    this.nextSongDelay = Math.min(this.nextSongDelay, this.gameMusicFrequency.getNextSongDelay(music, this.random));
                }
            }

            this.nextSongDelay = Math.min(this.nextSongDelay, this.gameMusicFrequency.getNextSongDelay(music, this.random));
            if (this.currentMusic == null && this.nextSongDelay-- <= 0) {
                this.startPlaying(musicinfo);
            }
        }
    }

    public void startPlaying(MusicInfo p_377601_) {
        SoundEvent soundevent = p_377601_.music().event().value();
        this.currentMusic = SimpleSoundInstance.forMusic(soundevent, p_377601_.volume());
        switch (this.minecraft.getSoundManager().play(this.currentMusic)) {
            case STARTED:
                this.minecraft.getToastManager().showNowPlayingToast();
                this.toastShown = true;
                break;
            case STARTED_SILENTLY:
                this.toastShown = false;
        }

        this.nextSongDelay = Integer.MAX_VALUE;
        this.currentGain = p_377601_.volume();
    }

    public void showNowPlayingToastIfNeeded() {
        if (!this.toastShown) {
            this.minecraft.getToastManager().showNowPlayingToast();
            this.toastShown = true;
        }
    }

    public void stopPlaying(Music p_278295_) {
        if (this.isPlayingMusic(p_278295_)) {
            this.stopPlaying();
        }
    }

    public void stopPlaying() {
        if (this.currentMusic != null) {
            this.minecraft.getSoundManager().stop(this.currentMusic);
            this.currentMusic = null;
            this.minecraft.getToastManager().hideNowPlayingToast();
        }

        this.nextSongDelay += 100;
    }

    private boolean fadePlaying(float p_375585_) {
        if (this.currentMusic == null) {
            return false;
        } else if (this.currentGain == p_375585_) {
            return true;
        } else {
            if (this.currentGain < p_375585_) {
                this.currentGain = this.currentGain + Mth.clamp(this.currentGain, 5.0E-4F, 0.005F);
                if (this.currentGain > p_375585_) {
                    this.currentGain = p_375585_;
                }
            } else {
                this.currentGain = 0.03F * p_375585_ + 0.97F * this.currentGain;
                if (Math.abs(this.currentGain - p_375585_) < 1.0E-4F || this.currentGain < p_375585_) {
                    this.currentGain = p_375585_;
                }
            }

            this.currentGain = Mth.clamp(this.currentGain, 0.0F, 1.0F);
            if (this.currentGain <= 1.0E-4F) {
                this.stopPlaying();
                return false;
            } else {
                this.minecraft.getSoundManager().setVolume(this.currentMusic, this.currentGain);
                return true;
            }
        }
    }

    public boolean isPlayingMusic(Music p_120188_) {
        return this.currentMusic == null ? false : p_120188_.event().value().location().equals(this.currentMusic.getLocation());
    }

    @Nullable
    public String getCurrentMusicTranslationKey() {
        if (this.currentMusic != null) {
            Sound sound = this.currentMusic.getSound();
            if (sound != null) {
                return sound.getLocation().toShortLanguageKey();
            }
        }

        return null;
    }

    public void setMinutesBetweenSongs(MusicManager.MusicFrequency p_409813_) {
        this.gameMusicFrequency = p_409813_;
        this.nextSongDelay = this.gameMusicFrequency.getNextSongDelay(this.minecraft.getSituationalMusic().music(), this.random);
    }

    @OnlyIn(Dist.CLIENT)
    public static enum MusicFrequency implements OptionEnum, StringRepresentable {
        DEFAULT(20),
        FREQUENT(10),
        CONSTANT(0);

        public static final Codec<MusicManager.MusicFrequency> CODEC = StringRepresentable.fromEnum(MusicManager.MusicFrequency::values);
        private static final String KEY_PREPEND = "options.music_frequency.";
        private final int id;
        private final int maxFrequency;
        private final String key;

        private MusicFrequency(final int p_408860_) {
            this.id = p_408860_;
            this.maxFrequency = p_408860_ * 1200;
            this.key = "options.music_frequency." + this.name().toLowerCase();
        }

        int getNextSongDelay(@Nullable Music p_408535_, RandomSource p_409383_) {
            if (p_408535_ == null) {
                return this.maxFrequency;
            } else if (this == CONSTANT) {
                return 100;
            } else {
                int i = Math.min(p_408535_.minDelay(), this.maxFrequency);
                int j = Math.min(p_408535_.maxDelay(), this.maxFrequency);
                return Mth.nextInt(p_409383_, i, j);
            }
        }

        @Override
        public int getId() {
            return this.id;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public String getSerializedName() {
            return this.name();
        }
    }
}