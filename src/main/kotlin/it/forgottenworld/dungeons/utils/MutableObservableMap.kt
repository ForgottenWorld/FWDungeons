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
        observers.forEach { it.onPut(key to value) }
        return oldVal
    }

    override fun putAll(from: Map<out K, V>) {
        map.putAll(from)
        observers.forEach { it.onPutAll(map) }
    }

    override fun remove(key: K): V? {
        val oldVal = map.remove(key)
        observers.forEach { it.onRemove(key) }
        return oldVal
    }
}

interface MapObserver<K,V> {
    fun onPutAll(map: Map<K,V>)
    fun onPut(entry: Pair<K,V>)
    fun onRemove(key: K)
}

class MapObserverDelegate<K,V>(
        observed: ObservableMap<K, V>,
        private val processPut: ((Pair<K,V>) -> Pair<K,V>)? = null,
        private val onPut: ((Map<K, V>) -> Unit)? = null,
        private val onRemove: ((K) -> Unit)? = null
) : MapObserver<K,V> {

    private var current = observed.toMap()

    init {
        observed.addObserver(this)
    }

    override fun onPut(entry: Pair<K, V>) {
        current = current + (processPut?.invoke(entry) ?: entry)
        onPut?.invoke(current)
    }

    override fun onPutAll(map: Map<K,V>) {
        current = current + map.entries.map { (k,v) -> processPut?.invoke(k to v) ?: k to v }
        onPut?.invoke(current)
    }

    override fun onRemove(key: K) {
        current = current.filterKeys { it != key }
        onRemove?.invoke(key)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = current
}

fun <K,V> mapObserver(
        observed: ObservableMap<K, V>,
        processPut: ((Pair<K,V>) -> Pair<K,V>)? = null,
        onPut: ((Map<K, V>) -> Unit)? = null,
        onRemove: ((K) -> Unit)? = null
) = MapObserverDelegate(observed, processPut, onPut, onRemove)