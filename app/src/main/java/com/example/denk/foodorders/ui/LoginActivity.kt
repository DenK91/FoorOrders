package com.example.denk.foodorders.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import com.example.denk.foodorders.*
import com.example.denk.foodorders.viewmodels.LoginVM
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk25.coroutines.onClick

class LoginActivity : AppCompatActivity(), AnkoLogger {

    override val loggerTag: String
        get() = "dnek"

    private var loginVM : LoginVM? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginVM = ViewModelProviders.of(this).get(LoginVM::class.java)
        loginVM?.getUser()?.observe(this, Observer {
            if (it != null && it.isLogin()) {
                info { "from prefs: ${it.id}" }
                startActivity(intentFor<OrdersActivity>())
                finish()
            }
        })

        btnLogin.onClick {
            loginVM?.login(etFirstName.text.toString(), etLastName.text.toString())
        }
    }
}
