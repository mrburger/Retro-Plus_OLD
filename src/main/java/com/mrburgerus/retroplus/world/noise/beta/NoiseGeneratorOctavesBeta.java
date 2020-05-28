package com.mrburgerus.retroplus.world.noise.beta;

import com.mrburgerus.retroplus.world.noise.AbstractOctavesGenerator;

import java.util.Random;

public class NoiseGeneratorOctavesBeta extends AbstractOctavesGenerator
{
	public NoiseGeneratorOctavesBeta(Random random, int boundIn)
	{
		super(boundIn);
		generatorCollection = new NoiseGeneratorPerlinBeta[bound];
		for (int i = 0; i < bound; i++)
		{
			generatorCollection[i] = new NoiseGeneratorPerlinBeta(random);
		}
	}

	public double[] generateNoiseOctaves(double[] doubles, int posX, int posZ, int fiveVal, int fiveVal2, double var6, double var8, double multiplier)
	{
		return generateNoiseOctaves(doubles, posX, 10.0D, posZ, fiveVal, 1, fiveVal2, var6, 1.0D, var8);
	}
}
