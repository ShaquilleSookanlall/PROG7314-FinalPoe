package com.st10140587.prog7314_poe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.st10140587.prog7314_poe.data.local.LocalCache
import com.st10140587.prog7314_poe.data.local.LocationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationsActivity : AppCompatActivity() {
    // 🔹 Make MainActivity use the saved language
    override fun attachBaseContext(newBase: Context) {
        val wrapped = LocaleUtils.wrapContext(newBase)
        super.attachBaseContext(wrapped)
    }

    private lateinit var cache: LocalCache
    private lateinit var rv: RecyclerView
    private val adapter = LocationsAdapter(
        onClick = { loc -> setDefault(loc) },
        onLongClick = { loc -> deleteLocation(loc) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locations)

        // Setup toolbar with hamburger menu
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.saved_locations)

        // Handle hamburger menu click - navigate back to MainActivity
        toolbar.setNavigationOnClickListener {
            finish() // Close this activity and return to MainActivity
        }

        cache = LocalCache(this)
        rv = findViewById(R.id.rvLocations)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // Swipe to delete
        val swipe = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                r: RecyclerView,
                vh: RecyclerView.ViewHolder,
                t: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val pos = vh.adapterPosition
                if (pos == RecyclerView.NO_POSITION) return
                val item = adapter.current.getOrNull(pos) ?: return
                deleteLocation(item)
            }
        }
        ItemTouchHelper(swipe).attachToRecyclerView(rv)
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun load() {
        lifecycleScope.launch {
            val items = withContext(Dispatchers.IO) { cache.getAllLocations() }
            adapter.submit(items)
        }
    }

    private fun setDefault(loc: LocationEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            cache.setDefault(loc.name)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@LocationsActivity, "Default set: ${loc.name}", Toast.LENGTH_SHORT).show()
                // Signal to MainActivity that default changed, and close this screen
                val data = Intent().putExtra("defaultChanged", true)
                setResult(RESULT_OK, data)
                finish()
            }
        }
    }


    private fun deleteLocation(loc: LocationEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            cache.deleteLocation(loc.name)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@LocationsActivity, "Deleted: ${loc.name}", Toast.LENGTH_SHORT).show()
                load()
            }
        }
    }
}