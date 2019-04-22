package com.tenbis.support.listeners

import com.tenbis.support.models.CreditCard

/**
 * Notifies when a change to the card state has been made
 */
interface OnCreditCardStateChanged {

    /**
     * Once all fields have valid inputs
     * @param creditCard the credit card based on the fields
     */
    fun onCreditCardCompleted(creditCard: CreditCard)

    /**
     * Notifies if an invalid card is currently inputted
     * Can be used to set button not clickable
     */
    fun onInvalidCardTyped()
}