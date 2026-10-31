package com.cinebox.tv.domain.repository

import com.cinebox.tv.domain.model.LibraryEntry
import com.cinebox.tv.domain.model.LibraryEntryInput
import com.cinebox.tv.domain.model.LibraryListTab
import com.cinebox.tv.domain.model.LibrarySourceMode
import com.cinebox.tv.domain.model.ListMembershipChanges
import com.cinebox.tv.domain.model.ListMembershipSnapshot
import com.cinebox.tv.domain.model.LibraryListPrivacy
import com.cinebox.tv.core.tracking.TrackingMembershipApplyResult
import com.cinebox.tv.core.tracking.TrackingProviderId
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    val sourceMode: Flow<LibrarySourceMode>
    val isSyncing: Flow<Boolean>
    val libraryItems: Flow<List<LibraryEntry>>
    val listTabs: Flow<List<LibraryListTab>>
    val membershipListTabs: Flow<List<LibraryListTab>>

    fun isInLibrary(itemId: String, itemType: String): Flow<Boolean>
    fun isInWatchlist(itemId: String, itemType: String): Flow<Boolean>

    suspend fun toggleDefault(
        item: LibraryEntryInput,
        confirmedRemovalProviders: Set<TrackingProviderId> = emptySet()
    ): TrackingMembershipApplyResult
    suspend fun getMembershipSnapshot(item: LibraryEntryInput): ListMembershipSnapshot
    suspend fun applyMembershipChanges(
        item: LibraryEntryInput,
        changes: ListMembershipChanges,
        confirmedRemovalProviders: Set<TrackingProviderId> = emptySet()
    ): TrackingMembershipApplyResult

    suspend fun createPersonalList(
        name: String,
        description: String?,
        privacy: LibraryListPrivacy,
        source: LibrarySourceMode
    )

    suspend fun updatePersonalList(
        listId: String,
        name: String,
        description: String?,
        privacy: LibraryListPrivacy,
        source: LibrarySourceMode
    )

    suspend fun deletePersonalList(listId: String, source: LibrarySourceMode)
    suspend fun reorderPersonalLists(orderedListIds: List<String>, source: LibrarySourceMode)
    suspend fun refreshNow()
}
