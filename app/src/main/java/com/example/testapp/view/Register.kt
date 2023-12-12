package com.example.testapp.view

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.testapp.model.MainActivity
import com.example.testapp.R
import com.example.testapp.data.RegistrationData
import com.example.testapp.data.ValidateEmail
import com.example.testapp.databinding.ActivityRegisterBinding
import com.example.testapp.repository.AuthRepository
import com.example.testapp.utils.APIservice
import com.example.testapp.utils.VibrateView
import com.example.testapp.view_model.RegisterActivityViewModel
import com.example.testapp.view_model.RegisterActivityViewModelFactory

class Register : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener,
    View.OnKeyListener, TextWatcher {

    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var mViewModel: RegisterActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.fullname2.onFocusChangeListener = this
        mBinding.email2.onFocusChangeListener = this
        mBinding.password2.onFocusChangeListener = this
        mBinding.cPassword2.setOnKeyListener(this)
        mBinding.cPassword2.onFocusChangeListener = this
        mBinding.cPassword2.addTextChangedListener(this)
        mBinding.registerButton.setOnKeyListener(this)
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
            if (it != null){
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    private fun validateFullname(shouldVibrateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        val value: String = mBinding.fullname2.text.toString()
        if (value.isEmpty()) {
            mBinding.fullname1.apply {
                isErrorEnabled = true
                error = "Full name is required"
            }
        }
        if (errorMessage != null) {
            mBinding.fullname1.apply{
                isErrorEnabled = true
                error = errorMessage
                if(shouldVibrateView) VibrateView.vibrate(this@Register, this)
            }
        }
        return errorMessage == null
    }

    private fun validateEmail(shouldUpdateView: Boolean = true, shouldVibrateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        val value = mBinding.email2.text.toString()
        if (value.isEmpty()){
            errorMessage = "Email is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()){
            errorMessage = "Email invalid"
        }
        if (errorMessage != null && shouldUpdateView){
            mBinding.email1.apply {
                isErrorEnabled = true
                error = errorMessage
                if(shouldVibrateView) VibrateView.vibrate(this@Register, this)
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
            errorMessage = "Passwrod must be 8 character long"
        }
        if (errorMessage != null && shouldUpdateView) {
            mBinding.password1.apply {
                isErrorEnabled = true
                error = errorMessage
                if(shouldVibrateView) VibrateView.vibrate(this@Register, this)
            }
        }
        return errorMessage == null
    }

    private fun validateConfirmPassword(shouldUpdateView: Boolean = true, shouldVibrateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        val value = mBinding.cPassword2.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Password required"
        } else if (value.length < 8) {
            errorMessage = "Passwrod must be 8 character long"
        }
        if (errorMessage != null && shouldUpdateView) {
            mBinding.cPassword1.apply {
                isErrorEnabled = true
                error = errorMessage
                if(shouldVibrateView) VibrateView.vibrate(this@Register, this)
            }
        }
        return errorMessage == null
    }

    private fun validatePasswordConfirmPassword(shouldUpdateView: Boolean = true, shouldVibrateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        val password = mBinding.password2.text.toString()
        val confirmPassword = mBinding.cPassword2.text.toString()
        if (password != confirmPassword) {
            errorMessage = "Confirm password doesn't match with password"
        }
        if (errorMessage != null && shouldUpdateView) {
            mBinding.cPassword1.apply {
                isErrorEnabled = true
                error = errorMessage
                if(shouldVibrateView) VibrateView.vibrate(this@Register, this)
            }
        }
        return errorMessage == null
    }

    override fun onClick(view: View?) {
        if (view != null && view.id == R.id.registerButton)
            onSubmit()
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
                            mViewModel.ValidateEmailAddress(ValidateEmail(mBinding.email2.text!!.toString()))
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

    override fun onKey(view: View?, keyCode: Int, keyEvent: KeyEvent?): Boolean {
        if(KeyEvent.KEYCODE_ENTER == keyCode && keyEvent!!.action == KeyEvent.ACTION_UP){
            onSubmit()
        }

        return false
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(validatePassword(shouldUpdateView = false) && validateConfirmPassword(shouldUpdateView = false)
            && validatePasswordConfirmPassword(shouldUpdateView = false)){
            mBinding.cPassword1.apply {
                if (isErrorEnabled) isErrorEnabled = false
                setStartIconDrawable(R.drawable.baseline_check_circle_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        }else{
            if (mBinding.cPassword1.startIconDrawable != null)
                mBinding.cPassword1.startIconDrawable = null
        }
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun onSubmit(){
        if(validate()){
            mViewModel.registerUser(RegistrationData(mBinding.fullname2.text!!.toString(), mBinding.email2.text!!.toString(), mBinding.password2.text!!.toString()))
        }
    }

    private fun validate(): Boolean {
        var isValid = true

        if(!validateFullname(shouldVibrateView = false)) isValid = false
        if(!validateEmail(shouldVibrateView = false)) isValid = false
        if(!validatePassword(shouldVibrateView = false)) isValid = false
        if(!validateConfirmPassword(shouldVibrateView = false)) isValid = false
        if(isValid && !validatePasswordConfirmPassword(shouldVibrateView = false)) isValid = false

        if (!isValid) VibrateView.vibrate(this, mBinding.cardview)

        return isValid
    }
}