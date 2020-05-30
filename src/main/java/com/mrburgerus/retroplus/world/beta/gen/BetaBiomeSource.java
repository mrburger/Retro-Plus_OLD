package com.mrburgerus.retroplus.world.beta.gen;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mrburgerus.retroplus.RetroPlus;
import com.mrburgerus.retroplus.world.noise.beta.NoiseGeneratorOctavesBiome;
import net.minecraft.util.Pair;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class BetaBiomeSource extends BiomeSource
{
	/* FIELDS */
	// Constants
	// Allowed initial biomes. I'll have to update this.
	public static final Set<Biome> BIOMES = ImmutableSet.of(Biomes.SAVANNA, Biomes.TAIGA);
	public static final List<Biome> SPAWN_BIOMES = Lists.asList(Biomes.BEACH, new Biome[]{Biomes.SNOWY_BEACH});

	// Used for world gen
	private NoiseGeneratorOctavesBiome temperatureOctave;
	public double[] temperatures;
	private NoiseGeneratorOctavesBiome humidityOctave;
	public double[] humidities;
	private NoiseGeneratorOctavesBiome noiseOctave;
	public double[] noise;
	public final double scaleVal = (1.0D / 39.999999404);
	public final double mult = 2;
	// New
	public BetaChunkGenerator generator;

	/* CONSTRUCTORS */
	public BetaBiomeSource (long seed)
	{
		super(BIOMES);
		temperatureOctave = new NoiseGeneratorOctavesBiome(new Random(seed * 9871), 4);
		humidityOctave = new NoiseGeneratorOctavesBiome(new Random(seed * 39811), 4);
		noiseOctave = new NoiseGeneratorOctavesBiome(new Random(seed * 543321), 2);
	}

	@Override
	public List<Biome> getSpawnBiomes ()
	{
		return SPAWN_BIOMES;
	}

	// CALL FIRST PLEASE
	public void setChunkGenerator (BetaChunkGenerator chunkGenerator)
	{
		generator = chunkGenerator;
	}

	// Get Biome for specified x, y, z coordinates
	// X, Y, Z appear to be in Biome coordinates, which are 4-per-chunk instead of 16-per
	@Override
	public Biome getBiomeForNoiseGen (int biomeX, int biomeY, int biomeZ)
	{
		//int groundHeight = generator.getHeightOnGroundForBiome(biomeX, biomeZ);
	//	int groundHeight = generator.getHeightOnGround(biomeX << 2, biomeZ << 2, Heightmap.Type.WORLD_SURFACE_WG);
		int groundHeight = 65;
		return Biomes.SAVANNA;
	}

	// NEW, gets a PAIR of Noise fields
	// WORKS
	public Pair<double[], double[]> getNoiseFields (int startX, int startZ, int sizeXZ)
	{
		double[] temps = temperatureOctave.generateOctaves(null, (double) startX, (double) startZ, sizeXZ, sizeXZ, scaleVal, scaleVal, 0.25);
		double[] humids = humidityOctave.generateOctaves(null, (double) startX, (double) startZ, sizeXZ, sizeXZ, scaleVal * mult, scaleVal * mult, 0.3333333333333333);
		double[] nois = noiseOctave.generateOctaves(null, (double) startX, (double) startZ, sizeXZ, sizeXZ, 0.25, 0.25, 0.5882352941176471);
		int counter = 0;
		for (int x = 0; x < sizeXZ; ++x)
		{
			for (int z = 0; z < sizeXZ; ++z)
			{
				// REQUIRED
				double var9 = nois[counter] * 1.1 + 0.5;
				double oneHundredth = 0.01;
				double point99 = 1.0 - oneHundredth;
				double temperatureVal = (temps[counter] * 0.15 + 0.7) * point99 + var9 * oneHundredth;
				oneHundredth = 0.002;
				point99 = 1.0 - oneHundredth;
				double humidityVal = (humids[counter] * 0.15 + 0.5) * point99 + var9 * oneHundredth;
				temperatureVal = 1.0 - (1.0 - temperatureVal) * (1.0 - temperatureVal);
				temperatureVal = MathHelper.clamp(temperatureVal, 0.0, 1.0);
				humidityVal = MathHelper.clamp(humidityVal, 0.0, 1.0);
				temps[counter] = temperatureVal;
				humids[counter] = humidityVal;
				counter++;

			}
		}
		return new Pair(temps, humids);
	}

	// Testing
	public static ChunkPos biomeToChunkPos (int biomeX, int biomeZ)
	{
		int chunkX = biomeX >> 2;
		int chunkZ = biomeZ >> 2;
		return new ChunkPos(chunkX, chunkZ);
	}
}
