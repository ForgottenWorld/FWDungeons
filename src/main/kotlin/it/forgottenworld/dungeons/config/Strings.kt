package it.forgottenworld.dungeons.config

import org.bukkit.configuration.file.FileConfiguration
import kotlin.reflect.KProperty

object Strings {

    lateinit var map: Map<String, String>

    private class LazyString {
        
        var value: String? = null
        
        operator fun getValue(thisRef: Any?, property: KProperty<*>) = value 
                ?: map.getOrDefault(
                        property.name,
                        "STRING_${property.name}"
                ).also { value = it }
    }

    private fun lazyString() = LazyString()
    
    fun loadFromRes(conf: FileConfiguration) {
        map = conf.getKeys(false).associateWith { conf.getString(it) ?: "STRING_$it" }
    }

    const val CHAT_PREFIX = "§4F§6W§eD§f "

    val ACTIVE_AREAS by lazyString()
    val ADVENTURERS_BROUGHT_BACK_TO_SAFETY_INST_RESET by lazyString()
    val ALREADY_EDITING_DUNGEON by lazyString()
    val ALREADY_IN_PARTY by lazyString()
    val ANOTHER_DUNGEON_WITH_SAME_NAME_EXISTS by lazyString()
    val CANT_WRITEOUT_YET_MISSING by lazyString()
    val CONGRATS_YOU_MADE_IT_OUT by lazyString()
    val COULDNT_FIND_DUNGEON_TEST_INSTANCE by lazyString()
    val CREATE by lazyString()
    val CREATED_IE_WITH_ID by lazyString()
    val CREATED_NEW_DUNGEON_NOW_IN_EDIT_MODE by lazyString()
    val CURRENTLY_NOT_IN_DUNGEON_PARTY by lazyString()
    val DEBUG_ENTERED_TRIGGER by lazyString()
    val DEBUG_EXITED_TRIGGER by lazyString()
    val DELETED_IE_WITH_ID by lazyString()
    val DESCRIPTION by lazyString()
    val DIFFICULTY by lazyString()
    val DUNGEON_ALREADY_BEING_EDITED by lazyString()
    val DUNGEON_AND_INSTANCE_ID_SHOULD_BE_INT by lazyString()
    val DUNGEON_BOX_AND_STARTPOS_SHOULD_BE_SET_BEFORE_ADDING_IE by lazyString()
    val DUNGEON_BOX_POS_SET by lazyString()
    val DUNGEON_BOX_SET by lazyString()
    val DUNGEON_BOX_SHOULD_BE_SET_BEFORE_ADDING_STARTPOS by lazyString()
    val DUNGEON_DESCRIPTION_CHANGED by lazyString()
    val DUNGEON_DIFFICULTY_CHANGED by lazyString()
    val DUNGEON_DISCARDED by lazyString()
    val DUNGEON_EXPORTED by lazyString()
    val DUNGEON_ID_SHOULD_BE_INT by lazyString()
    val DUNGEON_IMPORTED by lazyString()
    val DUNGEON_INSTANCE_NOT_FOUND by lazyString()
    val DUNGEON_IS_NOT_DISABLED by lazyString()
    val DUNGEON_NAME_CHANGED by lazyString()
    val DUNGEON_PARTY_ALREADY_PRIVATE by lazyString()
    val DUNGEON_PARTY_ALREADY_PUBLIC by lazyString()
    val DUNGEON_PARTY_CREATED_TO_CLOSE_CLICK by lazyString()
    val DUNGEON_PARTY_IS_FULL by lazyString()
    val DUNGEON_PARTY_IS_PRIVATE_YOURE_NOT_INVITED by lazyString()
    val DUNGEON_PARTY_MEMBERS_HAVE_BEEN_TPED by lazyString()
    val DUNGEON_POINTS_CHANGED by lazyString()
    val DUNGEON_SAVED by lazyString()
    val DUNGEON_STARTPOS_SET by lazyString()
    val DUNGEON_WITH_ID_ALREADY_ACTIVE by lazyString()
    val DUNGEON_WITH_ID_DISABLED by lazyString()
    val DUNGEON_WITH_ID_HAS_NO_INSTANCES_IMPORT_IT by lazyString()
    val DUNGEON_WITH_ID_IS_BEING_EDITED by lazyString()
    val DUNGEON_WITH_ID_WAS_DISABLED by lazyString()
    val DUNGEON_WITH_ID_WAS_ENABLED by lazyString()
    val DUNGEONS_CANT_HAVE_LESS_THAN_1_INSTANCE by lazyString()
    val FIRST by lazyString()
    val FULL by lazyString()
    val GOOD_LUCK_OUT_THERE by lazyString()
    val HERE by lazyString()
    val HIGHLIGHTED_IE_WITH_ID by lazyString()
    val IN_DUNGEON by lazyString()
    val INSTANCE_ADDED by lazyString()
    val INSTANCE_HAS_STARTED_CANT_LEAVE_NOW by lazyString()
    val INVALID_ARG_AMOUNT_OF_POINTS_SHOULD_BE_INT by lazyString()
    val INVALID_ARG_POSSIBLE_ARGS by lazyString()
    val INVALID_DUNGEON_ID by lazyString()
    val INVALID_INSTANCE_ID by lazyString()
    val INVITE_SENT by lazyString()
    val JOIN by lazyString()
    val LEADERBOARD_DESCR by lazyString()
    val LEADERBOARD_POINTS by lazyString()
    val LEADERBOARD_TITLE by lazyString()
    val LOOKUP_RESULT by lazyString()
    val MAIN_HAND_MUST_BE_EMPTY by lazyString()
    val MIN_MAX_PLAYERS_SHOULD_BE_INT by lazyString()
    val NEA_PLEASE_PROVIDE_AMOUNT by lazyString()
    val NEA_PROVIDE_DESCRIPTION by lazyString()
    val NEA_PROVIDE_DIFFICULTY by lazyString()
    val NEA_PROVIDE_DUNGEON_ID by lazyString()
    val NEA_PROVIDE_LABEL by lazyString()
    val NEA_PROVIDE_MIN_MAX_PLAYERS by lazyString()
    val NEA_PROVIDE_NAME by lazyString()
    val NO_DUNGEON_FOUND_WITH_ID by lazyString()
    val NO_EPEARLS_IN_THE_DUNGEON by lazyString()
    val NO_EPEARLS_OR_CHORUS_FRUIT_ALLOWED by lazyString()
    val NO_LONGER_EDITING_DUNGEON by lazyString()
    val NO_ONLINE_PLAYER_HAS_THIS_NAME by lazyString()
    val NOT_EDITING_ANY_DUNGEONS by lazyString()
    val NOT_ENOUGH_PLAYERS_FOR_DUNGEON by lazyString()
    val NOT_PARTY_LEADER by lazyString()
    val NOW_EDITING_DUNGEON_WITH_ID by lazyString()
    val NOW_HOLDING_WAND_FOR_MAKING_IE by lazyString()
    val NTH_POS_SET_PICK_ANOTHER by lazyString()
    val NUMBER_OF_PLAYERS_CHANGED by lazyString()
    val ONLY_LEADER_MAY_CLOSE_PARTY by lazyString()
    val ONLY_LEADER_MAY_INVITE by lazyString()
    val ONLY_LEADER_MAY_OPEN_PARTY by lazyString()
    val ONLY_LEADER_MAY_START_INSTANCE by lazyString()
    val OUTSIDE_OF_DUNGEON_BOX by lazyString()
    val PARTY_HAS_ALREADY_ENTERED_DUNGEON by lazyString()
    val PARTY_NOW_PRIVATE_INVITE_WITH_OPEN_WITH by lazyString()
    val PLAYER_DIED_IN_DUNGEON by lazyString()
    val PLAYER_INVITED_YOU_TO_JOIN_PARTY_CLICK by lazyString()
    val PLAYER_IS_NOT_IN_PARTY_OR_INSTANCE by lazyString()
    val PLAYER_JOINED_DUNGEON_PARTY by lazyString()
    val PLAYER_LEFT_DUNGEON_PARTY by lazyString()
    val PLAYER_NOT_FOUND by lazyString()
    val PLAYERS by lazyString()
    val PRIVATE by lazyString()
    val PROVIDE_NAME_OF_INVITEE by lazyString()
    val PROVIDE_BOTH_DUNGEON_AND_INSTANCE_ID by lazyString()
    val PROVIDE_DUNGEON_ID by lazyString()
    val PROVIDE_INDEX_OR_NO_INDEX_TO_REMOVE_LAST_INST by lazyString()
    val PROVIDE_PLAYER_NAME by lazyString()
    val PROVIDE_VALID_ACTIVE_AREA_ID by lazyString()
    val PROVIDE_VALID_TRIGGER_ID by lazyString()
    val RELOAD_WARNING by lazyString()
    val RELOADING_DUNGEONS_AND_INSTANCES by lazyString()
    val REMOVED_INSTANCE_AT_INDEX by lazyString()
    val ROOM by lazyString()
    val SECOND by lazyString()
    val SET_LABEL by lazyString()
    val TARGET_NOT_INSIDE_DUNGEON_BOX by lazyString()
    val THIS_DUNGEON_ALREADY_HAS_INSTANCES by lazyString()
    val THIS_DUNGEON_HAS_NO_IE_YET by lazyString()
    val TOGGLED_HIGHLIGHTED_FRAMES by lazyString()
    val TO_ACCEPT by lazyString()
    val TRIGGER_NOT_FOUND by lazyString()
    val TRIGGERS by lazyString()
    val WIM_AT_LEAST_ONE_ACTIVE_AREA by lazyString()
    val WIM_AT_LEAST_ONE_TRIGGER by lazyString()
    val WIM_BOX by lazyString()
    val WIM_STARTING_LOCATION by lazyString()
    val YOU by lazyString()
    val YOU_DIED_IN_THE_DUNGEON by lazyString()
    val YOU_JOINED_DUNGEON_PARTY by lazyString()
    val YOU_NEED_TO_BE_TARGETING by lazyString()
    val YOU_WILL_BE_TPED_SHORTLY by lazyString()
    val YOU_WILL_EXIT_THE_DUNGEON_IN_5_SECS by lazyString()
    val YOU_WISH_YOU_COULD by lazyString()
}
