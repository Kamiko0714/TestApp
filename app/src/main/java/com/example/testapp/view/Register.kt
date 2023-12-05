package com.example.testapp.view

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import com.example.testapp.R
import com.example.testapp.databinding.ActivityRegisterBinding

class Register : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {

    private lateinit var mBinding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.fullname2.onFocusChangeListener = this
        mBinding.email2.onFocusChangeListener = this
        mBinding.password2.onFocusChangeListener = this
        mBinding.cPassword2.onFocusChangeListener = this
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

    private fun validateEmail(): Boolean {
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
                error = "Email is invalid"
            }
            return false
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
                error = "Confirm password doesn't match with password"
            }
            return false
        }
        mBinding.cPassword1.isErrorEnabled = false
        return true
    }

    override fun onClick(view: View?) {
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if(view != null){
            when(view.id){
                R.id.fullname2 -> {
                    if(hasFocus){
                        if(mBinding.fullname1.isErrorEnabled){
                            mBinding.fullname1.isErrorEnabled = false
                        }
                    }else{
                        validateFullname()
                    }
                }
                R.id.email2 -> {
                    if(hasFocus){
                        if(mBinding.email1.isErrorEnabled){
                            mBinding.email1.isErrorEnabled = false
                        }
                    }else{
                        validateEmail()
                        //unik plz
                    }
                }
                R.id.password2 -> {
                    if(hasFocus){
                        if(mBinding.password1.isErrorEnabled){
                            mBinding.password1.isErrorEnabled = false
                        }
                    }else{
                        if (validatePassword() && mBinding.cPassword2.text!!.isNotEmpty() && validateConfirmPassword() && validatePasswordConfirmPassword()) {
                            mBinding.cPassword1.isErrorEnabled = false
                        }
                    }
                }
                R.id.cPassword2 -> {
                    if(hasFocus){
                        if(mBinding.cPassword1.isErrorEnabled){
                            mBinding.cPassword1.isErrorEnabled = false
                        }
                    }else{
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