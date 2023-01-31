package de.salomax.currencies.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import de.salomax.currencies.R
import de.salomax.currencies.model.Currency
import de.salomax.currencies.repository.Database


class CurrencyWidgetProvider : AppWidgetProvider() {

    private lateinit var from: Currency
    private lateinit var to: Currency

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Toast.makeText(context, "Broadcast Received!", Toast.LENGTH_SHORT).show()
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids = appWidgetManager.getAppWidgetIds(
            ComponentName(
                context!!,
                CurrencyWidgetProvider::class.java
            )
        )
        onUpdate(context, appWidgetManager, ids)
    }


    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, CurrencyWidgetProvider::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val database = Database(context)
            database.getLastBaseCurrency().observeForever {
                from = it!!
            }
            database.getLastDestinationCurrency().observeForever {
                to = it!!
            }

            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.widget_layout
            ).apply {
                setOnClickPendingIntent(R.id.btnUpdate, pendingIntent)
                setTextViewCompoundDrawablesRelative(R.id.textFrom, from.flagId, 0, 0, 0)
                setTextViewText(R.id.textFrom, from.name)
                setTextViewText(R.id.textTo, to.name)
                setTextViewCompoundDrawablesRelative(R.id.textTo, to.flagId, 0, 0, 0)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}