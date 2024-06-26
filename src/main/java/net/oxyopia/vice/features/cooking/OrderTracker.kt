package net.oxyopia.vice.features.cooking

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.oxyopia.vice.Vice
import net.oxyopia.vice.data.World
import net.oxyopia.vice.data.gui.HudElement
import net.oxyopia.vice.data.gui.Position
import net.oxyopia.vice.events.HudRenderEvent
import net.oxyopia.vice.events.core.SubscribeEvent
import net.oxyopia.vice.utils.HudUtils.drawStrings

object OrderTracker : HudElement("Cooking Order Tracker", Vice.storage.cooking.orderTrackerPos, searchTerm = "order tracker") {
	private val requests get() = Vice.storage.cooking.totalBurgerRequests
	private val completions get() = Vice.storage.cooking.totalBurgersComplete
	private val mc = MinecraftClient.getInstance()

	override fun shouldDraw(): Boolean = Vice.config.COOKING_ORDER_TRACKER
	override fun drawCondition(): Boolean = World.Burger.isInWorld() && (mc.player?.y ?: 0.0) > 100.0

	@SubscribeEvent
	fun onHudRender(event: HudRenderEvent) {
		if (!Vice.config.COOKING_ORDER_TRACKER || !drawCondition()) return
		draw(position, event.context)
	}

	private fun draw(position: Position, context: DrawContext): Pair<Float, Float> {
		val bossOrderList = mutableListOf<String>()
		val list = mutableListOf(
			"&&b&&lCooking Order Tracker"
		)

		var totalOrders = 0
		var totalOrdersComp = 0

		requests.forEach {
			CookingOrder.getById(it.key)?.apply {
				val completions = completions.getOrDefault(it.key, 0)

				val percentageComplete = ((completions.toDouble() / it.value.toDouble()) * 100).toInt()
				val percentageColor = getPercentageColour(percentageComplete)

				val text = "&&a${displayName}&&7: &&a$completions&&f/${it.value} &&7($percentageColor$percentageComplete&&7%)"

				if (isBossOrder) {
					bossOrderList.add(text.replaceFirst("&&a", "&&5"))
				} else {
					list.add(text)
				}

				totalOrders += it.value
				totalOrdersComp += completions
			}
		}

		list.addAll(bossOrderList)
		list.add("")
		list.add("&&7Total Orders: &&a$totalOrdersComp&&f/$totalOrders")

		return position.drawStrings(list, context)
	}

	private fun getPercentageColour(percentage: Int): String {
		@Suppress("KotlinConstantConditions")
		return when {
			percentage >= 75 -> "&&a"
			percentage in 25..74 -> "&&e"
			percentage < 25 -> "&&c"
			else -> "&&8"
		}
	}

	override fun storePosition(position: Position) {
		Vice.storage.cooking.orderTrackerPos = position
		Vice.storage.markDirty()
	}

	override fun Position.drawPreview(context: DrawContext): Pair<Float, Float> {
		return draw(this, context)
	}

}