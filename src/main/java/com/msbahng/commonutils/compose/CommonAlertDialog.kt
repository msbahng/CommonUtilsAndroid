package com.msbahng.commonutils.compose

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

object Dialogs {

    @Composable
    fun CommonAlertDialog(
        titleText: String?,
        messageText: String,
        confirmText: String? = null,
        confirm: (() -> Unit)? = null,
        dismissText: String? = null,
        dismiss: (() -> Unit)? = null,
        setShowDialog: (Boolean) -> Unit
    ) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                titleText?.let { Text(text = it) }
            },
            text = {
                Text(messageText)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        setShowDialog(false)
                        confirm?.invoke()
                    }
                ) {
                    if (confirmText != null) {
                        Text(confirmText)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        setShowDialog(false)
                        dismiss?.invoke()
                    }
                ) {
                    if (dismissText != null) {
                        Text(dismissText)
                    }
                }
            }
        )
    }

}