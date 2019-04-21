package com.tenbis.compatcardinputsupport.watchers

import android.text.Editable
import com.tenbis.compatcardinputsupport.consts.CardType
import com.tenbis.compatcardinputsupport.listeners.CreditCardTextChangeListener


class CreditCardCvvMaskWatcher(
    private val creditCardTextChangeListener: CreditCardTextChangeListener
) : MaskFormatterFormatterWatcher() {

    var cardType: CardType = CardType.UNKNOWN

    override var maxLength: Int = -1
        get() = cardType.cvvLength


    override fun removeAppliedMask(editable: Editable) = Unit

    override fun shouldAppendMask(index: Int): Boolean = false

    override fun validateContent(content: CharSequence): Boolean = true

    override fun onInvalidContent(editable: Editable, suggestFix: Boolean): Boolean = true

    override fun applyMask(editable: Editable, index: Int) = Unit

    override fun onRoundCompleted(currentContent: String) {
        when (currentContent.length) {
            maxLength ->
                creditCardTextChangeListener.onCardCVVEntered(currentContent, true)

            DEFAULT_CVV_LENGTH ->
                creditCardTextChangeListener.onCardCVVEntered(currentContent, false)

            else ->
                creditCardTextChangeListener.onInvalidCardCVVEntered()
        }
    }

    override fun onPrevious() {
        creditCardTextChangeListener.onFocusPrevious()
    }

    companion object {
        private const val DEFAULT_CVV_LENGTH = 3
    }
}