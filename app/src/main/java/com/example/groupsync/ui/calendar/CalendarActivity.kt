package com.example.groupsync.ui.calendar

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupsync.databinding.ActivityCalendarBinding
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.util.TimeSpan
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.Duration
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.TemporalUnit



class CalendarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val config = EventConfig(showSubtitle = false, showTimeEnd = false)
        binding.weekView.eventConfig = config
        binding.weekView.setShowNowIndicator(true)

        // Assuming EventCreator.weekData exists and correctly creates a list of Event objects
        binding.weekView.addEvents(EventCreator.weekData)

        val nowEvent = Event.Single(
            id = 1337,
            date = LocalDate.now(),
            title = "Current hour",
            shortTitle = "Now",
            timeSpan = TimeSpan.of(LocalTime.now().truncatedTo(ChronoUnit.HOURS), Duration.ofHours(1)),
            backgroundColor = Color.RED,
            textColor = Color.WHITE
        )
        binding.weekView.addEvent(nowEvent)

        // Add an onClickListener for each event
        binding.weekView.setEventClickListener { eventView ->
            Toast.makeText(applicationContext, "Removing " + eventView.event.title, Toast.LENGTH_SHORT).show()
            // Ensure you have a way to remove the event from your data model as well
        }

        // Optional: Register a context menu to each event
        registerForContextMenu(binding.weekView)
    }

    // Override other lifecycle methods and functionality using binding to reference your views

    companion object {
        private const val TAG = "CalendarActivity"
    }
}
