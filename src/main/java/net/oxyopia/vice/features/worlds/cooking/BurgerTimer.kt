package net.oxyopia.vice.features.worlds.cooking

import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.oxyopia.vice.Vice
import net.oxyopia.vice.data.Colors
import net.oxyopia.vice.data.World
import net.oxyopia.vice.events.BlockInteractEvent
import net.oxyopia.vice.events.ChatEvent
import net.oxyopia.vice.events.WorldRenderEvent
import net.oxyopia.vice.events.core.SubscribeEvent
import net.oxyopia.vice.utils.RenderUtils.drawString
import net.oxyopia.vice.utils.TimeUtils.formatShortDuration
import net.oxyopia.vice.utils.TimeUtils.timeDelta
import net.oxyopia.vice.utils.TimeUtils.timeDeltaUntil
import net.oxyopia.vice.utils.Utils
import kotlin.time.Duration.Companion.seconds

object BurgerTimer {
	private val BURN_TIME = 3.seconds
	private val timeRegex by lazy {
		Regex("You are now cooking a burger! Come back within (\\d+) seconds\\.")
	}

	internal var cooking: CookingTimerThing? = null
	private var lastRailClicked: BlockPos? = null

	@SubscribeEvent
	fun onChat(event: ChatEvent) {
		timeRegex.matchEntire(event.string)?.apply {
			val block = lastRailClicked ?: return
			val duration = groupValues[1].toIntOrNull() ?: return

			cooking = CookingTimerThing(block, System.currentTimeMillis() + duration * 1000L)
		}
	}

	@SubscribeEvent
	fun onClickStove(event: BlockInteractEvent) {
		if (event.block != Blocks.ACTIVATOR_RAIL || !World.Burger.isInWorld()) return

		lastRailClicked = event.hitResult.blockPos
	}

	@SubscribeEvent
	fun onWorldRender(event: WorldRenderEvent) {
		if (!Vice.config.COOKING_TIMER || !World.Burger.isInWorld() || (Utils.getPlayer()?.y ?: 0.0) <= 100.0) return

		val c = cooking ?: return
		val delta = c.completionTime.timeDelta()

		if (delta >= BURN_TIME) return
		if (delta > 0.seconds) {
			val formatted = (BURN_TIME - delta).formatShortDuration()
			event.drawString(c.pos.toCenterPos().add(0.0, 0.3, 0.0), "DONE", Colors.ChatColor.Green)
			event.drawString(c.pos.toCenterPos(), formatted, Colors.ChatColor.Red)
		} else {
			val formatted = c.completionTime.timeDeltaUntil().formatShortDuration()
			event.drawString(c.pos.toCenterPos().add(0.0, 0.3, 0.0), "COOKING", Colors.ChatColor.Yellow)
			event.drawString(c.pos.toCenterPos(), formatted, Colors.ChatColor.Yellow)
		}
	}

	data class CookingTimerThing(
		val pos: BlockPos,
		val completionTime: Long
	)
}