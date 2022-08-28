package epicsquid.roots.test.mixin.preinit;

import com.google.common.collect.Maps;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumMap;
import java.util.Map;

@Mixin(NetworkRegistry.class)
public abstract class MNetworkRegistry {
	@Shadow
	private EnumMap<Side, Map<String, FMLEmbeddedChannel>> channels;
	@Inject(
			remap = false,
			method = "<init>",
			at = @At("TAIL")
	)
	private void inject(String par1, int par2, CallbackInfo ci) {
		channels.put(Side.BUKKIT, Maps.newConcurrentMap());
	}
}

