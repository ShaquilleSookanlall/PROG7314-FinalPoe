package com.st10140587.prog7314_poe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {


    private val scope = MainScope()
    private lateinit var store: SettingsStore

    private lateinit var languageRow: LinearLayout
    private lateinit var languageValue: TextView

    // 🔹 Ensure this screen also uses the saved locale
    override fun attachBaseContext(newBase: Context) {
        val wrapped = LocaleUtils.wrapContext(newBase)
        super.attachBaseContext(wrapped)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        store = SettingsStore(this)

        // --- Toolbar with back arrow ---
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.menu_settings)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // --- Units switch (C / F) ---
        val unitSwitch = findViewById<SwitchMaterial>(R.id.switchUnits)
        scope.launch {
            try {
                unitSwitch.isChecked = store.useCelsius.first()
            } catch (_: Exception) { /* ignore */ }
        }
        unitSwitch.setOnCheckedChangeListener { _, checked ->
            scope.launch {
                try {
                    store.setUseCelsius(checked)
                } catch (_: Exception) { /* ignore */ }
            }
        }

        // --- Language row ---
        languageRow = findViewById(R.id.rowPreferredLanguage)
        languageValue = findViewById(R.id.tvLanguageValue)

        // Show current language label
        loadCurrentLanguageLabel()

        // Tap row -> choose language dialog
        languageRow.setOnClickListener {
            showLanguageDialog()
        }
    }

    // Read saved language + show the correct label
    private fun loadCurrentLanguageLabel() {
        val names = resources.getStringArray(R.array.language_names)
        val values = resources.getStringArray(R.array.language_values)

        val currentCode = LocaleUtils.getSavedLanguage(this)
        val index = values.indexOf(currentCode).takeIf { it >= 0 } ?: 0
        languageValue.text = names[index]
    }

    // Show the list of language options
    private fun showLanguageDialog() {
        val names = resources.getStringArray(R.array.language_names)
        val values = resources.getStringArray(R.array.language_values)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.preferred_language))
            .setSingleChoiceItems(names, getCurrentLanguageIndex(values)) { dialog, which ->
                val selectedCode = values[which]
                val selectedName = names[which]
                applyLanguage(selectedCode, selectedName)
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun getCurrentLanguageIndex(values: Array<String>): Int {
        val code = LocaleUtils.getSavedLanguage(this)
        val idx = values.indexOf(code)
        return if (idx >= 0) idx else 0
    }

    // Save language + restart the app
    private fun applyLanguage(langCode: String, label: String) {
        // 1) Save language using helper
        LocaleUtils.saveLanguage(this, langCode)

        // 2) Update the label in this screen (for immediate feedback)
        languageValue.text = label

        // 3) Restart from LandingActivity so ALL Activities are recreated
        val intent = Intent(this, LandingActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        )
        startActivity(intent)
        // Do not call finish(); CLEAR_TASK clears the back stack anyway
    }

    // Handle ActionBar "up" button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { onBackPressedDispatcher.onBackPressed(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
