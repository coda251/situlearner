package com.coda.situlearner.feature.home.entry

import androidx.lifecycle.ViewModel
import com.coda.situlearner.core.data.repository.AppVersionRepository

class HomeViewModel(
    appVersionRepository: AppVersionRepository
) : ViewModel() {

    val appVersionState = appVersionRepository.appVersionState
}