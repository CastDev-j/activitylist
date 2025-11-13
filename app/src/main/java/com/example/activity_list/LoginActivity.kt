package com.example.activity_list

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var actvProfiles: com.google.android.material.textfield.MaterialAutoCompleteTextView
    private lateinit var btnSelectProfile: MaterialButton
    private lateinit var etProfileName: TextInputEditText
    private lateinit var btnCreateProfile: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        actvProfiles = findViewById(R.id.actvProfiles)
        btnSelectProfile = findViewById(R.id.btnSelectProfile)
        etProfileName = findViewById(R.id.etProfileName)
        btnCreateProfile = findViewById(R.id.btnCreateProfile)

        updateProfilesList()

        btnSelectProfile.setOnClickListener {
            val profiles = UserManager.getAllProfiles()
            if (profiles.isEmpty()) {
                Toast.makeText(this, R.string.no_profiles, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedProfile = actvProfiles.text?.toString()?.trim() ?: ""
            if (selectedProfile.isEmpty()) {
                Toast.makeText(this, R.string.error_selecting_profile, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (UserManager.selectProfile(selectedProfile)) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, R.string.error_selecting_profile, Toast.LENGTH_SHORT).show()
            }
        }

        btnCreateProfile.setOnClickListener {
            val profileName = etProfileName.text.toString().trim()

            if (profileName.isEmpty()) {
                Toast.makeText(this, R.string.enter_profile_name, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (UserManager.createProfile(profileName)) {
                Toast.makeText(this, R.string.profile_created, Toast.LENGTH_SHORT).show()
                if (UserManager.selectProfile(profileName)) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            } else {
                Toast.makeText(this, R.string.profile_exists, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProfilesList() {
        val profiles = UserManager.getAllProfiles()
        if (profiles.isEmpty()) {
            actvProfiles.visibility = View.GONE
            btnSelectProfile.isEnabled = false
        } else {
            actvProfiles.visibility = View.VISIBLE
            btnSelectProfile.isEnabled = true
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, profiles)
            actvProfiles.setAdapter(adapter)
        }
    }
}
