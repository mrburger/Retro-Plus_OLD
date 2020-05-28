package com.mrburgerus.retroplus.world.noise;

public abstract class AbstractOctavesGenerator implements IOctavesGenerator
{
	protected final int bound;
	protected IPerlinGenerator[] generatorCollection;

	/* Probably horrible Data Management, but I'm not a computer scientist! */
	protected AbstractOctavesGenerator (int boundIn)
	{
		this.bound = boundIn;
	}

	public double[] generateNoiseOctaves(double[] values, double xVal, double yValZero, double zVal, int size1, int size2, int size3, double var11, double var13, double var15)
	{
		if (values == null)
		{
			values = new double[size1 * size2 * size3];
		}
		else
		{
			for (int i = 0; i < values.length; i++)
			{
				values[i] = 0.0D;
			}
		}
		double divideByTwo = 1.0D;
		for (int i = 0; i < bound; i++)
		{
			generatorCollection[i].generate(values, xVal, yValZero, zVal, size1, size2, size3, var11 * divideByTwo, var13 * divideByTwo, var15 * divideByTwo, divideByTwo);
			divideByTwo /= 2.0D;
		}
		return values;
	}

	/* Used by Beta Methods */
	public double[] generateNoiseOctaves(double[] values, int posX, int posZ, int fiveVal, int seventeenVal, double var6, double var8, double multiplier)
	{
		return generateNoiseOctaves(values, posX, 10.0D, posZ, fiveVal, 1, seventeenVal, var6, 1.0D, var8);
	}
}
