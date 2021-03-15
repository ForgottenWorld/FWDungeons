package it.forgottenworld.dungeons.api.command

class BranchingCommandBuilderScope {
    private val bindings = mutableMapOf<String, CommandHandler>()

    private fun assignCommand(handle: String, command: CommandHandler) {
        bindings[handle] = command
    }

    operator fun String.plusAssign(other: CommandHandler) {
        assignCommand(this, other)
    }

    fun CommandHandler.bindTo(vararg handles: String) {
        for (h in handles) assignCommand(h, this)
    }

    fun buildBranchingCommand() = BranchingCommand(bindings)
}

inline fun branchingCommand(
    build: BranchingCommandBuilderScope.() -> Unit
): BranchingCommand {
    val scope = BranchingCommandBuilderScope()
    scope.build()
    return scope.buildBranchingCommand()
}