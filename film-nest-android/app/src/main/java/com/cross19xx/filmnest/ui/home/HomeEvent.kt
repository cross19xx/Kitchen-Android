package com.cross19xx.filmnest.ui.home

sealed interface HomeEvent {
    data class ShowMessage(val message: String) : HomeEvent
}
