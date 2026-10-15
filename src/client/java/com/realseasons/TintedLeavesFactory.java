package com.realseasons;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;

public class TintedLeavesFactory extends FallingParticle {
    protected TintedLeavesFactory(final ClientLevel level, final double x, final double y, final double z, final TextureAtlasSprite sprite, final float fallAcceleration, final float sideAcceleration, final boolean swirl, final boolean flowAway, final float scale, final float startVelocity) {
        super(level, x, y, z, sprite, fallAcceleration, sideAcceleration, swirl, flowAway, scale, startVelocity);
    }

    public static class TintedLeavesProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private ColorMapConfig config;
        private Block block;

        public TintedLeavesProvider(final SpriteSet sprites, ColorMapConfig config, Block block) {
            this.sprites = sprites;
            this.config = config;
            this.block = block;
        }

        public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final RandomSource random) {
            int color = SeasonColorManager.getBlendedFoliageColor(level, BlockPos.containing(x, y, z), block.defaultBlockState(), config);
            TintedLeavesFactory particle = new TintedLeavesFactory(level, x, y, z, this.sprites.get(random), 0.07F, 10.0F, true, false, 2.0F, 0.021F);
            int red = (color >> 16) & 0xFF;
            int green = (color >> 8) & 0xFF;
            int blue = color & 0xFF;
            particle.setColor(red/256.0f, green/256.0f, blue/256.0f);
            return particle;
        }
    }
}

