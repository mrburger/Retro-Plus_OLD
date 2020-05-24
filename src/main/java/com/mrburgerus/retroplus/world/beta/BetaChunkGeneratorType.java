package com.mrburgerus.retroplus.world.beta;

import com.mrburgerus.retroplus.world.beta.gen.BetaBiomeSource;
import com.mrburgerus.retroplus.world.beta.gen.BetaChunkGenerator;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

// Climatic Chunk Generator approved!
public class BetaChunkGeneratorType implements InvocationHandler
{
	/* FIELDS */
	private Object factoryProxy;
	private Class<?> factoryClass;

	// Constructor hacks
	public BetaChunkGeneratorType()
	{
		String dev_name = "net.minecraft.world.gen.chunk.ChunkGeneratorFactory";
		String prod_name = "net.minecraft.class_2801";

		try
		{
			factoryClass = Class.forName(dev_name);
		}
		catch (ClassNotFoundException e1)
		{
			try
			{
				factoryClass = Class.forName(prod_name);
			}
			catch (ClassNotFoundException e2)
			{
				throw (new RuntimeException("Unable to find " + dev_name));
			}
		}
		factoryProxy = Proxy.newProxyInstance(factoryClass.getClassLoader(), new Class[]{factoryClass}, this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		// If this is a Beta Plus world
		if (args.length == 3 && args[0] instanceof World && args[1] instanceof BetaBiomeSource && args[2] instanceof BetaChunkGeneratorConfig)
		{
			return createProxy((World) args[0], (BetaBiomeSource) args[1], (BetaChunkGeneratorConfig) args[2]);
		}
		throw (new UnsupportedOperationException("Unknown Method: " + method.toString()));
	}

	// Create a chunk proxy
	public BetaChunkGenerator createProxy(World world, BetaBiomeSource biomeSource, BetaChunkGeneratorConfig generatorConfig)
	{
		return new BetaChunkGenerator(world, biomeSource, generatorConfig);
	}

	@SuppressWarnings("unchecked")
	public ChunkGeneratorType<BetaChunkGeneratorConfig, BetaChunkGenerator> getChunkGeneratorType(Supplier<BetaChunkGeneratorConfig> supplier) {
		Constructor<?>[] initlst = ChunkGeneratorType.class.getDeclaredConstructors();
		final Logger log = LogManager.getLogger("ChunkGenErr");

		for (Constructor<?> init : initlst) {
			init.setAccessible(true);
			if (init.getParameterCount() != 3) {
				continue; //skip
			}
			//lets try it
			try {
				return (ChunkGeneratorType<BetaChunkGeneratorConfig, BetaChunkGenerator>) init.newInstance(factoryProxy, true, supplier);
			} catch (Exception e) {
				log.error("Error in calling Chunk Generator Type", e);
			}
		}
		log.error("Unable to find constructor for ChunkGeneratorType");
		return null;
	}
}
