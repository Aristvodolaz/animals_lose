package com.application.lose_animals.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import android.app.PendingIntent
import android.content.Intent
import android.widget.Toast
import com.application.lose_animals.MainActivity
import com.application.lose_animals.R

class AnimalWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_UPDATE_WIDGET = "com.application.lose_animals.UPDATE_WIDGET"
        const val ACTION_ADD_PERSON = "com.application.lose_animals.ADD_PERSON"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        when (intent.action) {
            ACTION_UPDATE_WIDGET -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    intent.component
                )
                onUpdate(context, appWidgetManager, appWidgetIds)
                Toast.makeText(context, "Список обновлен", Toast.LENGTH_SHORT).show()
            }
            ACTION_ADD_PERSON -> {
                Toast.makeText(context, "Сообщение о пропаже человека", Toast.LENGTH_SHORT).show()
                val mainIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(mainIntent)
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.animal_widget)

        // Настраиваем действие для кнопки добавления
        val addIntent = Intent(context, AnimalWidget::class.java).apply {
            action = ACTION_ADD_PERSON
        }
        val addPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            addIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_add_button, addPendingIntent)

        // Настраиваем действие для обновления виджета
        val updateIntent = Intent(context, AnimalWidget::class.java).apply {
            action = ACTION_UPDATE_WIDGET
        }
        val updatePendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            updateIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_title, updatePendingIntent)

        // Устанавливаем текст для демонстрации
        views.setTextViewText(R.id.widget_title, "Потерянные люди")
        views.setTextViewText(R.id.widget_description, "Нажмите для обновления списка")

        // Обновляем виджет
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
} 