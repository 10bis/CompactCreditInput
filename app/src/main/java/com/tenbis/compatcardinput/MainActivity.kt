package com.tenbis.compatcardinput

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.tenbis.support.listeners.OnCreditCardStateChanged
import com.tenbis.support.models.CreditCard
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnCreditCardStateChanged {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_activity_credit_card.attachLifecycle(lifecycle)
        main_activity_credit_card.addOnCreditCardStateChangedListener(this)
    }

    override fun onCreditCardCompleted(creditCard: CreditCard) {
        Toast.makeText(this, creditCard.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onInvalidCardTyped() {
        Log.w("MainActivity", "invalid")
    }
}
