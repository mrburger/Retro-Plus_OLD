package com.mrburgerus.retroplus.world.beta.gen;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.Set;

public class BetaBiomeSource extends BiomeSource
{
	/* FIELDS */
	// Constants
	// Allowed initial biomes. I'll have to update this.
	public static final Set<Biome> BIOMES = ImmutableSet.of(Biomes.PLAINS);

	/* CONSTRUCTORS */
	public BetaBiomeSource()
	{
		super(BIOMES);
	}

	// Get Biome for specified x, y, z coordinates
	@Override
	public Biome getBiomeForNoiseGen(int x, int y, int z)
	{
		return Biomes.PLAINS;
	}
}
