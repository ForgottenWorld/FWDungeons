package it.forgottenworld.dungeons.model

class Dungeon {
    var id = -1
    lateinit var name: String
    lateinit var instances: List<DungeonInstance>
}