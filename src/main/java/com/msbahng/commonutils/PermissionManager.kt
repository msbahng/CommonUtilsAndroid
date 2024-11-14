package com.msbahng.commonutils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference

interface PermissionManagerInterface {
    fun rationale(description: String): PermissionManager
    fun request(vararg permission: Permission): PermissionManager
    fun checkPermission(callback: (Boolean) -> Unit)
    fun requestBackgroundLocationPermission()
}

class PermissionManager private constructor(private val activity: WeakReference<ComponentActivity>)
    : PermissionManagerInterface
{
    private val requiredPermissions = mutableListOf<Permission>()
    private var rationale: String? = null
    private var callback: (Boolean) -> Unit = {}
    private var detailedCallback: (Map<Permission,Boolean>) -> Unit = {}

    private val permissionCheck =
        activity.get()?.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { grantResults ->
            sendResultAndCleanUp(grantResults)
        }

    companion object {
        fun from(activity: ComponentActivity) = PermissionManager(WeakReference(activity))
    }

    override fun rationale(description: String): PermissionManager {
        rationale = description
        return this
    }

    override fun request(vararg permission: Permission): PermissionManager {
        requiredPermissions.addAll(permission)
        return this
    }

    override fun checkPermission(callback: (Boolean) -> Unit) {
        this.callback = callback
        handlePermissionRequest()
    }

    fun checkDetailedPermission(callback: (Map<Permission,Boolean>) -> Unit) {
        this.detailedCallback = callback
        handlePermissionRequest()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun requestBackgroundLocationPermission() {

        activity.get()?.let { activity ->
            val locationRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )

            if (locationRationale) {

                val requestKey = 1001

                AlertDialog.Builder(activity)
                    .setTitle("Permission required.")
                    .setMessage("Need Always Allow permission to notify when you leave working area without checking out.")
                    .setPositiveButton("OK") { _, _ ->
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                            requestKey
                        )
                    }
                    .create()
                    .show()
            }
        }
    }

    private fun handlePermissionRequest() {
        activity.get()?.let { activity ->
            when {
                areAllPermissionsGranted(activity) -> sendPositiveResult()
                shouldShowPermissionRationale(activity) -> displayRationale(activity)
                else -> requestPermissions()
            }
        }
    }

    private fun displayRationale(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle("Permission required")
            .setMessage(rationale ?: "Need permission to use this feature.")
            .setCancelable(false)
            .setPositiveButton("Confirm") { _, _ ->
                requestPermissions()
            }
            .show()
    }

    private fun sendPositiveResult() {
        sendResultAndCleanUp(getPermissionList().associate { it to true } )
    }

    private fun sendResultAndCleanUp(grantResults: Map<String, Boolean>) {
        callback(grantResults.all { it.value })
        detailedCallback(grantResults.mapKeys { Permission.from(it.key) })
        cleanUp()
    }

    private fun cleanUp() {
        requiredPermissions.clear()
        rationale = null
        callback = {}
        detailedCallback = {}
    }

    private fun requestPermissions() {
        permissionCheck?.launch(getPermissionList())
    }

    private fun areAllPermissionsGranted(activity: Activity) =
        requiredPermissions.all { it.isGranted(activity) }

    private fun shouldShowPermissionRationale(activity: Activity) =
        requiredPermissions.any { it.requiresRationale(activity) }

    private fun getPermissionList() =
        requiredPermissions.flatMap { it.permissions.toList() }.toTypedArray()

    private fun Permission.isGranted(activity: Activity) =
        permissions.all { hasPermission(activity, it) }

    private fun Permission.requiresRationale(activity: Activity) =
        permissions.any { activity.shouldShowRequestPermissionRationale(it) }

    private fun hasPermission(activity: Activity, permission: String) =
        ContextCompat.checkSelfPermission(
            activity,
            permission
        ) == PackageManager.PERMISSION_GRANTED
}