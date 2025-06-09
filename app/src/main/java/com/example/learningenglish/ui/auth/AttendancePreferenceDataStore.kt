package com.example.learningenglish.ui.auth

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

val Context.attendanceDataStore by preferencesDataStore(name = "attendance_prefs")

class AttendancePreferencesDataStore(private val context: Context) {
    companion object {
        private val CHECKED_DATES_KEY = stringSetPreferencesKey("checked_dates")
        private val TODAY_CHECKED_KEY = booleanPreferencesKey("today_checked")
    }

    fun getCheckedDates(): Flow<List<LocalDate>> = context.attendanceDataStore.data.map { prefs ->
        prefs[CHECKED_DATES_KEY]?.mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() } ?: emptyList()
    }

    suspend fun saveCheckedDates(dates: List<LocalDate>) {
        context.attendanceDataStore.edit { prefs ->
            prefs[CHECKED_DATES_KEY] = dates.map { it.toString() }.toSet()
        }
    }

    fun getTodayChecked(): Flow<Boolean> = context.attendanceDataStore.data.map { prefs ->
        prefs[TODAY_CHECKED_KEY] ?: false
    }

    suspend fun saveTodayChecked(checked: Boolean) {
        context.attendanceDataStore.edit { prefs ->
            prefs[TODAY_CHECKED_KEY] = checked
        }
    }

    suspend fun clearAll() {
        context.attendanceDataStore.edit { it.clear() }
    }
}