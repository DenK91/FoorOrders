package com.example.denk.foodorders

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import kotlinx.android.synthetic.main.activity_main.*
import com.apollographql.apollo.ApolloMutationCall
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk25.coroutines.onClick

class MainActivity : AppCompatActivity(), AnkoLogger {

    override val loggerTag: String
        get() = "dnek"

    private var callGetUserId: ApolloMutationCall<UserMutation.Data>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (prefs.isLogin()) {
            info { "from prefs: ${prefs.userId}" }
            startActivity(intentFor<OrdersActivity>())
            finish()
        }

        btnLogin.onClick {
            callGetUserId?.cancel()
            callGetUserId = apolloClient.mutate(UserMutation.builder()
                    .aFirstName(etFirstName.text.toString())
                    .aLastName(etLastName.text.toString())
                    .build())
            callGetUserId?.enqueue({
                val userId = it.data()?.user!!._id
                if (!TextUtils.isEmpty(userId)) {
                    prefs.userId = userId
                    startActivity(intentFor<OrdersActivity>())
                    finish()
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callGetUserId?.cancel()
    }
}
