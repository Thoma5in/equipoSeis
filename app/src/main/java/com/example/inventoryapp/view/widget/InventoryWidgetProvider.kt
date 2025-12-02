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
import com.example.inventoryapp.di.WidgetEntryPoint
import com.example.inventoryapp.repository.FirestoreInventoryRepository
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
        val hiltEntryPoint = EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)
        val repository = hiltEntryPoint.firestoreInventoryRepository()
        val firebaseAuth = hiltEntryPoint.firebaseAuth()

        val isUserLoggedIn = firebaseAuth.currentUser != null

        widgetScope.launch {
            val products = repository.getAllProductsOnce()
            val totalValue = products.sumOf { it.precio * it.cantidad }
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
        val saldoVisible = prefs.getBoolean("saldo_visible_${appWidgetId}", false) && isUserLoggedIn
        val saldoActual = prefs.getString("saldo_actual", "0") ?: "0"
        val views = RemoteViews(context.packageName, R.layout.widget_inventory)

        val saldoText = if (saldoVisible) "$ $saldoActual" else "$ ****"
        views.setTextViewText(R.id.widget_balance, saldoText)

        views.setViewVisibility(R.id.widget_eye_icon, View.VISIBLE)
        val eyeIcon = if (saldoVisible) R.drawable.ic_eye_closed else R.drawable.ic_eye_open
        views.setImageViewResource(R.id.widget_eye_icon, eyeIcon)

        val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = "TOGGLE_SALDO"
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val togglePendingIntent = PendingIntent.getBroadcast(
            context, appWidgetId, toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_eye_icon, togglePendingIntent)


        val openIntent = Intent(context, LoginActivity::class.java)
        val openPendingIntent = PendingIntent.getActivity(
            context, appWidgetId + 1, openIntent,
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
        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        val hiltEntryPoint = EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)
        val firebaseAuth = hiltEntryPoint.firebaseAuth()

        when (intent.action) {
            "TOGGLE_SALDO" -> {
                if (firebaseAuth.currentUser != null) {
                    val visible = prefs.getBoolean("saldo_visible_${appWidgetId}", false)
                    prefs.edit().putBoolean("saldo_visible_${appWidgetId}", !visible).apply()
                    updateAppWidget(context, manager, appWidgetId)
                } else {
                    val loginIntent = Intent(context, LoginActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra("from_widget", true)
                    }
                    context.startActivity(loginIntent)
                }
            }
            "com.example.inventoryapp.ACTION_UPDATE_WIDGET" -> {
                val ids = manager.getAppWidgetIds(ComponentName(context, InventoryWidgetProvider::class.java))
                onUpdate(context, manager, ids)
            }
        }
    }
}
