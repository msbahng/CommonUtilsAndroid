package com.msbahng.commonutils.compose

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

val pushEnterTransition: (@JvmSuppressWildcards
AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) = {
    slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(200)
    )
}

val popEnterTransition: (@JvmSuppressWildcards
AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) = {
    slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(200)
    )
}

val pushExitTransition: (@JvmSuppressWildcards
AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) = {
    slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(200)
    )
}

val popExitTransition: (@JvmSuppressWildcards
AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) = {
    slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(200)
    )
}