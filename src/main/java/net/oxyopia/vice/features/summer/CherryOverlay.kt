package net.oxyopia.vice.features.summer

import net.minecraft.client.gui.DrawContext
import net.oxyopia.vice.Vice
import net.oxyopia.vice.data.Colors
import net.oxyopia.vice.data.World
import net.oxyopia.vice.data.gui.HudElement
import net.oxyopia.vice.data.gui.Position
import net.oxyopia.vice.events.ChatEvent
import net.oxyopia.vice.events.HudRenderEvent
import net.oxyopia.vice.events.core.SubscribeEvent
import net.oxyopia.vice.utils.HudUtils.drawTexts
import net.oxyopia.vice.utils.HudUtils.toText
import net.oxyopia.vice.utils.TimeUtils.formatDuration
import net.oxyopia.vice.utils.TimeUtils.timeDeltaUntil
import java.awt.Color
import kotlin.time.Duration.Companion.seconds

object CherryOverlay : HudElement("Cherry Overlay", Vice.storage.summer.cherryInfoPos){
    override fun shouldDraw(): Boolean = Vice.config.CHERRY_OVERLAY
    override fun drawCondition(): Boolean = World.Summer.isInWorld()

    @SubscribeEvent
    fun onChatMessage(event: ChatEvent) {
        if (!World.Summer.isInWorld()) return

        if (event.string.contains("Complete the orders which appear on the whiteboard correctly and in timely fashion. You have 3 lives, so make sure they count!")) {

            val totalSeconds = 3600
            val resetTime = System.currentTimeMillis() + totalSeconds.toLong() * 1000L + 1000L

            Vice.storage.summer.cherryReset = resetTime
            Vice.storage.markDirty()
        }
    }

    @SubscribeEvent
    fun onHudRender(event: HudRenderEvent) {
        if (!shouldDraw() || !drawCondition()) return

        val list = mutableListOf(
            "Ice Cream Minigame".toText(Color(114, 89, 255), bold = true),
        )

        val reset = Vice.storage.summer.cherryReset.timeDeltaUntil()

        if (reset.inWholeMilliseconds < 0) {
            list.add("Reset!".toText(Colors.ChatColor.Green))
        } else {
            list.add("Resets in ".toText(Colors.ChatColor.Grey).append(reset.formatDuration().toText(Color(114, 89, 255))))
        }


        position.drawTexts(list, event.context)
    }

    override fun Position.drawPreview(context: DrawContext): Pair<Float, Float> {
        val list = listOf(
            "Ice Cream Minigame".toText(Color(114, 89, 255), bold = true),
            "Resets in ".toText(Colors.ChatColor.Grey).append(362.seconds.formatDuration().toText(Color(114, 89, 255)))
        )

        return position.drawTexts(list, context)
    }

    override fun storePosition(position: Position) {
        Vice.storage.summer.cherryInfoPos = position
        Vice.storage.markDirty()
    }
}