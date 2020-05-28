package com.mrburgerus.retroplus.mixin;

import com.mrburgerus.retroplus.RetroPlus;
import com.mrburgerus.retroplus.world.beta.gen.BetaChunkGenerator;
import com.mrburgerus.retroplus.world.beta.BetaChunkGeneratorConfig;
import com.mrburgerus.retroplus.world.beta.BetaWorldType;
import com.mrburgerus.retroplus.world.beta.gen.BetaBiomeSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.level.LevelGeneratorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OverworldDimension.class)
public abstract class MixinOverworldDimension extends Dimension
{

	public MixinOverworldDimension(World world, DimensionType dimensionType)
	{
		super(world, dimensionType, 0.0F);
	}

	@Inject(method = "createChunkGenerator", at = @At("RETURN"), cancellable = true)
	public void createChunkGenerator(CallbackInfoReturnable<ChunkGenerator<? extends ChunkGeneratorConfig>> info)
	{
		RetroPlus.LOGGER.info("Overworld Logged");
		LevelGeneratorType type = this.world.getLevelProperties().getGeneratorType();
		ChunkGeneratorType<BetaChunkGeneratorConfig, BetaChunkGenerator> chunkGenType = BetaWorldType.betaChunkGeneratorType;


		if (type == BetaWorldType.WORLD_TYPE)
		{
			CompoundTag opts = this.world.getLevelProperties().getGeneratorOptions();
			// Insert Beta+ Generator
			info.setReturnValue(chunkGenType.create(this.world, new BetaBiomeSource(this.world.getSeed()), new BetaChunkGeneratorConfig()));
		}
	}
}
