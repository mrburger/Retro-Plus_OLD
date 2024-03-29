package com.mrburgerus.retroplus.world.noise.alpha;

import com.mrburgerus.retroplus.world.noise.AbstractOctavesGenerator;

import java.util.Random;

public class NoiseGeneratorOctavesAlpha extends AbstractOctavesGenerator
{
	public NoiseGeneratorOctavesAlpha(Random random, int boundIn)
	{
		super(boundIn);
		this.generatorCollection = new NoiseGeneratorPerlinAlpha[boundIn];

		for (int i = 0; i < boundIn; ++i) {
			this.generatorCollection[i] = new NoiseGeneratorPerlinAlpha(random);
		}
	}
}
