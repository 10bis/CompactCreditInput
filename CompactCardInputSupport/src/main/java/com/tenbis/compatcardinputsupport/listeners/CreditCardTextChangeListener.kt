package com.tenbis.compatcardinputsupport.listeners

import com.tenbis.compatcardinputsupport.consts.CardType

interface CreditCardTextChangeListener {

    fun onCardTypeFound(cardType: CardType)

    fun onCardNumberEntered(cardNumber: String, completed: Boolean = true)
    fun onInvalidCardNumberEntered()

    fun onCardValidDateEntered(month: Int, year: Int)
    fun onCardInvalidDateEntered()

    fun onCardCVVEntered(cvv: String, completed: Boolean = true)
    fun onInvalidCardCVVEntered()


    fun onFocusNext(appendChar: Char? = null)
    fun onFocusPrevious()
}