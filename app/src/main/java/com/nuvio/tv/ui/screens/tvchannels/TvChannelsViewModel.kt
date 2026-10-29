package com.nuvio.tv.ui.screens.tvchannels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.core.network.NetworkResult
import com.nuvio.tv.domain.model.Addon
import com.nuvio.tv.domain.model.CatalogDescriptor
import com.nuvio.tv.domain.model.CatalogRow
import com.nuvio.tv.domain.model.enabledAddons
import com.nuvio.tv.domain.model.skipStep
import com.nuvio.tv.domain.model.supportsExtra
import com.nuvio.tv.domain.repository.AddonRepository
import com.nuvio.tv.domain.repository.CatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TvChannel(
    val id: String,
    val type: String,
    val name: String,
    val logo: String?,
    /** Running programme, e.g. "10:30–13:00 · Show · 5 min left" (taken from the start of the description). */
    val programme: String?,
    val addonBaseUrl: String,
)

data class TvChannelsUiState(
    val isLoading: Boolean = true,
    val channels: List<TvChannel> = emptyList(),
    val favorites: Set<String> = emptySet(),
)

/** Channels of every enabled addon catalog of type "tv", independent of what the home screen shows. */
@HiltViewModel
class TvChannelsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val addonRepository: AddonRepository,
    private val catalogRepository: CatalogRepository,
) : ViewModel() {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _state = MutableStateFlow(
        TvChannelsUiState(favorites = prefs.getStringSet(KEY_FAVORITES, emptySet()).orEmpty().toSet())
    )
    val state: StateFlow<TvChannelsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            addonRepository.getInstalledAddons().collectLatest { addons -> loadChannels(addons.enabledAddons()) }
        }
    }

    fun toggleFavorite(channelId: String) {
        val updated = _state.value.favorites.let { if (channelId in it) it - channelId else it + channelId }
        prefs.edit().putStringSet(KEY_FAVORITES, updated).apply()
        _state.update { it.copy(favorites = updated) }
    }

    private suspend fun loadChannels(addons: List<Addon>) {
        _state.update { it.copy(isLoading = true) }
        val channels = mutableListOf<TvChannel>()
        for (addon in addons) {
            for (catalog in addon.catalogs) {
                if (!catalog.apiType.equals("tv", ignoreCase = true) || catalog.isSearchOnlyCatalog()) continue
                channels += loadCatalog(addon, catalog)
            }
        }
        val distinct = channels.distinctBy { it.id }
        TvChannelRegistry.channels = distinct
        _state.update { it.copy(isLoading = false, channels = distinct) }
    }

    private suspend fun loadCatalog(addon: Addon, catalog: CatalogDescriptor): List<TvChannel> {
        val supportsSkip = catalog.supportsExtra("skip")
        val step = catalog.skipStep()
        val result = mutableListOf<TvChannel>()
        var skip = 0
        repeat(MAX_PAGES) {
            val row = fetchPage(addon, catalog, skip, step, supportsSkip) ?: return result
            row.items.forEach { item ->
                result += TvChannel(
                    id = item.id,
                    type = item.rawType,
                    name = item.name,
                    logo = item.poster ?: item.logo ?: item.background,
                    programme = currentProgramme(item.description),
                    addonBaseUrl = addon.baseUrl,
                )
            }
            if (!supportsSkip || !row.hasMore || row.items.isEmpty()) return result
            skip += step
        }
        return result
    }

    private suspend fun fetchPage(
        addon: Addon,
        catalog: CatalogDescriptor,
        skip: Int,
        step: Int,
        supportsSkip: Boolean,
    ): CatalogRow? {
        val outcome = catalogRepository.getCatalog(
            addonBaseUrl = addon.baseUrl,
            addonId = addon.id,
            addonName = addon.displayName,
            catalogId = catalog.id,
            catalogName = catalog.name,
            type = catalog.apiType,
            skip = skip,
            skipStep = step,
            supportsSkip = supportsSkip,
        ).first { it !is NetworkResult.Loading }
        return (outcome as? NetworkResult.Success)?.data
    }

    private fun currentProgramme(description: String?): String? {
        val text = description?.trim().orEmpty()
        if (!text.startsWith("🔴")) return null
        val head = text.removePrefix("🔴").trim().removePrefix("ACUM").trim()
        return head.substringBefore(" — ").trim().takeIf { it.isNotEmpty() }
    }

    private companion object {
        const val PREFS_NAME = "cinebox_tv_channels"
        const val KEY_FAVORITES = "favorites"
        const val MAX_PAGES = 6
    }
}

/** The channels in display order, so the player can switch to the next or previous one. */
object TvChannelRegistry {
    @Volatile
    var channels: List<TvChannel> = emptyList()
}
