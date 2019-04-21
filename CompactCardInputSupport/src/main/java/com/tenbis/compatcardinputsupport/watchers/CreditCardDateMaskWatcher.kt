package com.tenbis.tbapp.refactor.fragments.card.add.watchers

import android.text.Editable
import android.text.Spannable
import com.tenbis.compatcardinputsupport.watchers.AllowanceLevel
import com.tenbis.compatcardinputsupport.watchers.MaskFormatterFormatterWatcher
import com.tenbis.tbapp.refactor.fragments.card.add.CreditCardTextChangeListener
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import java.util.regex.Pattern
import com.tenbis.tbapp.refactor.fragments.card.add.spans.SlashSpan


class CreditCardDateMaskWatcher(
    private val creditCardTextChangeListener: CreditCardTextChangeListener
) : MaskFormatterFormatterWatcher() {

    override var maxLength: Int = 4

    override var errorAllowanceLevel: AllowanceLevel = AllowanceLevel.SUGGEST_FIX

    private val datePattern = Pattern.compile("(0[1-9]|1[0-2])(19|[2-6]\\d)")

    private val dateFormat = DateTimeFormat.forPattern("MMYY")

    private var currentDate = LocalDate()
    private val currentComparableDate = LocalDate(currentDate.year, currentDate.monthOfYear, 1)

    private var isValid = false
    private var parsedValidDate: LocalDate? = null

    override fun removeAppliedMask(editable: Editable) {
        val spans = editable.getSpans(0, editable.length, SlashSpan::class.java)

        for (i in spans.indices) {
            editable.removeSpan(spans[i])
        }
    }

    override fun shouldAppendMask(index: Int): Boolean {
        return index == DIVIDER_INDEX
    }

    override fun validateContent(content: CharSequence): Boolean {
        val matcher = datePattern.matcher(content)
        isValid = matcher.matches() || matcher.hitEnd()

        if (isValid && content.length == maxLength) {
            val parsedDate = LocalDate.parse(content.toString(), dateFormat)

            isValid = !parsedDate.isBefore(currentComparableDate)

            parsedValidDate = if (isValid) parsedDate else null
        }
        return isValid
    }


    override fun onInvalidContent(editable: Editable, suggestFix: Boolean): Boolean {
        if (editable.length == 1 && editable.toString().toInt() in 2..9) {
            editable.insert(0, "0")
            return true
        } else {
            editable.truncate()
            return false
        }
    }

    override fun applyMask(editable: Editable, index: Int) {
        val dividerSpan = SlashSpan()
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
        if (isValid && currentContent.length == maxLength && parsedValidDate != null) {
            creditCardTextChangeListener
                .onCardValidDateEntered(
                    parsedValidDate!!.monthOfYear,
                    parsedValidDate!!.year % 100
                )
        } else {
            creditCardTextChangeListener.onCardInvalidDateEntered()
        }
    }

    companion object {
        private const val DIVIDER_INDEX = 2
    }
}