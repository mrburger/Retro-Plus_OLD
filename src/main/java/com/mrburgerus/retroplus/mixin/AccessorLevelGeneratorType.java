package com.mrburgerus.retroplus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.level.LevelGeneratorType;

// Credit to Climatic Biomes
@Mixin(LevelGeneratorType.class)
public interface AccessorLevelGeneratorType
{
	@Invoker("<init>")
	static LevelGeneratorType create(int id, String name)
	{
		throw new AssertionError("f");
	}
}