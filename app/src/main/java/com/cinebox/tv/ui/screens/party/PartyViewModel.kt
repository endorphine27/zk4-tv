package com.cinebox.tv.ui.screens.party

import androidx.lifecycle.ViewModel
import com.cinebox.tv.core.party.PartyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PartyViewModel @Inject constructor(
    val party: PartyRepository,
) : ViewModel()
