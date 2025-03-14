package ru.slavapmk.journalTracker.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

object LocaleHelper {
    private val locales = listOf(Locale.ENGLISH, Locale("ru"))

    fun getStringForLocale(context: Context, resId: Int, locale: Locale): String {
        val res: Resources = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config).getText(resId).toString()
    }

    fun getAllVariations(context: Context, resId: Int): List<String> {
        return locales.map {
            getStringForLocale(
                context,
                resId,
                it
            )
        }
    }
}