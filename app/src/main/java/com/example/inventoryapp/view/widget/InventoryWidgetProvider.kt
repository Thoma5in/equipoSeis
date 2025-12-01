package com.example.inventoryapp.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.inventoryapp.R
import android.content.ComponentName
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.inventoryapp.data.AppDatabase
import com.example.inventoryapp.repository.InventoryRepository
import com.example.inventoryapp.view.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

private val widgetScope = CoroutineScope(Dispatchers.IO)

class InventoryWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, manager: AppWidgetManager, appWidgetId: Int) {
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val productDao = AppDatabase.getDatabase(context).productoDao()
        val repository = InventoryRepository(productDao)

        val widgetModule = EntryPointAccessors.fromApplication(context, WidgetModule::class.java)
        val firebaseAuth = widgetModule.getFirebaseAuth()
        val isUserLoggedIn = firebaseAuth.currentUser != null

        widgetScope.launch {
            val totalValue = repository.getInventoryTotalValue() ?: 0.0
            val formattedTotal = String.format(Locale.getDefault(), "%,.2f", totalValue)
            prefs.edit().putString("saldo_actual", formattedTotal).apply()

            updateWidgetUI(context, manager, appWidgetId, prefs, isUserLoggedIn)
        }
    }

    private fun updateWidgetUI(
        context: Context,
        manager: AppWidgetManager,
        appWidgetId: Int,
        prefs: android.content.SharedPreferences,
        isUserLoggedIn: Boolean
    ) {
        val saldoVisible = prefs.getBoolean("saldo_visible", false) && isUserLoggedIn
        val saldoActual = prefs.getString("saldo_actual", "0") ?: "0"
        val views = RemoteViews(context.packageName, R.layout.widget_inventory)

        val saldoText = if (saldoVisible) "$ $saldoActual" else "$ ****"
        views.setTextViewText(R.id.widget_balance, saldoText)

        views.setViewVisibility(R.id.widget_eye_icon, View.VISIBLE)
        val eyeIcon = if (saldoVisible) R.drawable.ic_eye_closed else R.drawable.ic_eye_open
        views.setImageViewResource(R.id.widget_eye_icon, eyeIcon)

        val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = "TOGGLE_SALDO"
        }
        val togglePendingIntent = PendingIntent.getBroadcast(
            context, 0, toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_eye_icon, togglePendingIntent)


        val openIntent = Intent(context, LoginActivity::class.java)
        val openPendingIntent = PendingIntent.getActivity(
            context, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val manageAreaId = context.resources.getIdentifier("widget_manage_area", "id", context.packageName)
        val manageTargetId = if (manageAreaId != 0) manageAreaId else R.id.widget_manage_icon
        views.setOnClickPendingIntent(manageTargetId, openPendingIntent)

        Handler(Looper.getMainLooper()).post {
            manager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, InventoryWidgetProvider::class.java))

        when (intent.action) {
            "TOGGLE_SALDO" -> {
                val widgetModule = EntryPointAccessors.fromApplication(context, WidgetModule::class.java)
                val firebaseAuth = widgetModule.getFirebaseAuth()
                if (firebaseAuth.currentUser != null) {
                    val visible = prefs.getBoolean("saldo_visible", false)
                    prefs.edit().putBoolean("saldo_visible", !visible).apply()
                    onUpdate(context, manager, ids)
                }
            }
            "com.example.inventoryapp.ACTION_UPDATE_WIDGET" -> {
                onUpdate(context, manager, ids)
            }
        }
    }
}
