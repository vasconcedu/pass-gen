package io.github.vasconcedu.passgen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.slider.Slider
import java.security.SecureRandom
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private val label = "PassGen"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup password length slider
        setupSlider()

        // Setup generate button
        setupGenerate()

        // Setup copy button
        setupCopy()

        // Setup clear button
        setupClear()
    }

    private fun setupSlider() {
        // Set initial value
        val slider = findViewById<Slider>(R.id.slider_password_length)
        val view = findViewById<TextView>(R.id.text_view_password_length)
        view.text = slider.value.toInt().toString()

        // Register listener
        slider.addOnChangeListener { _, value, _ ->
            view.text = value.toInt().toString()
        }
    }

    private fun setupGenerate() {
        val button = findViewById<Button>(R.id.button_generate)
        button.setOnClickListener {
            generate()
        }
    }

    private fun setupCopy() {
        val button = findViewById<Button>(R.id.button_copy)
        button.setOnClickListener {
            copy()
        }
    }

    private fun setupClear() {
        val button = findViewById<Button>(R.id.button_clear)
        button.setOnClickListener{
            clearClipboard()
        }
    }

    private fun generate() {
        val uppercase = findViewById<CheckBox>(R.id.checkbox_uppercase).isChecked
        val lowercase = findViewById<CheckBox>(R.id.checkbox_lowercase).isChecked
        val numbers = findViewById<CheckBox>(R.id.checkbox_numbers).isChecked
        val symbols = findViewById<CheckBox>(R.id.checkbox_symbols).isChecked
        val length = findViewById<Slider>(R.id.slider_password_length).value.toInt()

        val allowed = arrayListOf<Int>()

        if (uppercase) {
            allowed.addAll(65..90)
        }

        if (lowercase) {
            allowed.addAll(97..122)
        }

        if (numbers) {
            allowed.addAll(48..57)
        }

        if (symbols) {
            allowed.addAll(33..47)
            allowed.addAll(58..64)
            allowed.addAll(91..96)
            allowed.addAll(123..126)
        }

        // Otherwise would crash if no checkboxes were checked
        if (allowed.size == 0) {
            return
        }

        val builder = StringBuilder()
        val random = SecureRandom()

        for (i in 0 until length) {
            builder.append(allowed[abs(random.nextInt()) % allowed.size].toChar())
        }

        val view = findViewById<EditText>(R.id.edit_text_password)
        view.setText(builder.toString(), TextView.BufferType.EDITABLE)
    }

    private fun copy() {
        // Copy password to clipboard
        val view = findViewById<EditText>(R.id.edit_text_password)
        val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, view.text)
        manager.setPrimaryClip(clip)
        Toast.makeText(
            applicationContext,
            "$label: ${resources.getString(R.string.copied_to_clipboard)}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun clearClipboard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            while (manager.hasPrimaryClip()) {
                manager.clearPrimaryClip()
            }
            Toast.makeText(
                applicationContext,
                "$label: ${resources.getString(R.string.cleared_clipboard)}",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                applicationContext,
                "$label: ${resources.getString(R.string.failed_api_level_below_p)}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}