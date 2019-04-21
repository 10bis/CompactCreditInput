package com.tenbis.compatcardinputsupport.listeners

interface CreditCardTypedListener {

    fun onCardTyped(cardNumber: String,
                    expirationMonth: String, expirationYear: String,
                    cvv: String)

    fun onInvalidCardTyped()
}