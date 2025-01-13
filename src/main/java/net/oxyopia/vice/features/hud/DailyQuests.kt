package net.oxyopia.vice.features.hud

import net.minecraft.client.gui.DrawContext
import net.minecraft.item.Items
import net.oxyopia.vice.Vice
import net.oxyopia.vice.config.features.DailyQuestsStorage
import net.oxyopia.vice.data.Size
import net.oxyopia.vice.data.World
import net.oxyopia.vice.data.gui.HudElement
import net.oxyopia.vice.data.gui.Position
import net.oxyopia.vice.events.*
import net.oxyopia.vice.events.core.SubscribeEvent
import net.oxyopia.vice.utils.HudUtils.drawStrings
import net.oxyopia.vice.utils.ItemUtils.cleanName
import net.oxyopia.vice.utils.ItemUtils.getLore
import net.oxyopia.vice.utils.TimeUtils.formatDuration
import net.oxyopia.vice.utils.TimeUtils.timeDelta
import java.util.regex.Pattern
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

object DailyQuests : HudElement(
    "Daily Quests",
    Vice.storage.daily.questTrackerPos,
    { pos -> Vice.storage.daily.questTrackerPos = pos },
    enabled = { true },
    drawCondition = { true }
) {

    private val daily: DailyQuestsStorage = Vice.storage.daily
    private val DAILY_COOLDOWN = 24.hours

    @SubscribeEvent
    fun onHudRender(event: HudRenderEvent) {
        if (!canDraw()) return

        val list = mutableListOf("&&eDaily Quests:")

        for ((i, quest) in daily.quests.withIndex()) {
            if (quest.type.isEmpty()) continue

            val name = quest.type
            if (quest.inCooldown) {
                val diff = daily.quests[i].lastCompleted.timeDelta()
                val remainingTime = DAILY_COOLDOWN.inWholeSeconds - diff.inWholeSeconds
                val formatted = remainingTime.seconds.formatDuration()
                if (daily.quests[i].lastCompleted > -1) {
                    list.add("&&cNext in $formatted")
                } else {
                    daily.quests[i].inCooldown = false
                    list.add("&&4Error wait for tmr")
                }
                continue
            } else {

                val progress = daily.quests[i].progressMin
                val progressCap = parseProgressCap(name)

                val color = if (progress >= progressCap) "&&a" else "&&e"
                list.add("$color$name ($progress/$progressCap)")
            }
        }

        position.drawStrings(list, event.context)
    }

    private fun parseProgressCap(questName: String): Int {
        val matcher = Pattern.compile("\\d+").matcher(questName)
        return if (matcher.find()) matcher.group().toInt() else 1
    }

    @SubscribeEvent
    fun onInventoryOpen(event: ChestRenderEvent) {
        if (!World.Tower.isInWorld()) return
        if (!event.chestName.contains("DAILY QUESTS")) return

        for (i in 0 until 3) {
            val stack = event.slots[21 + i].stack
            if(stack.cleanName() != "Quest on Cooldown" || stack.item != Items.AIR) {
                daily.quests[i].type = stack.cleanName()

                val lore = stack.getLore()
                val progress = extractProgressFromLore(lore)
                daily.quests[i].progress = progress
            }
        }

        saveDailyData()
    }

    @SubscribeEvent
    fun onQuestSlotClick(event: SlotClickEvent) {
        if (!World.Tower.isInWorld()) return
        if (!event.chestName.contains("DAILY QUESTS")) return

        val clickedSlot = event.slotId
        if (clickedSlot !in 21..23) return

        val questIndex = clickedSlot - 21
        val quest = daily.quests[questIndex]
        if (quest.type == "Quest on Cooldown") return

        val progress = quest.progressMin
        val progressCap = quest.progressMax

        if (progress >= progressCap) {
            daily.quests[questIndex].type = ""
            daily.quests[questIndex].progress = mutableListOf(0, 0)
            daily.quests[questIndex].inCooldown = true
            daily.quests[questIndex].lastCompleted = System.currentTimeMillis()
            saveDailyData()
        }
    }

    private fun extractProgressFromLore(lore: List<String>): MutableList<Int> {
        val progressPattern = Pattern.compile("Progress:\\s*(\\d+)/(\\d+)")
        for (line in lore) {
            val matcher = progressPattern.matcher(line)
            if (matcher.find()) {
                return mutableListOf(matcher.group(1).toInt(), matcher.group(2).toInt())
            }
        }
        return mutableListOf(0, 0)
    }

    @SubscribeEvent
    fun onMobKill(event: EntityDeathEvent) {
        val entityName = event.entity.customName?.string ?: ""

        for ((i, quest) in daily.quests.withIndex()) {
            if (quest.type.contains(entityName)) {
                daily.quests[i].progress[0]++
                saveDailyData()
                break
            }
        }
    }

    @SubscribeEvent
    fun onSound(event: SoundEvent) {
        when {
            event.soundName == "block.amethyst_block.break" && event.pitch == 0.5f && event.volume == 9999.0f -> {
                updateQuestProgress("Amethyst Crystal")
            }
            event.soundName == "block.copper.break" && event.pitch == 0.5f && event.volume == 9999.0f -> {
                updateQuestProgress("Scrap Metal")
            }
            event.soundName == "block.glass.break" && event.pitch == 0.5f && event.volume == 9999.0f -> {
                updateQuestProgress("Office Light")
            }
            event.soundName == "block.wool.break" && event.pitch == 0.5f && event.volume == 9999.0f -> {
                updateQuestProgress("Carpet")
            }
            event.soundName == "item.shield.break" && event.pitch == 1.0f && event.volume == 9999.0f -> {
                updateQuestProgress("Rock Shard")
            }
        }
    }

    private fun updateQuestProgress(questName: String) {
        for ((i, quest) in daily.quests.withIndex()) {
            if (quest.type.contains(questName)) {
                daily.quests[i].progress[0]++
                Vice.storage.markDirty()
                break
            }
        }
    }

    private fun saveDailyData() {
        Vice.storage.markDirty()
    }

    override fun Position.drawPreview(context: DrawContext): Size {
        val list = listOf(
            "&&eDaily Quests:",
            "&&equest",
            "&&equest",
            "&&equest"
        )

        return position.drawStrings(list, context)
    }
}
