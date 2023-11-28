package net.oxyopia.vice.utils

import com.mojang.brigadier.exceptions.CommandSyntaxException
import gg.essential.universal.UChat
import gg.essential.universal.wrappers.message.UTextComponent
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.StringNbtReader
import net.minecraft.scoreboard.ScoreboardPlayerScore
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.oxyopia.vice.Vice

import kotlin.math.floor

object Utils {
	/**
	 * Use inDoomTowers() method instead, this is so dev mode bypassing is available
	 */
	var inDoomTowers = false
		get() = field || (Vice.config.DEVMODE && Vice.devConfig.BYPASS_INSTANCE_CHECK)

	var scoreboardData: Collection<ScoreboardPlayerScore> = emptyList()

	fun getWorld(): String? = Vice.client.world?.registryKey?.value?.path

	fun sendViceMessage(msg: String) {
		UChat.chat("${Vice.chatPrefix}${msg.replace("&&", "§")}")
	}

	fun sendViceMessage(msg: UTextComponent) {
		msg.text = "${Vice.chatPrefix}${msg.text}"
		UChat.chat(msg)
	}

	fun playSound(identifier: Identifier, pitch: Float, volume: Float) {
		try {
			Vice.client.soundManager.play(PositionedSoundInstance.master(SoundEvent.of(identifier), pitch, volume))
		} catch (err: Exception) {
			DevUtils.sendErrorMessage(err, "An error occurred attempting to play a sound")
		}
	}

	fun sendVanillaTitle(title: String, subtitle: String, stayTime: Float = 1f, fadeinout: Float = 0.25f) {
		Vice.client.inGameHud.setSubtitle(Text.of(subtitle.replace("&&", "§")))
		Vice.client.inGameHud.setTitle(Text.of(title.replace("&&", "§")))
		Vice.client.inGameHud.setTitleTicks((20 * fadeinout).toInt(), (20 * stayTime).toInt(), (20 * fadeinout).toInt())
	}

	/**
	 * Formats a duration as dd:hh:MM:SS
	 * @param ms Time in Milliseconds
	 */
	fun formatDuration(ms: Long, showMs: Boolean): String {
		val hours = floor(ms.toDouble() / (1000 * 60 * 60)).toLong()
		val mins = floor((ms / (1000 * 60)).toDouble() % 60).toLong()
		val secs = floor((ms / 1000).toDouble() % 60).toLong()
		val millis = ms % 1000

		return buildString {
			if (hours > 0) append(String.format("%02d:", hours))
			append(String.format("%02d:%02d", mins, secs))
			if (showMs) append(String.format(".%03d", millis))
		}
	}

	fun parseNbt(nbt: String): NbtCompound? {
		try {
			return StringNbtReader.parse(nbt)
		} catch (e: CommandSyntaxException) {
			DevUtils.sendErrorMessage(e, "An error occurred parsing an item's NBT!")
		}
		return null
	}
}