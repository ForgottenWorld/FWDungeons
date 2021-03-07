package it.forgottenworld.dungeons.core.config

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.storage.read
import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import org.bukkit.Bukkit
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Singleton
class Configuration @Inject constructor(
    private val plugin: FWDungeonsPlugin
) {

    private val loadedConfigProperties = mutableListOf<ConfigurationProperty<*>>()

    private interface ConfigurationProperty<T : Any> : ReadOnlyProperty<Configuration, T> {
        var value: T?
    }

    private inline fun <reified T : Any> configurationProperty(
        default: T? = null
    ) = object : ConfigurationProperty<T> {
        override var value: T? = null

        override fun getValue(thisRef: Configuration, property: KProperty<*>): T {
            if (value == null) {
                value = plugin.config.read {
                    get(property.name)
                        ?: default
                        ?: error("Value missing from config: ${property.name}")
                }
                loadedConfigProperties.add(this)
            }
            return value!!
        }
    }


    val debugMode by configurationProperty(false)


    val easyRankingIntegration by configurationProperty(false)

    var useEasyRanking = false


    val fwEchelonIntegration by configurationProperty(false)

    var useFWEchelon = false


    val vaultIntegration by configurationProperty(false)

    var useVault = false


    private val dungeonWorldName by configurationProperty<String>()

    val dungeonWorld get() = Bukkit.getWorld(dungeonWorldName)
        ?: error("Dungeon world not found!")


    fun reload() {
        loadedConfigProperties.forEach { it.value = null }
        loadedConfigProperties.clear()
    }
}