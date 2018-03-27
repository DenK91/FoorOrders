package com.example.denk.foodorders

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import kotlinx.android.synthetic.main.activity_main.*
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloCallback
import com.apollographql.apollo.ApolloMutationCall
import com.apollographql.apollo.api.Response
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk25.coroutines.onClick


class MainActivity : AppCompatActivity(), AnkoLogger {

    override val loggerTag: String
        get() = "dnek"

    var uiHandler = Handler(Looper.getMainLooper())
    var callGetUserId : ApolloMutationCall<UserMutation.Data>? = null

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
            callGetUserId?.enqueue(ApolloCallback<UserMutation.Data>(object : ApolloCall.Callback<UserMutation.Data>() {
                        override fun onResponse(response: Response<UserMutation.Data>) {
                            val userId = response.data()?.user!!._id
                            if (!TextUtils.isEmpty(userId)) {
                                prefs.userId = userId!!
                                info { "from server ${prefs.userId}" }
                                startActivity(intentFor<OrdersActivity>())
                                finish()
                            } else {
                                info { " from server $userId" }
                            }
                        }

                        override fun onFailure(e: ApolloException) {
                            info { e }
                        }

                    }, uiHandler))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callGetUserId?.cancel()
    }
}
