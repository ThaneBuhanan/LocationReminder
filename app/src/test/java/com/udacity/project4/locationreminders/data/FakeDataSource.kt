package com.udacity.project4.locationreminders.data

import androidx.annotation.VisibleForTesting
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeDataSource : ReminderDataSource {
    private val list = mutableListOf<ReminderDTO>()

    @VisibleForTesting
    var shouldSucceed: Boolean = true

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (!shouldSucceed) {
            return Result.Error("error message")
        }
        return try {
            Result.Success(list)
        } catch (e: Exception) {
            Result.Error(e.localizedMessage)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        list.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (!shouldSucceed) {
            return Result.Error("error message")
        }
        return try {
            Result.Success(list.first { it.id == id })
        } catch (e: Exception) {
            Result.Error(e.localizedMessage)
        }
    }

    override suspend fun deleteAllReminders() {
        list.clear()
    }
}