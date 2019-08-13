package com.tenbis.compatcardinput

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.tenbis.library.consts.CardType
import com.tenbis.library.listeners.OnCreditCardStateChanged
import com.tenbis.library.models.CreditCard
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnCreditCardStateChanged {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_activity_credit_card.attachLifecycle(lifecycle)
        main_activity_credit_card.addOnCreditCardStateChangedListener(this)
    }

    override fun onCreditCardValid(creditCard: CreditCard) {
        Toast.makeText(this, creditCard.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onInvalidCardTyped() {
        Log.w("MainActivity", "invalid")
    }

    override fun onCreditCardNumberValid(creditCardNumber: String) {
    }

    override fun onCreditCardExpirationDateValid(month: Int, year: Int) {
    }

    override fun onCreditCardCvvValid(cvv: String) {
    }

    override fun onCreditCardTypeFound(cardType: CardType) {
    }
}
