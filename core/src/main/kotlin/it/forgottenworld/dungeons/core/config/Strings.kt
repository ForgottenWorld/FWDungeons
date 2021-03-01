package it.forgottenworld.dungeons.core.config

import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStreamReader
import kotlin.reflect.KProperty

object Strings {

    lateinit var stringMap: Map<String, String>

    private val loadedResourceStrings = mutableSetOf<ResourceString>()

    private class ResourceString {

        var value: String? = null

        operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
            if (value != null) return value!!
            value = stringMap.getOrDefault(property.name, "STRING_${property.name}")
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
        stringMap = conf.getKeys(false).associateWith { conf.getString(it) ?: "STRING_$it" }
    }

    const val CHAT_PREFIX = "§4F§6W§eD§f "
    const val CHAT_PREFIX_NO_SPACE = "§4F§6W§eD§f"

    val ACTIVE_AREAS by ResourceString()
    val ADVENTURERS_BROUGHT_BACK_TO_SAFETY_INST_RESET by ResourceString()
    val ALREADY_EDITING_DUNGEON by ResourceString()
    val ALREADY_IN_PARTY by ResourceString()
    val ANOTHER_DUNGEON_WITH_SAME_NAME_EXISTS by ResourceString()
    val CANT_WRITEOUT_YET_MISSING by ResourceString()
    val CHEST_REMOVED_SUCCESFULLY by ResourceString()
    val CONGRATS_YOU_MADE_IT_OUT by ResourceString()
    val COULDNT_FIND_DUNGEON_TEST_INSTANCE by ResourceString()
    val CREATE by ResourceString()
    val CREATED_IE_WITH_ID by ResourceString()
    val CREATED_NEW_DUNGEON_NOW_IN_EDIT_MODE by ResourceString()
    val CURRENTLY_NOT_IN_DUNGEON_PARTY by ResourceString()
    val DEBUG_ENTERED_TRIGGER by ResourceString()
    val DEBUG_EXITED_TRIGGER by ResourceString()
    val DELETED_IE_WITH_ID by ResourceString()
    val DESCRIPTION by ResourceString()
    val DIFFICULTY by ResourceString()
    val DUNGEONS_CANT_HAVE_LESS_THAN_1_INSTANCE by ResourceString()
    val DUNGEON_ALREADY_BEING_EDITED by ResourceString()
    val DUNGEON_AND_INSTANCE_ID_SHOULD_BE_INT by ResourceString()
    val DUNGEON_BOX_AND_STARTPOS_SHOULD_BE_SET_BEFORE_ADDING_IE by ResourceString()
    val DUNGEON_BOX_POS_SET by ResourceString()
    val DUNGEON_BOX_SET by ResourceString()
    val DUNGEON_BOX_SHOULD_BE_SET_BEFORE_ADDING_STARTPOS by ResourceString()
    val DUNGEON_BOX_SHOULD_BE_SET_BEFORE_SAVING_VOLUME_MAP by ResourceString()
    val DUNGEON_DESCRIPTION_CHANGED by ResourceString()
    val DUNGEON_DIFFICULTY_CHANGED by ResourceString()
    val DUNGEON_DISCARDED by ResourceString()
    val DUNGEON_EXPORTED by ResourceString()
    val DUNGEON_ID_SHOULD_BE_INT by ResourceString()
    val DUNGEON_IMPORTED by ResourceString()
    val DUNGEON_INSTANCE_NOT_FOUND by ResourceString()
    val DUNGEON_IS_NOT_DISABLED by ResourceString()
    val DUNGEON_NAME_CHANGED by ResourceString()
    val DUNGEON_PARTY_ALREADY_PRIVATE by ResourceString()
    val DUNGEON_PARTY_ALREADY_PUBLIC by ResourceString()
    val DUNGEON_PARTY_CREATED_TO_CLOSE_CLICK by ResourceString()
    val DUNGEON_PARTY_IS_FULL by ResourceString()
    val DUNGEON_PARTY_IS_PRIVATE_YOURE_NOT_INVITED by ResourceString()
    val DUNGEON_PARTY_MEMBERS_HAVE_BEEN_TPED by ResourceString()
    val DUNGEON_POINTS_CHANGED by ResourceString()
    val DUNGEON_SAVED by ResourceString()
    val DUNGEON_STARTPOS_SET by ResourceString()
    val DUNGEON_VOLUME_MAP_COULNDT_BE_SAVED by ResourceString()
    val DUNGEON_VOLUME_MAP_SAVED by ResourceString()
    val DUNGEON_WILL_BE_EVACUATED by ResourceString()
    val DUNGEON_WILL_BE_EVACUATED_BECAUSE by ResourceString()
    val DUNGEON_WITH_ID_ALREADY_ACTIVE by ResourceString()
    val DUNGEON_WITH_ID_HAS_NO_INSTANCES_IMPORT_IT by ResourceString()
    val DUNGEON_WITH_ID_IS_BEING_EDITED by ResourceString()
    val DUNGEON_WITH_ID_NOT_DISABLED by ResourceString()
    val DUNGEON_WITH_ID_WAS_DISABLED by ResourceString()
    val DUNGEON_WITH_ID_WAS_ENABLED by ResourceString()
    val FIRST by ResourceString()
    val FULL by ResourceString()
    val GOOD_LUCK_OUT_THERE by ResourceString()
    val HERE by ResourceString()
    val HIGHLIGHTED_IE_WITH_ID by ResourceString()
    val INSTANCE_ADDED by ResourceString()
    val INSTANCE_HAS_STARTED_CANT_LEAVE_NOW by ResourceString()
    val INVALID_ARG_AMOUNT_OF_POINTS_SHOULD_BE_INT by ResourceString()
    val INVALID_ARG_POSSIBLE_ARGS by ResourceString()
    val INVALID_DUNGEON_ID by ResourceString()
    val INVALID_INSTANCE_ID by ResourceString()
    val INVITE_SENT by ResourceString()
    val IN_DUNGEON by ResourceString()
    val JOIN by ResourceString()
    val LABEL_CANNOT_BE_EMPTY by ResourceString()
    val LEADERBOARD_DESCR by ResourceString()
    val LEADERBOARD_POINTS by ResourceString()
    val LEADERBOARD_TITLE by ResourceString()
    val LOOKUP_RESULT by ResourceString()
    val MAIN_HAND_MUST_BE_EMPTY by ResourceString()
    val MIN_MAX_PLAYERS_SHOULD_BE_INT by ResourceString()
    val NEA_PLEASE_PROVIDE_AMOUNT by ResourceString()
    val NEA_PROVIDE_DESCRIPTION by ResourceString()
    val NEA_PROVIDE_DIFFICULTY by ResourceString()
    val NEA_PROVIDE_DUNGEON_ID by ResourceString()
    val NEA_PROVIDE_LABEL by ResourceString()
    val NEA_PROVIDE_MIN_MAX_PLAYERS by ResourceString()
    val NEA_PROVIDE_NAME by ResourceString()
    val NOT_EDITING_ANY_DUNGEONS by ResourceString()
    val NOT_ENOUGH_PLAYERS_FOR_DUNGEON by ResourceString()
    val NOW_PARTY_LEADER by ResourceString()
    val NOW_EDITING_DUNGEON_WITH_ID by ResourceString()
    val NOW_HOLDING_WAND_FOR_MAKING_IE by ResourceString()
    val NO_ACTIVE_AREA_WITH_SUCH_ID by ResourceString()
    val NO_CHESTS_YET by ResourceString()
    val NO_CHEST_WITH_SUCH_ID by ResourceString()
    val NO_DUNGEON_FOUND_WITH_ID by ResourceString()
    val NO_EPEARLS_IN_THE_DUNGEON by ResourceString()
    val NO_EPEARLS_OR_CHORUS_FRUIT_ALLOWED by ResourceString()
    val NO_LONGER_EDITING_DUNGEON by ResourceString()
    val NO_ONLINE_PLAYER_HAS_THIS_NAME by ResourceString()
    val NO_TRIGGER_WITH_SUCH_ID by ResourceString()
    val NTH_POS_SET_PICK_ANOTHER by ResourceString()
    val NUMBER_OF_PLAYERS_CHANGED by ResourceString()
    val ONLY_LEADER_MAY_CLOSE_PARTY by ResourceString()
    val ONLY_LEADER_MAY_INVITE by ResourceString()
    val ONLY_LEADER_MAY_OPEN_PARTY by ResourceString()
    val ONLY_LEADER_MAY_START_INSTANCE by ResourceString()
    val OUTSIDE_OF_DUNGEON_BOX by ResourceString()
    val PARTY_HAS_ALREADY_ENTERED_DUNGEON by ResourceString()
    val PARTY_NOW_PRIVATE_INVITE_WITH_OPEN_WITH by ResourceString()
    val PLAYERS by ResourceString()
    val PLAYER_DIED_IN_DUNGEON by ResourceString()
    val PLAYER_INVITED_YOU_TO_JOIN_PARTY_CLICK by ResourceString()
    val PLAYER_IS_NOT_IN_PARTY_OR_INSTANCE by ResourceString()
    val PLAYER_JOINED_DUNGEON_PARTY by ResourceString()
    val PLAYER_LEFT_DUNGEON_PARTY by ResourceString()
    val PLAYER_NOT_FOUND by ResourceString()
    val POTION_EFFECT_NOT_ALLOWED by ResourceString()
    val PRIVATE by ResourceString()
    val PROVIDE_BOTH_DUNGEON_AND_INSTANCE_ID by ResourceString()
    val PROVIDE_DUNGEON_ID by ResourceString()
    val PROVIDE_INDEX_OR_NO_INDEX_TO_REMOVE_LAST_INST by ResourceString()
    val PROVIDE_NAME_OF_INVITEE by ResourceString()
    val PROVIDE_PLAYER_NAME by ResourceString()
    val PROVIDE_VALID_ACTIVE_AREA_ID by ResourceString()
    val PROVIDE_VALID_TRIGGER_ID by ResourceString()
    val RELOADING_DUNGEONS_AND_INSTANCES by ResourceString()
    val RELOAD_WARNING by ResourceString()
    val REMOVED_INSTANCE_AT_INDEX by ResourceString()
    val ROOM by ResourceString()
    val SECOND by ResourceString()
    val SET_LABEL by ResourceString()
    val TARGET_NOT_INSIDE_DUNGEON_BOX by ResourceString()
    val THIS_DUNGEON_ALREADY_HAS_INSTANCES by ResourceString()
    val THIS_DUNGEON_HAS_NO_IE_YET by ResourceString()
    val TOGGLED_HIGHLIGHTED_FRAMES by ResourceString()
    val TO_ACCEPT by ResourceString()
    val TRIGGERS by ResourceString()
    val TRIGGER_NOT_FOUND by ResourceString()
    val WIM_AT_LEAST_ONE_ACTIVE_AREA by ResourceString()
    val WIM_AT_LEAST_ONE_TRIGGER by ResourceString()
    val WIM_BOX by ResourceString()
    val WIM_STARTING_LOCATION by ResourceString()
    val YOU by ResourceString()
    val YOU_CANNOT_JOIN_A_DUNGEON_RIGHT_NOW by ResourceString()
    val YOU_DIED_IN_THE_DUNGEON by ResourceString()
    val YOU_JOINED_DUNGEON_PARTY by ResourceString()
    val YOU_NEED_TO_BE_TARGETING by ResourceString()
    val YOU_WILL_BE_EVACUATED by ResourceString()
    val YOU_WILL_BE_EVACUATED_BECAUSE by ResourceString()
    val YOU_WILL_BE_TPED_SHORTLY by ResourceString()
    val YOU_WILL_EXIT_THE_DUNGEON_IN_5_SECS by ResourceString()
    val YOU_WISH_YOU_COULD by ResourceString()
}