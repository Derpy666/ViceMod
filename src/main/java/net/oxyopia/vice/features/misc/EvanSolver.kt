package net.oxyopia.vice.features.misc

import net.oxyopia.vice.Vice
import net.oxyopia.vice.data.World
import net.oxyopia.vice.events.ChatEvent
import net.oxyopia.vice.events.core.SubscribeEvent
import net.oxyopia.vice.utils.HudUtils
import net.oxyopia.vice.utils.Utils

object EvanSolver {
	@SubscribeEvent
	fun onChat(event: ChatEvent) {
		if (!Vice.config.GLITCH_HQ_EVAN_SOLVER || !World.GlitchHQ.isInWorld()) return

		questions[event.string.lowercase()]?.let { isCorrect ->
			val text = if (isCorrect) {
				Utils.playSound("block.note_block.pling", 2f)
				"&&a&&lTRUE"
			} else {
				Utils.playSound("block.note_block.pling", 0.5f)
				"&&c&&lFALSE"
			}

			HudUtils.sendVanillaTitle(text, "")
		}
	}

	private val questions = mapOf(
		"Is the max amount of silver 900?" to true,
		"Is the Adventurer's Hook from World 5?" to true,
		"Does the World 11 Train spawn every 45m?" to true,
		"Is \"Quaking Quarry\" a real scrapped world?" to true,
		"Is World 9 called \"Glimpse\"" to true,
		"The answer is not False" to true,

		"Was DoomTowers made on the 6th May 2023?" to false,
		"Is Exonitas after World 12?" to false,
		"Is \"Shifty Shoots\" a real scrapped world?" to false,
		"Is the Blowpipe the first Ability Item?" to false,
		"Are there only 6 bosses in-game?" to false,
		"Does PPP (Boss 4) have 3 phases?" to false,
		"Is World 10 called \"Arcade\"" to false,
		"Is the Jelly NPC on Floor 5?" to false,
		"Is the max Carnage Level 4?" to false
	)
		get() = field.mapKeys { (key, _) -> key.lowercase() }
}