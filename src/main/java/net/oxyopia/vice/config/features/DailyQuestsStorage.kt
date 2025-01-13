package net.oxyopia.vice.config.features

import com.google.gson.annotations.Expose
import net.oxyopia.vice.data.gui.Position

class DailyQuestsStorage {

    @Expose
    var questTrackerPos: Position = Position(175f, 150f)

    @Expose
    var quests: MutableList<Quest> = mutableListOf(
        Quest(),
        Quest(),
        Quest()
    )

    data class Quest(
        @Expose var inCooldown: Boolean = false,
        @Expose var lastCompleted: Long = -1,
        @Expose var progress: MutableList<Int> = mutableListOf(0, 0),
        @Expose var type: String = "",
        @Expose var mob: String? = null,
        @Expose var block: String? = null
    ) {
        val progressMin: Int
            get() = progress.minOrNull() ?: 0

        val progressMax: Int
            get() = progress.maxOrNull() ?: 0
    }
}