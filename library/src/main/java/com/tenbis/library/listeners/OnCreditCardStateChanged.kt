package com.tenbis.library.listeners

import com.tenbis.library.consts.CardType
import com.tenbis.library.models.CreditCard

/**
 * Notifies when a change to the card state has been made
 */
interface OnCreditCardStateChanged {

    /**
     * Once all fields have valid inputs
     * @param creditCard the credit card based on the fields
     */
    fun onCreditCardValid(creditCard: CreditCard)

    /**
     * Once a valid card number is entered
     * @param creditCardNumber the credit card number
     */
    fun onCreditCardNumberValid(creditCardNumber: String)

    /**
     * Once a valid expiration date is entered
     * @param month 1 - 12
     * @param year 19 - 39
     */
    fun onCreditCardExpirationDateValid(month: Int, year: Int)

    /**
     * Once a valid cvv is entered
     * @param cvv the credit card cvv
     */
    fun onCreditCardCvvValid(cvv: String)

    /**
     * Once the card type is found
     * @param cardType the type of the credit card
     */
    fun onCreditCardTypeFound(cardType: CardType)

    /**
     * Notifies if an invalid card is currently inputted
     * Can be used to set button not clickable
     */
    fun onInvalidCardTyped()
}