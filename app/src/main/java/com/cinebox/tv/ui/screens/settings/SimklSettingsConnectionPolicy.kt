package com.cinebox.tv.ui.screens.settings

import com.cinebox.tv.data.simkl.SimklAuthState
import com.cinebox.tv.data.simkl.SimklConnectionMode

internal fun shouldRefreshMissingSimklIdentity(
    state: SimklAuthState,
    isRefreshBlocked: Boolean
): Boolean = state.isAuthenticated && state.username.isNullOrBlank() && !isRefreshBlocked

internal fun shouldCancelSimklPolling(mode: SimklConnectionMode): Boolean =
    mode == SimklConnectionMode.DISCONNECTED
