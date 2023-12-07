package com.example.testapp.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.testapp.R
import com.example.testapp.data.RegistrationData
import com.example.testapp.databinding.ActivityRegisterBinding
import com.example.testapp.repository.AuthRepository
import com.example.testapp.utils.APIservice

class Register : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener,
    View.OnKeyListener {

    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var mViewModel: RegisterActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.fullname2.onFocusChangeListener = this
        mBinding.email2.onFocusChangeListener = this
        mBinding.password2.onFocusChangeListener = this
        mBinding.cPassword2.onFocusChangeListener = this
        mViewModel = ViewModelProvider(
            this,
            RegisterActivityViewModelFactory(AuthRepository(APIservice.getService()), application)
        )
            .get(RegisterActivityViewModel::class.java)
        setupObservers()
    }

    private fun setupObservers() {
        mViewModel.getIsloading().observe(this) {
            mBinding.progressBar.isVisible = it
        }

        mViewModel.getIsUniqu().observe(this){
            if(validateEmail(shouldUpdateView = false)){
                if(it){
                    mBinding.email1.apply {
                        if (isErrorEnabled) isErrorEnabled = false
                        setStartIconDrawable(R.drawable.baseline_info_24)
                        setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                    }
                }else{
                    mBinding.email1.apply {
                        if (startIconDrawable != null) startIconDrawable = null
                        isErrorEnabled = true
                        error = "Email is used"
                    }
                }
            }
            mBinding.email1.isErrorEnabled = false
            return@observe
        }

        mViewModel.getErrorMessage().observe(this) {
            val formErrorKeys = arrayOf("fullname", "email", "password")
            val message = StringBuilder()
            it.map { entry ->
                if (formErrorKeys.contains(entry.key)) {
                    when(entry.key) {
                        "fullname" -> {
                            mBinding.fullname1.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }
                        }

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
                    }
                } else {
                    message.append(entry.value).append("\n")
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
        }

        mViewModel.getUser().observe(this) {

        }
    }

    private fun validateFullname(): Boolean {
        val value: String = mBinding.fullname2.text.toString()
        if (value.isEmpty()) {
            mBinding.fullname1.apply {
                isErrorEnabled = true
                error = "Full name is required"
            }
            return false
        }
        mBinding.fullname1.isErrorEnabled = false
        return true
    }

    private fun validateEmail(shouldUpdateView: Boolean = true): Boolean {
        val value = mBinding.email2.text.toString()
        if (value.isEmpty()) {
            mBinding.email1.apply {
                isErrorEnabled = true
                error = "Email is required"
            }
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            mBinding.email1.apply {
                isErrorEnabled = true
                error = "Invalid email address"
            }
            return false
        }
        if (shouldUpdateView) {
            mBinding.email1.apply {
                if (isErrorEnabled) isErrorEnabled = false
                setStartIconDrawable(R.drawable.baseline_check_circle_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        }
        mBinding.email1.isErrorEnabled = false
        return true
    }

    private fun validatePassword(): Boolean {
        val value = mBinding.password2.text.toString()
        if (value.isEmpty()) {
            mBinding.password1.apply {
                isErrorEnabled = true
                error = "Password is required"
            }
            return false
        } else if (value.length < 8) {
            mBinding.password1.apply {
                isErrorEnabled = true
                error = "Password is need 6 characters long or more"
            }
            return false
        }
        mBinding.password1.isErrorEnabled = false
        return true
    }

    private fun validateConfirmPassword(): Boolean {
        val value = mBinding.cPassword2.text.toString()
        if (value.isEmpty()) {
            mBinding.cPassword1.apply {
                isErrorEnabled = true
                error = "Confirm password is required"
            }
            return false
        } else if (value.length < 8) {
            mBinding.cPassword1.apply {
                isErrorEnabled = true
                error = "Confirm password is need 6 characters long or more"
            }
            return false
        }
        mBinding.cPassword1.isErrorEnabled = false
        return true
    }

    private fun validatePasswordConfirmPassword(): Boolean {
        val password = mBinding.password2.text.toString()
        val confirmPassword = mBinding.cPassword2.text.toString()
        if (password != confirmPassword) {
            mBinding.cPassword1.apply {
                isErrorEnabled = true
                error= "Confirm password doesn't match with password"
            }
            return false
        }
        mBinding.cPassword1.isErrorEnabled = false
        return true
    }

    override fun onClick(view: View?) {
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                R.id.fullname2 -> {
                    if (hasFocus) {
                        if (mBinding.fullname1.isErrorEnabled) {
                            mBinding.fullname1.isErrorEnabled = false
                        }
                    } else {
                        validateFullname()
                    }
                }

                R.id.email2 -> {
                    if (hasFocus) {
                        if (mBinding.email1.isErrorEnabled) {
                            mBinding.email1.isErrorEnabled = false
                        }
                    } else {
                        if (validateEmail()) {
                            mViewModel.validateRegistrion(RegistrationData(mBinding.email2.text!!.toString()))
                        }
                    }
                }

                R.id.password2 -> {
                    if (hasFocus) {
                        if (mBinding.password1.isErrorEnabled) {
                            mBinding.password1.isErrorEnabled = false
                        }
                    } else {
                        if (validatePassword() && mBinding.cPassword2.text!!.isNotEmpty() && validateConfirmPassword() && validatePasswordConfirmPassword()) {
                            mBinding.cPassword1.isErrorEnabled = false
                        }
                    }
                }

                R.id.cPassword2 -> {
                    if (hasFocus) {
                        if (mBinding.cPassword1.isErrorEnabled) {
                            mBinding.cPassword1.isErrorEnabled = false
                        }
                    } else {
                        validateConfirmPassword()
                        validatePasswordConfirmPassword()
                    }
                }
            }
        }
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }
}