package com.example.championcart.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

object NavigationUtils {

    fun openMapForNavigation(context: Context, address: String, storeName: String) {
        try {
            // Try Google Maps first with navigation mode
            val gmmIntentUri = Uri.parse("google.navigation:q=${Uri.encode(address)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
            }

            if (isAppInstalled(context, "com.google.android.apps.maps")) {
                context.startActivity(mapIntent)
            } else {
                // Fallback to any map app that can handle geo queries
                openWithAnyMapApp(context, address, storeName)
            }
        } catch (e: Exception) {
            // Final fallback - open in web browser
            openInBrowser(context, address)
        }
    }

    fun openWithAnyMapApp(context: Context, address: String, storeName: String) {
        try {
            // Use geo: URI scheme that works with any map app
            val geoUri = Uri.parse("geo:0,0?q=${Uri.encode("$storeName, $address")}")
            val intent = Intent(Intent.ACTION_VIEW, geoUri)

            // Check if there's an app that can handle this
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // No map app available, open in browser
                openInBrowser(context, address)
            }
        } catch (e: Exception) {
            openInBrowser(context, address)
        }
    }

    fun openInBrowser(context: Context, address: String) {
        try {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/${Uri.encode(address)}")
            )
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "לא ניתן לפתוח מפה", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWaze(context: Context, address: String) {
        try {
            val wazeUri = Uri.parse("waze://?q=${Uri.encode(address)}")
            val wazeIntent = Intent(Intent.ACTION_VIEW, wazeUri)
            context.startActivity(wazeIntent)
        } catch (e: Exception) {
            // Fallback to Waze web
            val webUri = Uri.parse("https://waze.com/ul?q=${Uri.encode(address)}")
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

    fun openMoovit(context: Context, address: String) {
        try {
            val moovitUri = Uri.parse("moovit://directions?dest_name=${Uri.encode(address)}")
            val moovitIntent = Intent(Intent.ACTION_VIEW, moovitUri)
            context.startActivity(moovitIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "לא ניתן לפתוח את Moovit", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}