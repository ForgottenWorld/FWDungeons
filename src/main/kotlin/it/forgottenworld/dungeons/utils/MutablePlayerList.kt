package it.forgottenworld.dungeons.utils

import org.bukkit.entity.Player
import java.util.UUID

class MutablePlayerList : MutableList<Player?> {

    private val list = mutableListOf<UUID>()

    override val size get() = list.size

    val uuids: List<UUID> get() = list

    override fun contains(element: Player?) = element
        ?.let { list.contains(it.uniqueId) } == true

    override fun containsAll(elements: Collection<Player?>) = list
        .containsAll(elements.mapNotNull { it?.uniqueId })

    override fun get(index: Int) = getPlayer(list[index])

    override fun indexOf(element: Player?) = element
        ?.let { list.indexOf(element.uniqueId) } ?: -1

    override fun isEmpty() = list.isEmpty()

    override fun iterator() = list
        .mapNotNull { getPlayer(it) }
        .toMutableList()
        .iterator()

    override fun lastIndexOf(element: Player?) = element
        ?.let { list.lastIndexOf(element.uniqueId) } ?: -1

    override fun add(element: Player?) = element
        ?.let { list.add(it.uniqueId) } == true

    override fun add(index: Int, element: Player?) {
        element?.let { list.add(index, it.uniqueId) }
    }

    override fun addAll(index: Int, elements: Collection<Player?>) =
        list.addAll(index, elements.mapNotNull { it?.uniqueId })

    override fun addAll(elements: Collection<Player?>) =
        list.addAll(elements.mapNotNull { it?.uniqueId })

    override fun clear() = list.clear()

    override fun listIterator() = list
        .map { getPlayer(it) }
        .toMutableList()
        .listIterator()

    override fun listIterator(index: Int) = list
        .map { getPlayer(it) }
        .toMutableList()
        .listIterator(index)

    override fun remove(element: Player?) = element
        ?.let { list.remove(it.uniqueId) } == true

    override fun removeAll(elements: Collection<Player?>) = list
        .removeAll(elements.mapNotNull { it?.uniqueId })

    override fun removeAt(index: Int): Player? = getPlayer(list.removeAt(index))

    override fun retainAll(elements: Collection<Player?>) = list
        .retainAll(elements.mapNotNull { it?.uniqueId })

    override fun set(index: Int, element: Player?) = element
        ?.also { list[index] = it.uniqueId }

    override fun subList(fromIndex: Int, toIndex: Int) = list
        .subList(fromIndex, toIndex)
        .map { getPlayer(it) }
        .toMutableList()

    companion object {
        fun of(vararg elements: Player) = MutablePlayerList().apply { addAll(elements) }
    }
}