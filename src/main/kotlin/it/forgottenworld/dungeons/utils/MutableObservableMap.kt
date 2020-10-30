package it.forgottenworld.dungeons.utils

import kotlin.reflect.KProperty

fun <K,V> observableMapOf(vararg entries: Pair<K,V>) = ObservableMap(entries.toMap().toMutableMap())
fun <K,V> observableMapOf(map: MutableMap<K,V>) = ObservableMap(map.toMap().toMutableMap())

class ObservableMap<K, V>(private val map: MutableMap<K, V>) : MutableMap<K, V> by map {

    private val observers = mutableListOf<MapObserver<K,V>>()

    fun addObserver(observer: MapObserver<K,V>) {
        observers.add(observer)
    }

    fun clearObservers() = observers.clear()

    override fun put(key: K, value: V): V? {
        val oldVal = map.put(key,value)
        notifyObservers()
        return oldVal
    }

    override fun putAll(from: Map<out K, V>) {
        map.putAll(from)
        notifyObservers()
    }

    override fun remove(key: K): V? {
        val oldVal = map.remove(key)
        notifyObservers()
        return oldVal
    }

    private fun notifyObservers() = observers.forEach { it.onChanged(map) }
}

interface MapObserver<K,V> {
    fun onChanged(map: Map<K,V>)
}

class MapObserverDelegate<K,V>(
        observed: ObservableMap<K, V>,
        private val beforeChange: ((Map<K, V>) -> Map<K,V>)? = null,
        private val afterChange: ((Map<K, V>) -> Unit)? = null
) : MapObserver<K,V> {

    init {
        observed.addObserver(this)
    }

    var current = mapOf<K,V>()

    override fun onChanged(map: Map<K,V>) {
        current = beforeChange?.invoke(map) ?: map
        afterChange?.invoke(current)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = current
}

fun <K,V> mapObserver(
        observed: ObservableMap<K, V>,
        beforeChange: ((Map<K, V>) -> Map<K, V>)? = null,
        afterChange: ((Map<K, V>) -> Unit)? = null
) = MapObserverDelegate(observed, beforeChange, afterChange)