package ru.slavapmk.journalTracker.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import ru.slavapmk.journalTracker.excelExporter.RenderData

class ExportWeekViewModel : ViewModel() {
    private suspend fun generateDay(
        context: Context,
        date: SimpleDate,
        group: String
    ): List<RenderData> {
        val result = mutableListOf<RenderData>()
        return result
    }
}