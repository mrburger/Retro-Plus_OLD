package com.mrburgerus.retroplus.world.beta.gen;

import com.mrburgerus.retroplus.RetroPlus;
import com.mrburgerus.retroplus.api.HeightMap;
import com.mrburgerus.retroplus.world.beta.BetaChunkGeneratorConfig;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

// Implement Height Map so we can remove the simulator.
public class BetaChunkGenerator extends ChunkGenerator<BetaChunkGeneratorConfig>
{
	/* FIELDS */

	/* CONSTRUCTORS */
	public BetaChunkGenerator(IWorld world, BiomeSource biomeSource, BetaChunkGeneratorConfig configSettings)
	{
		super(world, biomeSource, configSettings);
	}

	// Build the surface, including grass blocks and the like.
	@Override
	public void buildSurface(ChunkRegion chunkRegion, Chunk chunk)
	{
		// TODO WRTIE
		int startX = chunk.getPos().getStartX();
		int startZ = chunk.getPos().getStartZ();
		for (int xP = 0; xP < RetroPlus.CHUNK_SIZE; xP++)
		{
			for (int zP = 0; zP < RetroPlus.CHUNK_SIZE; zP++)
			{
				int x = startX + xP;
				int z = startZ + zP;
				int height = (int) Math.round(Math.random() * 2 + RetroPlus.SEA_LEVEL);
				for (int y = 0; y < height; y++)
				{
					chunk.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState(), false);
				}
			}
		}
	}

	// Spawning height
	@Override
	public int getSpawnHeight()
	{
		return RetroPlus.SEA_LEVEL + 1;
	}

	@Override
	public void populateNoise(IWorld world, Chunk chunk)
	{

	}

	// Get height of a block position, very useful for Villages and other crap that is always breaking.
	@Override
	public int getHeightOnGround(int x, int z, Heightmap.Type heightmapType)
	{
		return 0;
	}
}
