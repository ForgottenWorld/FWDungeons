package it.forgottenworld.dungeons.core

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.assistedinject.FactoryModuleBuilder
import it.forgottenworld.dungeons.api.game.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.api.game.unlockables.Unlockable
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.config.StorageImpl
import it.forgottenworld.dungeons.core.game.dungeon.DungeonFactory
import it.forgottenworld.dungeons.core.game.instance.DungeonInstanceFactory
import it.forgottenworld.dungeons.core.game.instance.DungeonInstanceImpl
import it.forgottenworld.dungeons.core.game.interactiveregion.ActiveAreaFactory
import it.forgottenworld.dungeons.core.game.interactiveregion.ActiveAreaImpl
import it.forgottenworld.dungeons.core.game.unlockables.UnlockableFactory
import it.forgottenworld.dungeons.core.game.unlockables.UnlockableImpl


class MainModule(private val plugin: FWDungeonsPlugin) : AbstractModule() {
    fun createInjector(): Injector = Guice.createInjector(this)

    override fun configure() {
        bind(FWDungeonsPlugin::class.java).toInstance(plugin)
        bind(Storage::class.java).to(StorageImpl::class.java)

        install(
            FactoryModuleBuilder()
                .implement(Unlockable::class.java, UnlockableImpl::class.java)
                .build(UnlockableFactory::class.java)
        )

        install(
            FactoryModuleBuilder()
                .implement(ActiveArea::class.java, ActiveAreaImpl::class.java)
                .build(ActiveAreaFactory::class.java)
        )

        install(
            FactoryModuleBuilder()
                .implement(DungeonInstance::class.java, DungeonInstanceImpl::class.java)
                .build(DungeonInstanceFactory::class.java)
        )

        install(
            FactoryModuleBuilder()
                .build(DungeonFactory::class.java)
        )
    }
}