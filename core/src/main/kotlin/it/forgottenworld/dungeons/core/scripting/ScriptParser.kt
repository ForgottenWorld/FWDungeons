package it.forgottenworld.dungeons.core.scripting

import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger

@Singleton
class ScriptParser {

    private object Keywords {
        const val TRIGGER_ENTRY_POINT = "@TRIGGER"
    }

    fun parseScript(dungeon: FinalDungeon, script: String): Map<Int, Trigger.Effect> {
        val noNewLines = ScriptingUtils.removeNewLines(script)
        val noWhiteSpace = ScriptingUtils.removeWhitespaceNotInQuotes(noNewLines)
        return parseTriggers(dungeon, noWhiteSpace.iterator())
    }

    private fun parseTriggers(dungeon: FinalDungeon, code: CharIterator): Map<Int, Trigger.Effect> {
        val result = mutableMapOf<Int, Trigger.Effect>()
        while (code.hasNext()) {
            when (ScriptingUtils.findKeyword(code, Keywords.TRIGGER_ENTRY_POINT)) {
                Keywords.TRIGGER_ENTRY_POINT -> {
                    val args = ScriptingUtils.parseArguments(code)
                    if (args.size != 1) throw ScriptingException("Expected trigger ID or label")
                    val triggerId = if (args[0].startsWith('"')) {
                        val label = args[0].trim('"')
                        dungeon
                            .triggers
                            .values
                            .find { it.label == label }
                            ?.id
                            ?: throw ScriptingException("No trigger found for label $label")
                    } else {
                        args[0].toIntOrNull() ?: throw ScriptingException("Invalid trigger ID")
                    }
                    val block = ScriptingUtils.parseBlock(code)
                    result[triggerId] = Trigger.Effect(GenericScope(dungeon).parse(block))
                }
                else -> return result
            }
        }
        return result
    }
}