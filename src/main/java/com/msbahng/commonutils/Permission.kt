package com.msbahng.commonutils

import android.Manifest

sealed class Permission(vararg val permissions: String) {
    // Individual permissions
    object Camera : Permission(Manifest.permission.CAMERA)

    object Notification : Permission(Manifest.permission.POST_NOTIFICATIONS)

    // Bundled permissions
    object MandatoryForFeatureOne : Permission(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Grouped permissions
    object Location : Permission(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    object Storage : Permission(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )


    companion object {
        fun from(permission: String) = when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION -> Location
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE -> Storage
            Manifest.permission.CAMERA -> Camera
            Manifest.permission.POST_NOTIFICATIONS -> Notification
            else -> throw IllegalArgumentException("Unknown permission: $permission")
        }
    }
}