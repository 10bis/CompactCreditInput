package com.tenbis.library.watchers

import android.text.Editable
import com.tenbis.library.consts.CardType
import com.tenbis.library.listeners.CreditCardTextChangeListener

open class CreditCardCvvMaskWatcher(
    private val creditCardTextChangeListener: CreditCardTextChangeListener
) : MaskFormatterWatcher() {

    var cardType: CardType = CardType.UNKNOWN

    override val maxLength: Int
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
                creditCardTextChangeListener.onCardCVVEntered(null, false)
        }
    }

    override fun onPrevious() {
        creditCardTextChangeListener.onPrevious()
    }

    companion object {
        private const val DEFAULT_CVV_LENGTH = 3
    }
}