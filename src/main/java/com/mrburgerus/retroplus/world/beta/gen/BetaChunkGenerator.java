package com.mrburgerus.retroplus.world.beta.gen;

import com.mrburgerus.retroplus.RetroPlus;
import com.mrburgerus.retroplus.world.beta.BetaChunkGeneratorConfig;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

// Implement Height Map so we can remove the simulator and such.
// In the fuutre, a height map will be best.
public class BetaChunkGenerator extends SurfaceChunkGenerator<BetaChunkGeneratorConfig>
{
	/* FIELDS */

	/* CONSTRUCTORS */
	public BetaChunkGenerator(IWorld world, BiomeSource biomeSource, BetaChunkGeneratorConfig configSettings)
	{
		// Insert Beta-Values
		super(world, biomeSource, 4,4, RetroPlus.WORLD_HEIGHT, configSettings, false);
	}

	// Build the surface, including grass blocks and the like.
	@Override
	public void buildSurface(ChunkRegion chunkRegion, Chunk chunk)
	{
		// TODO WRITE
		// ADD Gravel Beaches and such.
		super.buildSurface(chunkRegion, chunk);
	}

	@Override
	public void populateNoise(IWorld world, Chunk chunk)
	{

	}

	@Override
	protected double[] computeNoiseRange(int x, int z)
	{
		return new double[0];
	}

	@Override
	protected double computeNoiseFalloff(double depth, double scale, int y)
	{
		return 0;
	}

	// Get height of a block position, very useful for Villages and other crap that is always breaking.
	@Override
	public int getHeightOnGround(int x, int z, Heightmap.Type heightmapType)
	{
		return 0;
	}

	@Override
	protected void sampleNoiseColumn(double[] buffer, int x, int z)
	{

	}

	// Spawning height
	@Override
	public int getSpawnHeight()
	{
		return RetroPlus.SEA_LEVEL + 1;
	}
}
