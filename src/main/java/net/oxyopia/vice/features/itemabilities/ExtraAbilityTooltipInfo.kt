package net.oxyopia.vice.features.itemabilities

import net.oxyopia.vice.Vice
import net.oxyopia.vice.data.Colors
import net.oxyopia.vice.events.ItemTooltipEvent
import net.oxyopia.vice.events.core.SubscribeEvent
import net.oxyopia.vice.utils.HudUtils.toText
import net.oxyopia.vice.utils.ItemUtils.cleanName
import net.oxyopia.vice.utils.ItemUtils.getEquippedSets
import net.oxyopia.vice.utils.Utils
import java.awt.Color

object ExtraAbilityTooltipInfo {
	private const val DAMAGE_LINE_STARTER = "⚔ Damage: "
	private const val SET_LINE_STARTER = "♦ Set: "

	@SubscribeEvent
	fun onItemTooltip(event: ItemTooltipEvent) {
		if (!Vice.config.SHOW_EXTRA_ABILITY_INFO) return

		val itemName = event.stack.cleanName()
		val ability = ItemAbility.getByName(itemName) ?: return
		val itemColor = event.stack.name.siblings.firstOrNull()?.style?.color?.rgb?.let { Color(it) } ?: Colors.ChatColor.Aqua

		val cooldownLineIndex = event.lines.indexOfFirst {
			it.string.startsWith(DAMAGE_LINE_STARTER) || it.string.startsWith(SET_LINE_STARTER)
		}.coerceAtLeast(0) + 1
		event.lines.add(cooldownLineIndex, "\uD83D\uDD51 Cooldown".toText(itemColor).append("§7: ${ability.cooldown}s"))

		if (ability.setAmount > 0) {
			val setLineIndex = event.lines.indexOfFirst { it.string.startsWith(SET_LINE_STARTER) }.takeIf{ it >= 0 } ?: return
			val appliedSetAmount = Utils.getPlayer()?.getEquippedSets()?.getOrDefault(ability.set, 0) ?: 0

			var text = " §8(${appliedSetAmount}/${ability.setAmount})"
			if (ability.hasSetEquipped()){
				text += " §a↑ ${appliedSetAmount * 25}%"
			}
			event.lines[setLineIndex] = event.lines[setLineIndex].copy().append(text)
		}
	}
}