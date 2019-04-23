package com.tenbis.support.watchers

import android.text.Editable
import android.text.Spannable
import com.tenbis.support.consts.CardType
import com.tenbis.support.listeners.CreditCardTextChangeListener
import com.tenbis.support.spans.PaddingRightSpan

open class CreditCardNumberMaskWatcher(
    private val creditCardTextChangeListener: CreditCardTextChangeListener
) : MaskFormatterWatcher() {

    private var cardType: CardType = CardType.UNKNOWN

    override val maxLength: Int
        get() = cardType.numberLength

    override fun onTextChanged(content: CharSequence?, start: Int, before: Int, count: Int) {
        super.onTextChanged(content, start, before, count)
        val updatedLength = start + count

        if (updatedLength < CARD_TYPE_MIN_CHARS) {
            cardType = CardType.UNKNOWN
            creditCardTextChangeListener.onCardTypeFound(cardType)
        } else if (updatedLength in CARD_TYPE_MIN_CHARS..CARD_TYPE_MAX_CHARS ||
            start < CARD_TYPE_MIN_CHARS && count > CARD_TYPE_PASTE_CHARS_SIZE
        ) {
            cardType = CardType.fromCardNumber(content.toString())
            creditCardTextChangeListener.onCardTypeFound(cardType)
        }
    }

    override fun removeAppliedMask(editable: Editable) {
        val spans = editable.getSpans(0, editable.length, PaddingRightSpan::class.java)

        for (i in spans.indices) {
            editable.removeSpan(spans[i])
        }
    }

    override fun shouldAppendMask(index: Int): Boolean =
        index in cardType.cardSpacingPattern

    override fun validateContent(content: CharSequence): Boolean = true

    override fun onInvalidContent(editable: Editable, suggestFix: Boolean): Boolean = false

    override fun applyMask(editable: Editable, index: Int) {
        val paddingSpan = PaddingRightSpan(CARD_PATTERN_SPACING_PADDING)
        editable.setSpan(paddingSpan, index - 1, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    override fun onPrevious() {
        creditCardTextChangeListener.onPrevious()
    }

    override fun onNext(appendNext: Char?) {
        creditCardTextChangeListener.onNext(appendNext)
    }

    override fun onRoundCompleted(currentContent: String) {
        when {
            currentContent.length == cardType.numberLength ->
                creditCardTextChangeListener.onCardNumberEntered(currentContent, true)

            currentContent.length < MINIMUM_SUPPORTED_DIGITS ->
                creditCardTextChangeListener.onCardNumberEntered()

            else ->
                creditCardTextChangeListener.onCardNumberEntered(currentContent, false)
        }
    }

    companion object {
        private const val CARD_PATTERN_SPACING_PADDING = 15
        private const val MINIMUM_SUPPORTED_DIGITS = 9
        private const val CARD_TYPE_MIN_CHARS = 2
        private const val CARD_TYPE_MAX_CHARS = 5
        private const val CARD_TYPE_PASTE_CHARS_SIZE = 3
    }
}