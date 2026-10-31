package com.cinebox.tv.core.di

import com.cinebox.tv.data.simkl.AndroidSimklAuthStorage
import com.cinebox.tv.data.simkl.AndroidSimklSyncStorage
import com.cinebox.tv.data.simkl.SimklAuthStorage
import com.cinebox.tv.data.simkl.SimklApiSyncRemote
import com.cinebox.tv.data.simkl.SimklSyncRemote
import com.cinebox.tv.data.simkl.SimklSyncStorage
import com.cinebox.tv.core.tracking.TrackingLibraryProvider
import com.cinebox.tv.core.tracking.TrackingProvider
import com.cinebox.tv.data.repository.TraktTrackingLibraryProvider
import com.cinebox.tv.data.repository.TraktTrackingProvider
import com.cinebox.tv.data.simkl.SimklLibraryService
import com.cinebox.tv.core.tracking.TrackingHistoryWriter
import com.cinebox.tv.core.tracking.TrackingProgressProvider
import com.cinebox.tv.data.repository.TraktTrackingHistoryWriter
import com.cinebox.tv.data.repository.TraktTrackingProgressProvider
import com.cinebox.tv.data.simkl.SimklTrackingHistoryWriter
import com.cinebox.tv.data.simkl.SimklTrackingProgressProvider
import com.cinebox.tv.data.simkl.SimklTrackingProvider
import com.cinebox.tv.core.profile.ProfileScopedCredentialStore
import com.cinebox.tv.data.mdblist.MdbListTrackingHistoryWriter
import com.cinebox.tv.data.mdblist.MdbListTrackingProgressProvider
import com.cinebox.tv.data.mdblist.MdbListTrackingProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackingModule {
    @Binds
    @IntoSet
    abstract fun bindMdbListLibrary(provider: com.cinebox.tv.data.mdblist.MdbListTrackingLibraryProvider): TrackingLibraryProvider

    @Binds
    @IntoSet
    abstract fun bindMdbListProvider(provider: MdbListTrackingProvider): TrackingProvider

    @Binds
    @IntoSet
    abstract fun bindMdbListProgress(provider: MdbListTrackingProgressProvider): TrackingProgressProvider

    @Binds
    @IntoSet
    abstract fun bindMdbListHistory(writer: MdbListTrackingHistoryWriter): TrackingHistoryWriter

    @Binds
    @Singleton
    abstract fun bindSimklAuthStorage(storage: AndroidSimklAuthStorage): SimklAuthStorage

    @Binds
    @IntoSet
    abstract fun bindSimklProfileScopedCredentialStore(
        storage: AndroidSimklAuthStorage
    ): ProfileScopedCredentialStore

    @Binds
    @Singleton
    abstract fun bindSimklSyncStorage(storage: AndroidSimklSyncStorage): SimklSyncStorage

    @Binds
    @Singleton
    abstract fun bindSimklSyncRemote(remote: SimklApiSyncRemote): SimklSyncRemote

    @Binds
    @IntoSet
    abstract fun bindTraktTrackingLibraryProvider(
        provider: TraktTrackingLibraryProvider
    ): TrackingLibraryProvider

    @Binds
    @IntoSet
    abstract fun bindSimklTrackingLibraryProvider(
        provider: SimklLibraryService
    ): TrackingLibraryProvider

    @Binds
    @IntoSet
    abstract fun bindTraktTrackingProgressProvider(
        provider: TraktTrackingProgressProvider
    ): TrackingProgressProvider

    @Binds
    @IntoSet
    abstract fun bindSimklTrackingProgressProvider(
        provider: SimklTrackingProgressProvider
    ): TrackingProgressProvider

    @Binds
    @IntoSet
    abstract fun bindTraktTrackingHistoryWriter(
        writer: TraktTrackingHistoryWriter
    ): TrackingHistoryWriter

    @Binds
    @IntoSet
    abstract fun bindSimklTrackingHistoryWriter(
        writer: SimklTrackingHistoryWriter
    ): TrackingHistoryWriter

    @Binds
    @IntoSet
    abstract fun bindTraktTrackingProvider(
        provider: TraktTrackingProvider
    ): TrackingProvider

    @Binds
    @IntoSet
    abstract fun bindSimklTrackingProvider(
        provider: SimklTrackingProvider
    ): TrackingProvider
}
