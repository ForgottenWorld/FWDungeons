package it.forgottenworld.dungeons.core.config

import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStreamReader
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object Strings {

    private lateinit var stringResourceMap: Map<String, String>
    private val loadedResourceStrings = mutableSetOf<ResourceString>()

    private interface ResourceString : ReadOnlyProperty<Strings, String> { var value: String? }
    
    private fun resourceString() = object : ResourceString {
        override var value: String? = null

        override operator fun getValue(thisRef: Strings, property: KProperty<*>): String {
            if (value != null) return value!!
            value = stringResourceMap.getOrDefault(property.name, "STRING_${property.name}")
            loadedResourceStrings.add(this)
            return value!!
        }
    }

    fun load(plugin: FWDungeonsPlugin) {
        val stringsFile = File(plugin.dataFolder, "strings.yml")
        val conf = YamlConfiguration()
        if (!stringsFile.exists()) {
            YamlConfiguration().run {
                load(InputStreamReader(plugin.getResource("strings.it.yml")!!))
                save(File(plugin.dataFolder, "strings.it.yml"))
            }
            conf.load(InputStreamReader(plugin.getResource("strings.yml")!!))
            conf.save(stringsFile)
        } else {
            conf.load(stringsFile)
        }
        loadFromRes(conf)
    }

    private fun loadFromRes(conf: FileConfiguration) {
        for (rs in loadedResourceStrings) rs.value = null
        loadedResourceStrings.clear()
        stringResourceMap = conf.getKeys(false).associateWith { conf.getString(it) ?: "STRING_$it" }
    }

    const val CHAT_PREFIX = "§4F§6W§eD§f "
    const val CHAT_PREFIX_NO_SPACE = "§4F§6W§eD§f"
    const val CONSOLE_PREFIX = "§f[§4F§6W§eD§fungeons] "

    val ACTIVE_AREAS by resourceString()
    val ADVENTURERS_BROUGHT_BACK_TO_SAFETY_INST_RESET by resourceString()
    val ALREADY_EDITING_DUNGEON by resourceString()
    val ALREADY_IN_PARTY by resourceString()
    val ANOTHER_DUNGEON_WITH_SAME_NAME_EXISTS by resourceString()
    val CANT_WRITEOUT_YET_MISSING by resourceString()
    val CHEST_REMOVED_SUCCESFULLY by resourceString()
    val CONGRATS_YOU_MADE_IT_OUT by resourceString()
    val COULDNT_FIND_DUNGEON_TEST_INSTANCE by resourceString()
    val CREATE by resourceString()
    val CREATED_IE_WITH_ID by resourceString()
    val CREATED_NEW_DUNGEON_NOW_IN_EDIT_MODE by resourceString()
    val CURRENTLY_NOT_IN_DUNGEON_PARTY by resourceString()
    val DEBUG_ENTERED_TRIGGER by resourceString()
    val DEBUG_EXITED_TRIGGER by resourceString()
    val DELETED_IE_WITH_ID by resourceString()
    val DESCRIPTION by resourceString()
    val DIFFICULTY by resourceString()
    val DUNGEONS_CANT_HAVE_LESS_THAN_1_INSTANCE by resourceString()
    val DUNGEON_ALREADY_BEING_EDITED by resourceString()
    val DUNGEON_AND_INSTANCE_ID_SHOULD_BE_INT by resourceString()
    val DUNGEON_BOX_AND_STARTPOS_SHOULD_BE_SET_BEFORE_ADDING_IE by resourceString()
    val DUNGEON_BOX_POS_SET by resourceString()
    val DUNGEON_BOX_SET by resourceString()
    val DUNGEON_BOX_SHOULD_BE_SET_BEFORE_ADDING_STARTPOS by resourceString()
    val DUNGEON_BOX_SHOULD_BE_SET_BEFORE_SAVING_VOLUME_MAP by resourceString()
    val DUNGEON_DESCRIPTION_CHANGED by resourceString()
    val DUNGEON_DIFFICULTY_CHANGED by resourceString()
    val DUNGEON_DISCARDED by resourceString()
    val DUNGEON_EXPORTED by resourceString()
    val DUNGEON_ID_SHOULD_BE_INT by resourceString()
    val DUNGEON_IMPORTED by resourceString()
    val DUNGEON_INSTANCE_NOT_FOUND by resourceString()
    val DUNGEON_IS_NOT_DISABLED by resourceString()
    val DUNGEON_NAME_CHANGED by resourceString()
    val DUNGEON_PARTY_ALREADY_PRIVATE by resourceString()
    val DUNGEON_PARTY_ALREADY_PUBLIC by resourceString()
    val DUNGEON_PARTY_CREATED_TO_CLOSE_CLICK by resourceString()
    val DUNGEON_PARTY_IS_FULL by resourceString()
    val DUNGEON_PARTY_IS_PRIVATE_YOURE_NOT_INVITED by resourceString()
    val DUNGEON_PARTY_MEMBERS_HAVE_BEEN_TPED by resourceString()
    val DUNGEON_POINTS_CHANGED by resourceString()
    val DUNGEON_SAVED by resourceString()
    val DUNGEON_STARTPOS_SET by resourceString()
    val DUNGEON_VOLUME_MAP_COULNDT_BE_SAVED by resourceString()
    val DUNGEON_VOLUME_MAP_SAVED by resourceString()
    val DUNGEON_WILL_BE_EVACUATED by resourceString()
    val DUNGEON_WILL_BE_EVACUATED_BECAUSE by resourceString()
    val DUNGEON_WITH_ID_ALREADY_ACTIVE by resourceString()
    val DUNGEON_WITH_ID_HAS_NO_INSTANCES_IMPORT_IT by resourceString()
    val DUNGEON_WITH_ID_IS_BEING_EDITED by resourceString()
    val DUNGEON_WITH_ID_NOT_DISABLED by resourceString()
    val DUNGEON_WITH_ID_WAS_DISABLED by resourceString()
    val DUNGEON_WITH_ID_WAS_ENABLED by resourceString()
    val FIRST by resourceString()
    val FULL by resourceString()
    val GOOD_LUCK_OUT_THERE by resourceString()
    val HERE by resourceString()
    val HIGHLIGHTED_IE_WITH_ID by resourceString()
    val INSTANCE_ADDED by resourceString()
    val INSTANCE_HAS_STARTED_CANT_LEAVE_NOW by resourceString()
    val INVALID_ARG_AMOUNT_OF_POINTS_SHOULD_BE_INT by resourceString()
    val INVALID_ARG_POSSIBLE_ARGS by resourceString()
    val INVALID_DUNGEON_ID by resourceString()
    val INVALID_INSTANCE_ID by resourceString()
    val INVITE_SENT by resourceString()
    val IN_DUNGEON by resourceString()
    val JOIN by resourceString()
    val LABEL_CANNOT_BE_EMPTY by resourceString()
    val LEADERBOARD_DESCR by resourceString()
    val LEADERBOARD_POINTS by resourceString()
    val LEADERBOARD_TITLE by resourceString()
    val LOOKUP_RESULT by resourceString()
    val MAIN_HAND_MUST_BE_EMPTY by resourceString()
    val MIN_MAX_PLAYERS_SHOULD_BE_INT by resourceString()
    val NEA_PLEASE_PROVIDE_AMOUNT by resourceString()
    val NEA_PROVIDE_DESCRIPTION by resourceString()
    val NEA_PROVIDE_DIFFICULTY by resourceString()
    val NEA_PROVIDE_DUNGEON_ID by resourceString()
    val NEA_PROVIDE_LABEL by resourceString()
    val NEA_PROVIDE_MIN_MAX_PLAYERS by resourceString()
    val NEA_PROVIDE_NAME by resourceString()
    val NOT_EDITING_ANY_DUNGEONS by resourceString()
    val NOT_ENOUGH_PLAYERS_FOR_DUNGEON by resourceString()
    val NOW_PARTY_LEADER by resourceString()
    val NOW_EDITING_DUNGEON_WITH_ID by resourceString()
    val NOW_HOLDING_WAND_FOR_MAKING_IE by resourceString()
    val NO_ACTIVE_AREA_WITH_SUCH_ID by resourceString()
    val NO_CHESTS_YET by resourceString()
    val NO_CHEST_WITH_SUCH_ID by resourceString()
    val NO_DUNGEON_FOUND_WITH_ID by resourceString()
    val NO_EPEARLS_IN_THE_DUNGEON by resourceString()
    val NO_EPEARLS_OR_CHORUS_FRUIT_ALLOWED by resourceString()
    val NO_LONGER_EDITING_DUNGEON by resourceString()
    val NO_ONLINE_PLAYER_HAS_THIS_NAME by resourceString()
    val NO_TRIGGER_WITH_SUCH_ID by resourceString()
    val NTH_POS_SET_PICK_ANOTHER by resourceString()
    val NUMBER_OF_PLAYERS_CHANGED by resourceString()
    val ONLY_LEADER_MAY_CLOSE_PARTY by resourceString()
    val ONLY_LEADER_MAY_INVITE by resourceString()
    val ONLY_LEADER_MAY_OPEN_PARTY by resourceString()
    val ONLY_LEADER_MAY_START_INSTANCE by resourceString()
    val OUTSIDE_OF_DUNGEON_BOX by resourceString()
    val PARTY_HAS_ALREADY_ENTERED_DUNGEON by resourceString()
    val PARTY_NOW_PRIVATE_INVITE_WITH_OPEN_WITH by resourceString()
    val PLAYERS by resourceString()
    val PLAYER_DIED_IN_DUNGEON by resourceString()
    val PLAYER_INVITED_YOU_TO_JOIN_PARTY_CLICK by resourceString()
    val PLAYER_IS_NOT_IN_PARTY_OR_INSTANCE by resourceString()
    val PLAYER_JOINED_DUNGEON_PARTY by resourceString()
    val PLAYER_LEFT_DUNGEON_PARTY by resourceString()
    val PLAYER_NOT_FOUND by resourceString()
    val POTION_EFFECT_NOT_ALLOWED by resourceString()
    val PRIVATE by resourceString()
    val PROVIDE_BOTH_DUNGEON_AND_INSTANCE_ID by resourceString()
    val PROVIDE_DUNGEON_ID by resourceString()
    val PROVIDE_INDEX_OR_NO_INDEX_TO_REMOVE_LAST_INST by resourceString()
    val PROVIDE_NAME_OF_INVITEE by resourceString()
    val PROVIDE_PLAYER_NAME by resourceString()
    val PROVIDE_VALID_ACTIVE_AREA_ID by resourceString()
    val PROVIDE_VALID_TRIGGER_ID by resourceString()
    val RELOADING_DUNGEONS_AND_INSTANCES by resourceString()
    val RELOAD_WARNING by resourceString()
    val REMOVED_INSTANCE_AT_INDEX by resourceString()
    val ROOM by resourceString()
    val SECOND by resourceString()
    val SET_LABEL by resourceString()
    val TARGET_NOT_INSIDE_DUNGEON_BOX by resourceString()
    val THIS_DUNGEON_ALREADY_HAS_INSTANCES by resourceString()
    val THIS_DUNGEON_HAS_NO_IE_YET by resourceString()
    val TOGGLED_HIGHLIGHTED_FRAMES by resourceString()
    val TO_ACCEPT by resourceString()
    val TRIGGERS by resourceString()
    val TRIGGER_NOT_FOUND by resourceString()
    val WIM_AT_LEAST_ONE_ACTIVE_AREA by resourceString()
    val WIM_AT_LEAST_ONE_TRIGGER by resourceString()
    val WIM_BOX by resourceString()
    val WIM_STARTING_LOCATION by resourceString()
    val YOU by resourceString()
    val YOU_CANNOT_JOIN_A_DUNGEON_RIGHT_NOW by resourceString()
    val YOU_DIED_IN_THE_DUNGEON by resourceString()
    val YOU_JOINED_DUNGEON_PARTY by resourceString()
    val YOU_NEED_TO_BE_TARGETING by resourceString()
    val YOU_WILL_BE_EVACUATED by resourceString()
    val YOU_WILL_BE_EVACUATED_BECAUSE by resourceString()
    val YOU_WILL_BE_TPED_SHORTLY by resourceString()
    val YOU_WILL_EXIT_THE_DUNGEON_IN_5_SECS by resourceString()
    val YOU_WISH_YOU_COULD by resourceString()
    val PROVIDE_VALID_UNLOCKABLE_SERIES_ID by resourceString()
    val PROVIDE_VALID_UNLOCKABLE_ID by resourceString()
    val NO_PRESSURE_PLATE_BELOW_YOU by resourceString()
    val PRESSURE_PLATE_IS_NOT_BOUND by resourceString()
    val PRESSURE_PLATE_IS_NOW_BOUND by resourceString()
    val PRESSURE_PLATE_IS_BOUND_TO by resourceString()
    val REQUIREMENTS_NOT_MET by resourceString()
    val YOU_CANT_UNLOCK_YET by resourceString()
    val YOU_HAVENT_UNLOCKED_THIS_YET by resourceString()
    val PRESSURE_PLATE_HAS_BEEN_UNBOUND by resourceString()
}