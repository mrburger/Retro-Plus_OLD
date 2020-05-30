package com.mrburgerus.retroplus.world.beta.gen;

import com.mrburgerus.retroplus.RetroPlus;
import com.mrburgerus.retroplus.mixin.MixinBiomeArray;
import com.mrburgerus.retroplus.util.MutableBiomeArray;
import com.mrburgerus.retroplus.world.beta.BetaChunkGeneratorConfig;
import com.mrburgerus.retroplus.world.noise.beta.NoiseGeneratorOctavesBeta;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.PackedIntegerArray;
import net.minecraft.util.Pair;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// Implement Height Map so we can remove the simulator and such.
// In the future, a height map will be best.
// See SurfaceChunkGenerator for help.
public class BetaChunkGenerator extends SurfaceChunkGenerator<BetaChunkGeneratorConfig> implements Tickable
{
	/* FIELDS */
	private Random random;

	// Octave Generators
	private NoiseGeneratorOctavesBeta octaves1;
	private double[] octaveArr1;
	private NoiseGeneratorOctavesBeta octaves2;
	private double[] octaveArr2;
	private NoiseGeneratorOctavesBeta octaves3;
	private double[] octaveArr3;
	private NoiseGeneratorOctavesBeta beachBlockNoise; // Formerly scaleNoise, used for Gravel and Sand, so probably beaches.
	private double[] sandNoise = new double[256];
	private double[] gravelNoise = new double[256];
	private double[] stoneNoise = new double[256];
	private NoiseGeneratorOctavesBeta surfaceNoise; // Formerly octaves7
	private NoiseGeneratorOctavesBeta scaleNoise; // Formerly octaves6, renamed using ChunkGeneratorOverworld
	private double[] scaleNoiseArr;
	private NoiseGeneratorOctavesBeta octaves7;
	private double[] octaveArr7;
	// Noise Arrays
	private double[] heightNoise;
	private double[] heightCacheNoise;
	// Biome PackedIntArray
	PackedIntegerArray heightMapArray = new PackedIntegerArray(9, 256);

	// Block Y-height cache
	public Map<BlockPos, Integer> groundCacheY = new HashMap<>();
	// Biome based (4x4x4) Y-height cache
	public Map<BlockPos, Integer> biomeCacheY = new HashMap<>();

	/* CONSTRUCTORS */
	public BetaChunkGenerator (IWorld world, BiomeSource biomeSource, BetaChunkGeneratorConfig configSettings)
	{
		// Insert Beta-Values
		super(world, biomeSource, 4, 4, 256, configSettings, false);
		this.random = new Random(seed);
		octaves1 = new NoiseGeneratorOctavesBeta(random, 16);
		octaves2 = new NoiseGeneratorOctavesBeta(random, 16);
		octaves3 = new NoiseGeneratorOctavesBeta(random, 8);
		beachBlockNoise = new NoiseGeneratorOctavesBeta(random, 4);
		surfaceNoise = new NoiseGeneratorOctavesBeta(random, 4);
		scaleNoise = new NoiseGeneratorOctavesBeta(random, 10);
		octaves7 = new NoiseGeneratorOctavesBeta(random, 16);
		// Set chunk generator
		((BetaBiomeSource) biomeSource).setChunkGenerator(this);
	}

	// WORKS
	// Build the surface, including grass blocks and the like.
	@Override
	public void buildSurface (ChunkRegion chunkRegion, Chunk chunk)
	{
		// For now
		//super.buildSurface(chunkRegion, chunk);
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;
		random.setSeed((long) chunkX * 341873128712L + (long) chunkZ * 132897987541L);
		this.buildBiomeSurface(chunk);
	}

	// Spawning height
	@Override
	public int getSpawnHeight ()
	{
		return RetroPlus.SEA_LEVEL + 1;
	}

	@Override
	public int getSeaLevel ()
	{
		return RetroPlus.SEA_LEVEL;
	}

	// WORKING
	// Add Stone & Water features
	@Override
	public void populateNoise (IWorld world, Chunk chunk)
	{
		// Testing getHeight
		// it appears to work
		/*
		ChunkPos chunkPos = chunk.getPos();
		for (int posX = 0; posX < RetroPlus.CHUNK_SIZE; posX++)
		{
			for (int posZ = 0; posZ < RetroPlus.CHUNK_SIZE; posZ++)
			{
				// This line works
				//int groundHeight = this.getHeightOnGround(chunkPos.getStartX() + posX, chunkPos.getStartZ() + posZ, Heightmap.Type.WORLD_SURFACE_WG);
				// Pass it through...
				// RESULTS: Correct. This is making me angry.
				int groundHeight = ((BetaBiomeSource) biomeSource).generator.getHeightOnGround(chunkPos.getStartX() + posX, chunkPos.getStartZ() + posZ, Heightmap.Type.WORLD_SURFACE_WG);
				for (int posY = 0; posY <= groundHeight; posY++)
				{
					chunk.setBlockState(new BlockPos(posX, posY, posZ), Blocks.STONE.getDefaultState(), false);
				}
			}
		}
		 */
		//Testing null
		// Works
		//long[] heightArray = new long[256];
		int[][] chunkY = new int[RetroPlus.CHUNK_SIZE][RetroPlus.CHUNK_SIZE];
		heightNoise = octaveGenerator(null, chunk.getPos().x * 4, chunk.getPos().z * 4);
		for (int ctrX = 0; ctrX < 4; ++ctrX)
		{
			for (int ctrZ = 0; ctrZ < 4; ++ctrZ)
			{
				for (int ctrY = 0; ctrY < 16; ++ctrY)
				{
					double eigth = 0.125;
					double var16 = heightNoise[((ctrX) * 5 + ctrZ) * 17 + ctrY];
					double var18 = heightNoise[((ctrX) * 5 + ctrZ + 1) * 17 + ctrY];
					double var20 = heightNoise[((ctrX + 1) * 5 + ctrZ) * 17 + ctrY];
					double var22 = heightNoise[((ctrX + 1) * 5 + ctrZ + 1) * 17 + ctrY];
					double var24 = (heightNoise[((ctrX) * 5 + ctrZ) * 17 + ctrY + 1] - var16) * eigth;
					double var26 = (heightNoise[((ctrX) * 5 + ctrZ + 1) * 17 + ctrY + 1] - var18) * eigth;
					double var28 = (heightNoise[((ctrX + 1) * 5 + ctrZ) * 17 + ctrY + 1] - var20) * eigth;
					double var30 = (heightNoise[((ctrX + 1) * 5 + ctrZ + 1) * 17 + ctrY + 1] - var22) * eigth;
					for (int l = 0; l < 8; ++l)
					{
						double quarter = 0.25;
						double var35 = var16;
						double var37 = var18;
						double var39 = (var20 - var16) * quarter;
						double var41 = (var22 - var18) * quarter;
						for (int m = 0; m < 4; ++m)
						{
							int x = m + ctrX * 4;
							int y = ctrY * 8 + l;
							int z = ctrZ * 4;
							double var46 = 0.25;
							double stoneNoiseDensity = var35;
							double var50 = (var37 - var35) * var46;
							for (int n = 0; n < 4; ++n)
							{
								Block block = null;
								if (y < RetroPlus.SEA_LEVEL)
								{
									block = Blocks.WATER;
								}
								if (stoneNoiseDensity > 0.0)
								{
									block = Blocks.STONE;
									// MOVED UP HERE
									// Add to heightmap?
									chunkY[x][z] = y + 1;
									// Testing
									heightMapArray.set(x + z * 16, y);
								}
								if (block != null)
								{
									chunk.setBlockState(new BlockPos(x, y, z), block.getDefaultState(), false);
								}
								++z;
								stoneNoiseDensity += var50;
							}
							var35 += var39;
							var37 += var41;
						}
						var16 += var24;
						var18 += var26;
						var20 += var28;
						var22 += var30;
					}

				}
			}
		}
		// Testing (PULLED FROM Heightmap.class & PackedIntegerArray.class)

		chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG).setTo(heightMapArray.getStorage());
		//RetroPlus.LOGGER.info(chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG).asLongArray().length); //36

		// Replace Ocean Biomes (All blocks in 4x4 under sea level
		MutableBiomeArray biomeArray = MutableBiomeArray.inject(chunk.getBiomeArray());
		int submergedCount = 0;
		for (int ctrX = 0; ctrX < 4; ctrX++)
		{
			for (int ctrZ = 0; ctrZ < 4; ctrZ++)
			{
				// Check all 16 values
				submergedCount = 0;
				int lBoundX = ctrX * 4;
				int uBoundX = lBoundX + 4;
				int lBoundZ = ctrZ * 4;
				int uBoundZ = lBoundZ + 4;
				// Check each value in segment is below Sea Level
				for (int cX = lBoundX; cX < uBoundX; cX++)
				{
					for (int cZ = lBoundZ; cZ < uBoundZ; cZ++)
					{
						if (chunkY[cX][cZ] < RetroPlus.SEA_LEVEL - 1) // -1 because we dont want surface level blocks to be oceanic
						{
							submergedCount++;
						}
					}
				}
				if (submergedCount == 14) // Allow for some leeway
				{
					// TEMPORARY
					biomeArray.setBiome(lBoundX, 0, lBoundZ, Biomes.OCEAN);
				}
			}
		}
	}

	// Replaces simulator (REQUIRED?)
	// x and z are block coordinates

	@Override
	public int getHeightOnGround (int x, int z, Heightmap.Type heightmapType)
	{
		if (heightmapType != Heightmap.Type.WORLD_SURFACE_WG)
		{
			return RetroPlus.SEA_LEVEL * 2;
		}
		BlockPos blockPos = new BlockPos(x, 0, z);
		ChunkPos chunkPos = new ChunkPos(blockPos);
		if (groundCacheY.get(blockPos) == null)
		{
			populateGroundCache(chunkPos);
		}
		int groundHeight = groundCacheY.get(blockPos);
		return groundHeight;
	}

	// Get biome-coordinate height.
	// VERY SLOW
	public int getHeightOnGroundForBiome (int biomeX, int biomeZ)
	{
		int absoluteStartX = biomeX << 2; // Closest X-Coordinate start
		int absoluteStartZ = biomeZ << 2; // Closest Z-Coordinate start
		int absoluteEndX = absoluteStartX + 3; // Unknown if correct.
		int absoluteEndZ = absoluteStartZ + 3; // Unknown if correct.
		BlockPos blockPos = new BlockPos(absoluteStartX, 0, absoluteStartZ);
		ChunkPos chunkPos = new ChunkPos(blockPos);

		// SLOW
		//return world.getChunk(blockPos).getHeightmap(Heightmap.Type.WORLD_SURFACE_WG).get(absoluteStartX & 15, absoluteStartZ & 15);
		if (biomeCacheY.get(blockPos) == null)
		{
			//RetroPlus.LOGGER.info("Create for: " + blockPos.getX() + ", " + blockPos.getZ());
			populateBiomeGroundCache(chunkPos);
		}
		Integer groundHeight = biomeCacheY.get(blockPos);
		if (groundHeight == null)
		{
			groundHeight = 100;
			RetroPlus.LOGGER.error(blockPos.getX() + ", " + blockPos.getZ());
		}

		return groundHeight;
	}

	// Unknown if this will even work. Just forget about it I guess?
	private void populateBiomeGroundCache (ChunkPos chunkPos)
	{
		heightCacheNoise = octaveGenerator(null, chunkPos.x * 4, chunkPos.z * 4);
		int[][] chunkY = new int[4][4];
		for (int ctrX = 0; ctrX < 4; ++ctrX)
		{
			for (int ctrZ = 0; ctrZ < 4; ++ctrZ)
			{
				for (int ctrY = 0; ctrY < 16; ++ctrY)
				{
					double eighth = 0.125;
					double noise1 = heightCacheNoise[(ctrX * 5 + ctrZ) * 17 + ctrY];
					double eightNoise1 = (heightCacheNoise[(ctrX * 5 + ctrZ) * 17 + ctrY + 1] - noise1) * eighth;
					for (int ctrY2 = 0; ctrY2 < 8; ++ctrY2)
					{
						double stoneDensity = noise1;
						int posX = ctrX * 4;
						int posZ = ctrZ * 4;
						int posY = ctrY * 8 + ctrY2;
						if (stoneDensity > 0.0)
						{
							chunkY[ctrX][ctrZ] = posY;
						}
						noise1 += eightNoise1;
					}
				}
			}
		}
		for (int pX = 0; pX < chunkY.length; pX++)
		{
			for (int pZ = 0; pZ < chunkY[pX].length; pZ++)
			{
				// ??
				BlockPos pos = new BlockPos(chunkPos.getStartX() + (pX * 4), 0, chunkPos.getStartZ() + (pZ * 4));
				biomeCacheY.put(pos, chunkY[pX][pZ] + 1); // +1 because it is one above the ground
			}
		}
	}

	// WORKING
	// See populateNoise for base
	private void populateGroundCache (ChunkPos chunkPos)
	{
		heightCacheNoise = octaveGenerator(null, chunkPos.x * 4, chunkPos.z * 4);
		int[][] chunkY = new int[16][16];

		for (int ctrX = 0; ctrX < 4; ++ctrX)
		{
			for (int ctrZ = 0; ctrZ < 4; ++ctrZ)
			{
				for (int ctrY = 0; ctrY < 16; ++ctrY)
				{
					double eigth = 0.125;
					double var16 = heightCacheNoise[((ctrX) * 5 + ctrZ) * 17 + ctrY];
					double var18 = heightCacheNoise[((ctrX) * 5 + ctrZ + 1) * 17 + ctrY];
					double var20 = heightCacheNoise[((ctrX + 1) * 5 + ctrZ) * 17 + ctrY];
					double var22 = heightCacheNoise[((ctrX + 1) * 5 + ctrZ + 1) * 17 + ctrY];
					double var24 = (heightCacheNoise[((ctrX) * 5 + ctrZ) * 17 + ctrY + 1] - var16) * eigth;
					double var26 = (heightCacheNoise[((ctrX) * 5 + ctrZ + 1) * 17 + ctrY + 1] - var18) * eigth;
					double var28 = (heightCacheNoise[((ctrX + 1) * 5 + ctrZ) * 17 + ctrY + 1] - var20) * eigth;
					double var30 = (heightCacheNoise[((ctrX + 1) * 5 + ctrZ + 1) * 17 + ctrY + 1] - var22) * eigth;
					for (int l = 0; l < 8; ++l)
					{
						double quarter = 0.25;
						double var35 = var16;
						double var37 = var18;
						double var39 = (var20 - var16) * quarter;
						double var41 = (var22 - var18) * quarter;
						for (int m = 0; m < 4; ++m)
						{
							int posX = m + ctrX * 4;
							int posY = ctrY * 8 + l;
							int posZ = ctrZ * 4;
							double var46 = 0.25;
							double stoneDensity = var35;
							double var50 = (var37 - var35) * var46;
							for (int n = 0; n < 4; ++n)
							{
								if (stoneDensity > 0.0)
								{
									chunkY[posX][posZ] = posY;
								}
								++posZ;
								stoneDensity += var50;
							}
							var35 += var39;
							var37 += var41;
						}
						var16 += var24;
						var18 += var26;
						var20 += var28;
						var22 += var30;
					}
				}
			}
		}
		// This is much faster, only one .put() call
		for (int pX = 0; pX < chunkY.length; pX++)
		{
			for (int pZ = 0; pZ < chunkY[pX].length; pZ++)
			{
				BlockPos pos = new BlockPos(chunkPos.getStartX() + pX, 0, chunkPos.getStartZ() + pZ);
				groundCacheY.put(pos, chunkY[pX][pZ] + 1); // +1 because it is one above the ground
			}
		}
	}

	// Testing
	public Triple<double[], double[], double[]> getCoastNoiseFields (ChunkPos chunkPos)
	{
		double thirtySecond = 1.0 / 32.0;
		double[] sand = this.beachBlockNoise.generateNoiseOctaves(null, chunkPos.x * 16, chunkPos.z * 16, 0.0, 16, 16, 1, thirtySecond, thirtySecond, 1.0);
		double[] gravel = this.beachBlockNoise.generateNoiseOctaves(null, chunkPos.x * 16, 109.0134, chunkPos.z * 16, 16, 1, 16, thirtySecond, 1.0, thirtySecond);
		double[] stone = this.surfaceNoise.generateNoiseOctaves(null, chunkPos.x * 16, chunkPos.z * 16, 0.0, 16, 16, 1, thirtySecond * 2.0, thirtySecond * 2.0, thirtySecond * 2.0);
		return Triple.of(sand, gravel, stone);
	}

	/* YES, IT IS COPIED AND MODIFIED FROM 1.12 */
	private void buildBiomeSurface (Chunk chunk)
	{
		ChunkPos chunkPos = chunk.getPos();
		int chunkX = chunkPos.x;
		int chunkZ = chunkPos.z;
		double thirtySecond = 0.03125;
		Triple<double[], double[], double[]> tr = this.getCoastNoiseFields(chunkPos);
		this.sandNoise = tr.getLeft();
		//this.beachBlockNoise.generateNoiseOctaves(this.sandNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond, thirtySecond, 1.0);
		this.gravelNoise = tr.getMiddle();
		//this.beachBlockNoise.generateNoiseOctaves(this.gravelNoise, chunkX * 16, 109.0134, chunkZ * 16, 16, 1, 16, thirtySecond, 1.0, thirtySecond);
		this.stoneNoise = tr.getRight();
		//this.surfaceNoise.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond * 2.0, thirtySecond * 2.0, thirtySecond * 2.0);
		for (int posZ = 0; posZ < RetroPlus.CHUNK_SIZE; ++posZ)
		{
			for (int posX = 0; posX < RetroPlus.CHUNK_SIZE; ++posX)
			{
				// These are biome coordinates, not chunk or absolute coordinates. 4x4x4 blocks instead of granular.
				// Works, or appears to
				Biome biome = chunk.getBiomeArray().getBiomeForNoiseGen(posX >> 2, 0, posZ >> 2);
				boolean sandN = this.sandNoise[posZ + posX * 16] + this.random.nextDouble() * 0.2 > 0.0;
				boolean gravelN = this.gravelNoise[posZ + posX * 16] + this.random.nextDouble() * 0.2 > 3.0;
				int stoneN = (int) (this.stoneNoise[posZ + posX * 16] / 3.0 + 3.0 + this.random.nextDouble() * 0.25);
				int checkVal = -1;
				BlockState topBlock = biome.getSurfaceConfig().getTopMaterial();
				BlockState fillerBlock = biome.getSurfaceConfig().getUnderMaterial();

				// GO from Top to bottom of world
				// Changed from 127 to getMaxY()
				for (int y = getMaxY(); y >= 0; --y)
				{
					if (y <= this.random.nextInt(5))
					{
						chunk.setBlockState(new BlockPos(posX, y, posZ), Blocks.BEDROCK.getDefaultState(), false);
					}
					else
					{
						Block block = chunk.getBlockState(new BlockPos(posX, y, posZ)).getBlock();

						if (block == Blocks.AIR)
						{
							checkVal = -1;
							continue;
						}

						//Checks if model already changed
						if (block != Blocks.STONE)
						{
							continue;
						}

						if (checkVal == -1)
						{
							if (stoneN <= 0)
							{
								topBlock = Blocks.AIR.getDefaultState();
								fillerBlock = Blocks.STONE.getDefaultState();
							}
							else if (y >= RetroPlus.SEA_LEVEL - 4 && y <= RetroPlus.SEA_LEVEL + 1)
							{
								topBlock = biome.getSurfaceConfig().getTopMaterial();
								fillerBlock = biome.getSurfaceConfig().getUnderMaterial();
								if (gravelN)
								{
									topBlock = Blocks.AIR.getDefaultState();
									fillerBlock = Blocks.GRAVEL.getDefaultState();
								}
								if (sandN)
								{
									topBlock = Blocks.SAND.getDefaultState();
									fillerBlock = Blocks.SAND.getDefaultState();
								}
							}


							if (y < RetroPlus.SEA_LEVEL && topBlock == Blocks.AIR.getDefaultState())
							{
								topBlock = Blocks.WATER.getDefaultState();
							}

							// Sets top & filler Blocks
							checkVal = stoneN;
							// Test this still.
							if (y >= RetroPlus.SEA_LEVEL - 1)
							{
								chunk.setBlockState(new BlockPos(posX, y, posZ), topBlock, false);
							}
							else
							{
								chunk.setBlockState(new BlockPos(posX, y, posZ), fillerBlock, false);
							}
						}
						else if (checkVal > 0)
						{
							--checkVal;
							chunk.setBlockState(new BlockPos(posX, y, posZ), fillerBlock, false);
							//Possibly state comparison fucked it
							if (checkVal == 0 && fillerBlock == Blocks.SAND.getDefaultState())
							{
								checkVal = this.random.nextInt(4);
								fillerBlock = Blocks.SANDSTONE.getDefaultState();
							}
						} //END OF Y LOOP
					}
				}
			}
		}
	}

	/* Nice, New, Fresh! */
	private double[] octaveGenerator (double[] values, int posX, int posZ)
	{
		int distX = 5;
		int distY = 17;
		int distZ = distX;
		if (values == null)
		{
			values = new double[5 * 17 * 5];
		}
		double noiseFactor = 684.412;
		Pair p = ((BetaBiomeSource) biomeSource).getNoiseFields(posX, posZ, RetroPlus.CHUNK_SIZE);

		// New Test
		//((BetaBiomeSource) biomeSource).generateNoiseFields(posX, posZ, RetroPlus.CHUNK_SIZE);
		double[] temps = (double[]) p.getLeft();
		//((BetaBiomeSource) biomeSource).temperatures;
		double[] humidities = (double[]) p.getRight();
		//((BetaBiomeSource) biomeSource).humidities;
		// try nulls
		scaleNoiseArr = scaleNoise.generateNoiseOctaves(null, posX, posZ, distX, distZ, 1.121, 1.121, 0.5);
		octaveArr7 = octaves7.generateNoiseOctaves(null, posX, posZ, distX, distZ, 200.0, 200.0, 0.5);
		octaveArr1 = octaves3.generateNoiseOctaves(null, posX, 0, posZ, distX, distY, distZ, noiseFactor / 80.0, noiseFactor / 160.0, noiseFactor / 80.0);
		octaveArr2 = octaves1.generateNoiseOctaves(null, posX, 0, posZ, distX, distY, distZ, noiseFactor, noiseFactor, noiseFactor);
		octaveArr3 = octaves2.generateNoiseOctaves(null, posX, 0, posZ, distX, distY, distZ, noiseFactor, noiseFactor, noiseFactor);


		int incrementer1 = 0;
		int incrementer2 = 0;
		int threepTwo = 16 / 5;
		for (int i = 0; i < 5; ++i)
		{
			int var18 = i * threepTwo + threepTwo / 2;
			for (int j = 0; j < 5; ++j)
			{
				double var29;
				int var20 = j * threepTwo + threepTwo / 2;
				double temperatureV = temps[var18 * 16 + var20];
				double var23 = humidities[var18 * 16 + var20] * temperatureV;
				double var25 = 1.0 - var23;
				var25 *= var25;
				var25 *= var25;
				var25 = 1.0 - var25;
				double var27 = (scaleNoiseArr[incrementer2] + 256.0) / 512.0;
				if ((var27 *= var25) > 1.0)
				{
					var27 = 1.0;
				}
				if ((var29 = octaveArr7[incrementer2] / 8000.0) < 0.0)
				{
					var29 = (-var29) * 0.3;
				}
				if ((var29 = var29 * 3.0 - 2.0) < 0.0)
				{
					if ((var29 /= 2.0) < -1.0)
					{
						var29 = -1.0;
					}
					var29 /= 1.4;
					var29 /= 2.0;
					var27 = 0.0;
				}
				else
				{
					if (var29 > 1.0)
					{
						var29 = 1.0;
					}
					var29 /= 8.0;
				}
				if (var27 < 0.0)
				{
					var27 = 0.0;
				}
				var27 += 0.5;
				var29 = var29 * (double) 17 / 16.0;
				double var31 = (double) 17 / 2.0 + var29 * 4.0;
				++incrementer2;
				for (int k = 0; k < 17; ++k)
				{
					double var34;
					double var36 = ((double) k - var31) * 12.0 / var27;
					if (var36 < 0.0)
					{
						var36 *= 4.0;
					}
					double var38 = octaveArr2[incrementer1] / 512.0;
					double var40 = octaveArr3[incrementer1] / 512.0;
					double var42 = (octaveArr1[incrementer1] / 10.0 + 1.0) / 2.0;
					var34 = var42 < 0.0 ? var38 : (var42 > 1.0 ? var40 : var38 + (var40 - var38) * var42);
					var34 -= var36;
					if (k > 17 - 4)
					{
						double var44 = (float) (k - (17 - 4)) / 3.0f;
						var34 = var34 * (1.0 - var44) + -10.0 * var44;
					}
					values[incrementer1] = var34;
					++incrementer1;
				}
			}
		}
		return values;
	}

	// Possibly Ignore these
	@Override
	public double[] computeNoiseRange (int x, int z)
	{
		double[] rangeDouble = new double[2];
		rangeDouble[0] = 0; // Test
		rangeDouble[1] = 1;
		return rangeDouble;
	}

	// TEST
	@Override
	protected double computeNoiseFalloff (double depth, double scale, int y)
	{
		return 0;
	}

	@Override
	protected void sampleNoiseColumn (double[] buffer, int x, int z)
	{

	}

	// Clear cache
	@Override
	public void tick ()
	{
		groundCacheY.clear();
		biomeCacheY.clear();
	}
}
