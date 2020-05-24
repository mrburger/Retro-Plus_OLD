package com.mrburgerus.retroplus.mixin;

import com.mrburgerus.retroplus.world.beta.BetaWorldType;
import net.minecraft.world.level.LevelGeneratorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelGeneratorType.class)
public class MixinLevelGeneratorType
{
	@Inject(at = @At("HEAD"), method = "getTypeFromName", cancellable = true)
	private static void getTypeFromName(String name, CallbackInfoReturnable<LevelGeneratorType> info)
	{
		if (name.equalsIgnoreCase(BetaWorldType.LEVEL_TYPE_NAME))
		{
			info.setReturnValue(BetaWorldType.WORLD_TYPE);
		}
	}
}
