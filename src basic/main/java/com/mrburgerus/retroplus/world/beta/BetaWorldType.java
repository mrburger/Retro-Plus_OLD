package com.mrburgerus.retroplus.world.beta;

import com.mrburgerus.retroplus.mixin.AccessorLevelGeneratorType;
import com.mrburgerus.retroplus.world.beta.gen.BetaChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.level.LevelGeneratorType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

// Simply a HOLDER & Providing class
// Created by me (originally), organized by valoeghese
public class BetaWorldType
{
	public static final String LEVEL_TYPE_NAME = "beta_plus";
	public static LevelGeneratorType WORLD_TYPE = getWorldType();
	public static ChunkGeneratorType<BetaChunkGeneratorConfig, BetaChunkGenerator> betaChunkGeneratorType = new BetaChunkGeneratorType().getChunkGeneratorType(BetaChunkGeneratorConfig::new);

	public static LevelGeneratorType getWorldType()
	{
		LevelGeneratorType worldType;
		int id = 7;
		Field types = null;

		for (Field f : LevelGeneratorType.class.getDeclaredFields())
		{
			if (f.getType() == LevelGeneratorType[].class)
			{
				types = f;
			}
		}

		if (types != null)
		{
			try
			{
				LevelGeneratorType newTypes[] = new LevelGeneratorType[LevelGeneratorType.TYPES.length + 1];

				System.arraycopy(LevelGeneratorType.TYPES, 0, newTypes, 0, LevelGeneratorType.TYPES.length);
				newTypes[newTypes.length - 1] = null;

				types.setAccessible(true);
				Field modifiers = Field.class.getDeclaredField("modifiers");
				modifiers.setAccessible(true);

				modifiers.setInt(types, types.getModifiers() & ~Modifier.FINAL);
				types.set(null, newTypes);
				id = LevelGeneratorType.TYPES.length - 1;
			}
			catch (IllegalAccessException | NoSuchFieldException e)
			{
				return null;
			}
		}
		else
		{
			return null;
		}
		try
		{
			worldType = AccessorLevelGeneratorType.create(id, LEVEL_TYPE_NAME);
			worldType.setCustomizable(false);
		}
		catch (Exception e)
		{
			return null;
		}

		return worldType;

	}
}
