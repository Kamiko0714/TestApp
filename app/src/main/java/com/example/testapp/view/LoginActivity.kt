package com.example.testapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.testapp.model.MainActivity
import com.example.testapp.R
import com.example.testapp.data.LoginRequestBody
import com.example.testapp.databinding.ActivityLoginBinding
import com.example.testapp.repository.AuthRepository
import com.example.testapp.utils.APIservice
import com.example.testapp.utils.VibrateView
import com.example.testapp.view_model.LoginActivityViewModel
import com.example.testapp.view_model.LoginActivityViewModelFactory

class LoginActivity : AppCompatActivity(), View.OnKeyListener, View.OnFocusChangeListener, View.OnClickListener {

    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var mViewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.loginButton.setOnClickListener(this)
        mBinding.registerButton.setOnClickListener(this)
        mBinding.email2.onFocusChangeListener = this
        mBinding.password2.onFocusChangeListener = this
        mBinding.password2.setOnKeyListener(this)

        mViewModel = ViewModelProvider(this, LoginActivityViewModelFactory(AuthRepository(APIservice.getService()), application)).get(LoginActivityViewModel::class.java)

        setupObservers()
    }

    private fun setupObservers() {
        mViewModel.getIsloading().observe(this) {
            mBinding.progressBar.isVisible = it
        }

        mViewModel.getErrorMessage().observe(this) {
            val formErrorKeys = arrayOf("email", "password")
            val message = StringBuilder()

            it.forEach { entry ->
                when (entry.key) {
                    "email" -> {
                        mBinding.email1.apply {
                            isErrorEnabled = true
                            error = entry.value
                        }
                    }

                    "password" -> {
                        mBinding.password1.apply {
                            isErrorEnabled = true
                            error = entry.value
                        }
                    }

                    else -> {
                        message.append(entry.value).append("\n")
                    }
                }
            }

            if (message.isNotEmpty()) {
                AlertDialog.Builder(this)
                    .setIcon(R.drawable.baseline_check_circle_24)
                    .setTitle("INFORMATION")
                    .setMessage(message)
                    .setPositiveButton("OK") { dialog, _ -> dialog!!.dismiss() }
                    .show()
            }
        }

        mViewModel.getUser().observe(this) {
            if (it != null) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    private fun validateEmail(shouldUpdateView: Boolean = true, shouldVibrateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        val value = mBinding.email2.text.toString()

        if (value.isEmpty()) {
            errorMessage = "Email is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            errorMessage = "Email invalid"
        }

        if (errorMessage != null && shouldUpdateView) {
            mBinding.email1.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibrateView.vibrate(this@LoginActivity, this)
            }
        }

        return errorMessage == null
    }

    private fun validatePassword(shouldUpdateView: Boolean = true, shouldVibrateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        val value = mBinding.password2.text.toString()

        if (value.isEmpty()) {
            errorMessage = "Password required"
        } else if (value.length < 8) {
            errorMessage = "Password must be 8 characters long"
        }

        if (errorMessage != null && shouldUpdateView) {
            mBinding.password1.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibrateView.vibrate(this@LoginActivity, this)
            }
        }

        return errorMessage == null
    }

    private fun validate(): Boolean {
        var isValid = true

        if (!validateEmail(shouldVibrateView = false)) isValid = false
        if (!validatePassword(shouldVibrateView = false)) isValid = false

        if (!isValid) VibrateView.vibrate(this, mBinding.cardview)

        return isValid
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                R.id.email2 -> {
                    if (hasFocus) {
                        if (mBinding.email1.isErrorEnabled) {
                            mBinding.email1.isErrorEnabled = false
                        }
                    } else {
                        validateEmail()
                    }
                }

                R.id.password2 -> {
                    if (hasFocus) {
                        if (mBinding.password1.isErrorEnabled) {
                            mBinding.password1.isErrorEnabled = false
                        }
                    } else {
                        validatePassword()
                    }
                }
            }
        }
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.loginButton -> {
                    submitForm()
                }
                R.id.registerButton -> {
                    startActivity(Intent(this, Register::class.java))
                }
            }
        }
    }

    override fun onKey(view: View?, keyCode: Int, keyEvent: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
            submitForm()
        }
        return false
    }

    private fun submitForm() {
        if (validate()) {
            mViewModel.loginUser(LoginRequestBody(mBinding.email2.text!!.toString(), mBinding.password2.text!!.toString()))
        }
    }
}
