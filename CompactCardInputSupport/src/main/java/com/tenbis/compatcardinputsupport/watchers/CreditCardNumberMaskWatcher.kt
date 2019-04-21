package com.tenbis.compatcardinputsupport.watchers

import android.text.Editable
import android.text.Spannable
import com.tenbis.compatcardinputsupport.consts.CardType
import com.tenbis.compatcardinputsupport.listeners.CreditCardTextChangeListener
import com.tenbis.compatcardinputsupport.spans.PaddingRightSpan

class CreditCardNumberMaskWatcher(
    private val creditCardTextChangeListener: CreditCardTextChangeListener
) :
    MaskFormatterFormatterWatcher() {

    private var cardType: CardType = CardType.UNKNOWN


    override var maxLength: Int = -1
        get() = cardType.numberLength

    override fun onTextChanged(content: CharSequence?, start: Int, before: Int, count: Int) {
        super.onTextChanged(content, start, before, count)
        val updatedLength = start + count

        if (updatedLength < 2) {
            cardType = CardType.UNKNOWN
            creditCardTextChangeListener.onCardTypeFound(cardType)
        } else if (updatedLength in 2..5 || (start < 2 && count > 3)) {
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
        val dividerSpan = PaddingRightSpan(15)
        editable.setSpan(
            dividerSpan,
            index - 1,
            index,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    override fun onPrevious() {
        creditCardTextChangeListener.onFocusPrevious()
    }

    override fun onNext(appendNext: Char?) {
        creditCardTextChangeListener.onFocusNext(appendNext)
    }

    override fun onRoundCompleted(currentContent: String) {
        when {
            currentContent.length == cardType.numberLength ->
                creditCardTextChangeListener.onCardNumberEntered(currentContent, true)

            currentContent.length < 6 ->
                creditCardTextChangeListener.onInvalidCardNumberEntered()

            else ->
                creditCardTextChangeListener.onCardNumberEntered(currentContent, false)
        }
    }
}