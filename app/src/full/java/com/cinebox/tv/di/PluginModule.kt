package com.cinebox.tv.di

import com.cinebox.tv.core.auth.AuthManager
import com.cinebox.tv.core.plugin.PluginManager
import com.cinebox.tv.core.plugin.PluginRuntime
import com.cinebox.tv.core.plugin.cloudstream.ExternalExtensionLoader
import com.cinebox.tv.core.plugin.cloudstream.ExternalExtensionRunner
import com.cinebox.tv.core.plugin.cloudstream.ExternalRepoParser
import com.cinebox.tv.core.sync.PluginSyncService
import com.cinebox.tv.data.local.PluginDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PluginModule {

    @Provides
    @Singleton
    fun providePluginRuntime(): PluginRuntime {
        return PluginRuntime()
    }

    @Provides
    @Singleton
    fun providePluginManager(
        dataStore: PluginDataStore,
        runtime: PluginRuntime,
        pluginSyncService: PluginSyncService,
        authManager: AuthManager,
        externalRepoParser: ExternalRepoParser,
        externalExtensionLoader: ExternalExtensionLoader,
        externalExtensionRunner: ExternalExtensionRunner
    ): PluginManager {
        return PluginManager(
            dataStore, runtime, pluginSyncService, authManager,
            externalRepoParser, externalExtensionLoader, externalExtensionRunner
        )
    }
}
