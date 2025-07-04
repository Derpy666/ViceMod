package net.oxyopia.vice.config

import com.google.gson.annotations.Expose
import net.oxyopia.vice.Vice
import net.oxyopia.vice.Vice.Companion.version
import net.oxyopia.vice.config.features.MiscStorage
import net.oxyopia.vice.config.features.worlds.ArenaStorage
import net.oxyopia.vice.config.features.worlds.AuxiliaryStorage
import net.oxyopia.vice.config.features.BossStorage
import net.oxyopia.vice.config.features.ExpeditionsStorage
import net.oxyopia.vice.config.features.ShatteredSectorStorage
import net.oxyopia.vice.config.features.event.EventStorage
import net.oxyopia.vice.config.features.worlds.CookingStorage
import net.oxyopia.vice.config.features.worlds.LostInTimeStorage
import net.oxyopia.vice.config.features.worlds.ShowdownStorage
import net.oxyopia.vice.config.features.worlds.SummerStorage
import java.io.File
import java.io.Reader
import java.io.Writer

class Storage : PersistentSave(File("./config/vice/storage.json")) {

	@Expose
	var isFirstUse = true

	@Expose
	var lastVersion: String = version.friendlyString

	@Expose
	var arenas = ArenaStorage()

	@Expose
	var auxiliary = AuxiliaryStorage()

	@Expose
	var bosses = BossStorage()

	@Expose
	var cooking = CookingStorage()

	@Expose
	var expeditions = ExpeditionsStorage()

	@Expose
	var event = EventStorage()

	@Expose
	var summer = SummerStorage()

	@Expose
	var showdown = ShowdownStorage()

	@Expose
	var lostInTime = LostInTimeStorage()

	@Expose
	var misc = MiscStorage()

	@Expose
	var shatteredSector = ShatteredSectorStorage()

	override fun canWrite() = !Vice.config.DEVMODE || Vice.devConfig.STORAGE_MARK_DIRTY

	override fun write(writer: Writer) {
		Vice.gson.toJson(this, writer)
	}

	override fun read(reader: Reader) {
		Vice.storage = Vice.gson.fromJson(reader, this.javaClass)
	}
}