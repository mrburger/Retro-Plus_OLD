package com.mrburgerus.retroplus;

import com.mrburgerus.retroplus.world.beta.BetaWorldType;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.level.LevelGeneratorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class RetroPlus implements ModInitializer
{
	/* FIELDS */
	// Constants
	public static final String MOD_NAME = "Retro+";
	public static final String MOD_RESOURCE = "retro_plus";
	public static final int SEA_LEVEL = 64;
	public static final int CHUNK_SIZE = 16;
	public static final int WORLD_HEIGHT = 256;
	// Chunk Generator Declarations
	public static LevelGeneratorType alphaLevelType = null;
	public static final Logger LOGGER = LogManager.getLogger();

	static LevelGeneratorType loadOnClientBeta;

	@Override
	public void onInitialize()
	{
		// Put something funny, okay?
		LOGGER.info("Woah! Vintage maps!");
		// Load on client
		loadOnClientBeta = BetaWorldType.WORLD_TYPE;
		// Register generator
		Registry.register(Registry.CHUNK_GENERATOR_TYPE, new Identifier(MOD_RESOURCE, BetaWorldType.LEVEL_TYPE_NAME).toString(), BetaWorldType.betaChunkGeneratorType);
	}
}
