package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RemindersListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val list = listOf(
        ReminderDataItem(
            title = "title",
            description = "description",
            latitude = (-360..360).random().toDouble(),
            longitude = (-360..360).random().toDouble(),
            location = "location"
        )
    )
    private val firstReminder = list[0]

    private val fakeDataSource = FakeDataSource()
    private val remindersListViewModel =
        RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)


    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun loadReminders_DataSourceWithTwoItems_CorrectReminderList() = runBlockingTest {
        val reminder1 = ReminderDTO("title1", "description1", "location1", 1.0, 1.0)
        val reminder2 = ReminderDTO("title2", "description2", "location2", 2.0, 2.0)
        fakeDataSource.addReminders(reminder1, reminder2)

        remindersListViewModel.loadReminders()

        assertEquals(remindersListViewModel.remindersList.value!![0].id, reminder1.id)
        assertEquals(remindersListViewModel.remindersList.value!![1].id, reminder2.id)
    }

    @Test
    fun loadReminders_DataSourceEmpty_EmptyListLiveDataIsTrue() {
        remindersListViewModel.loadReminders()

        val value = remindersListViewModel.showNoData.getOrAwaitValue()
        assertThat(value, `is`(true))
    }

    @Test
    fun loadReminders_DataSourceNoEmpty_EmptyListLiveDataIsFalse() = runBlockingTest {
        val reminder1 = ReminderDTO("title1", "description1", "location1", 1.0, 1.0)
        val reminder2 = ReminderDTO("title2", "description2", "location2", 2.0, 2.0)
        fakeDataSource.addReminders(reminder1, reminder2)

        remindersListViewModel.loadReminders()

        val value = remindersListViewModel.showNoData.getOrAwaitValue()
        assertThat(value, `is`(false))
    }

    @Test
    fun shouldReturnError() = runBlockingTest {
        val reminder1 = ReminderDTO("title1", "description1", "location1", 1.0, 1.0)
        val reminder2 = ReminderDTO("title2", "description2", "location2", 2.0, 2.0)
        fakeDataSource.addReminders(reminder1, reminder2)
        fakeDataSource.shouldSucceed = false

        remindersListViewModel.loadReminders()

        val value = remindersListViewModel.showSnackBar.getOrAwaitValue()
        assertThat(value, `is`("error message"))
    }

    @Test
    fun checkLoading() {
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }
}