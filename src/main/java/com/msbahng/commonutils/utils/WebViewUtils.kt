package com.msbahng.commonutils.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun openWebView(url: String, context: Context) {

    if (url.isEmpty()) {
        return
    }

    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}