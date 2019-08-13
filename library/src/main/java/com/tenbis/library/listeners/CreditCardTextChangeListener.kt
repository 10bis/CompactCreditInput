package com.tenbis.library.listeners

import com.tenbis.library.consts.CardType

interface CreditCardTextChangeListener {

    /**
     * Called when a card type is found
     * @param cardType the type that was found
     */
    fun onCardTypeFound(cardType: CardType)

    /**
     * Called when a number is entered
     * @param cardNumber a valid card number or null otherwise
     * @param completed flag so we can know if the card has the same length as it's type
     * this is used because there are cards like Isracard that are only 9 digits but look like another card
     */
    fun onCardNumberEntered(cardNumber: String? = null, completed: Boolean = true)

    /**
     * Called when a date is entered
     * @param dateValid if the provided date is valid
     * @param month the expiry month or -1 if not valid
     * @param year the expiry year or -1 if not valid
     */
    fun onCardDateEntered(dateValid: Boolean = false, month: Int = -1, year: Int = -1)

    /**
     * Called when a cvv number is entered
     * @param cvv a valid card cvv number or null otherwise
     * @param completed flag so we can know if the card has the same cvv length as it's type
     * this is used because cards like [CardType.AMERICAN_EXPRESS] have 4 digits cvv but they also support 3 digits cid
     */
    fun onCardCVVEntered(cvv: String? = null, completed: Boolean = true)

    /**
     * Called when the next field is requested
     *
     * @param appendChar char to append at the beginning of the next field
     */
    fun onNext(appendChar: Char? = null)

    /**
     * Called when the previous field is requested
     */
    fun onPrevious()
}