package com.example.inventoryapp.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.inventoryapp.R
import android.content.ComponentName
import com.example.inventoryapp.view.MainActivity

class InventoryWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, manager: AppWidgetManager, appWidgetId: Int) {
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val saldoVisible = prefs.getBoolean("saldo_visible", false)
        val saldoActual = prefs.getString("saldo_actual", "0") ?: "0"

        val views = RemoteViews(context.packageName, R.layout.widget_inventory)

        // Mostrar/ocultar saldo
        val saldoText = if (saldoVisible) "$ $saldoActual" else "$ ****"
        views.setTextViewText(R.id.widget_balance, saldoText)

        // Cambiar icono del ojo
        val eyeIcon = if (saldoVisible) R.drawable.ic_eye_closed else R.drawable.ic_eye_open
        views.setImageViewResource(R.id.widget_eye_icon, eyeIcon)

        // Intent para alternar visibilidad
        val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = "TOGGLE_SALDO"
        }
        val togglePendingIntent = PendingIntent.getBroadcast(
            context, 0, toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_eye_icon, togglePendingIntent)

        // Intent para abrir la app
        val openIntent = Intent(context, MainActivity::class.java)
        val openPendingIntent = PendingIntent.getActivity(
            context, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_manage_icon, openPendingIntent)

        manager.updateAppWidget(appWidgetId, views)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, InventoryWidgetProvider::class.java))

        when (intent.action) {
            "TOGGLE_SALDO" -> {
                val visible = prefs.getBoolean("saldo_visible", false)
                prefs.edit().putBoolean("saldo_visible", !visible).apply()
                onUpdate(context, manager, ids)
            }
        }
    }
}