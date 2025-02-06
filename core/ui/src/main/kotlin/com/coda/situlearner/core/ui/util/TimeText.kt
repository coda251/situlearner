package com.coda.situlearner.core.ui.util

import kotlin.time.Duration.Companion.milliseconds

const val UndefinedTimeText = "--:--"

fun Long.asTimeText() = milliseconds.toComponents { hours, minutes, seconds, _ ->
    if (hours > 0) "%02d:%02d:%02d".format(hours, minutes, seconds)
    else "%02d:%02d".format(minutes, seconds)
}