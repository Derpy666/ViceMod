package net.oxyopia.vice.mixin;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.oxyopia.vice.Vice;
import net.oxyopia.vice.events.EntityShouldRenderEvent;
import net.oxyopia.vice.utils.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer<T extends Entity> {
	@Inject(
		method = "shouldRender",
		at = @At("HEAD"),
		cancellable = true
	)
	private void onRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
		if (Utils.INSTANCE.getInDoomTowers()) {
			EntityShouldRenderEvent result = Vice.EVENT_MANAGER.publish(new EntityShouldRenderEvent(entity));

			if (result.isCanceled()) {
				cir.setReturnValue(false);
			}
		}
	}
}