// package it.forgottenworld.dungeons.core.command.edit.trigger

/*
class CmdTriggerCode @Inject constructor(
    private val codeParser: CodeParser,
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        val triggerId = args.getOrNull(0)?.toIntOrNull() ?: run {
            sender.sendPrefixedMessage(Strings.PROVIDE_VALID_TRIGGER_ID)
            return true
        }

        val trigger = dungeon.triggers[triggerId] ?: run {
            sender.sendPrefixedMessage(Strings.TRIGGER_NOT_FOUND)
            return true
        }

        val code = codeParser.beautify(trigger.effectCode)

        sender.sendPrefixedMessage("\n§7[ CODE ]")
        sender.sendMessage(code)

        return true
    }
}*/
