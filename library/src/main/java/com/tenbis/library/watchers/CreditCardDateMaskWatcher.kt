package com.tenbis.library.watchers

import android.text.Editable
import android.text.Spannable
import com.tenbis.library.listeners.CreditCardTextChangeListener
import com.tenbis.library.spans.SlashSpan
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

open class CreditCardDateMaskWatcher(
    private val creditCardTextChangeListener: CreditCardTextChangeListener
) : MaskFormatterWatcher() {

    override val maxLength: Int = DATE_MAX_LENGTH

    override var errorAllowanceLevel: AllowanceLevel = AllowanceLevel.SUGGEST_FIX

    private val datePattern = Pattern.compile("(0[1-9]|1[0-2])(19|[2-3]\\d)")

    private val now = Calendar.getInstance()
    private val comperableInstance = GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), 1)

    private val dateFormat = SimpleDateFormat("MMyy", Locale.ENGLISH)

    private var isValid = false
    private var parsedValidDate: Date? = null

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
            val parsedDate = dateFormat.parse(content.toString())
            now.set(Calendar.DAY_OF_MONTH, 1)
            isValid = !parsedDate.before(comperableInstance.time)

            parsedValidDate = if (isValid) parsedDate else null
        }
        return isValid
    }

    override fun onInvalidContent(editable: Editable, suggestFix: Boolean): Boolean {
        return if (editable.length == 1 && editable.toString().toInt() in FIXABLE_MONTH_RANGE) {
            editable.insert(0, "0")
            true
        } else {
            editable.truncate()
            false
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
        creditCardTextChangeListener.onPrevious()
    }

    override fun onNext(appendNext: Char?) {
        creditCardTextChangeListener.onNext(appendNext)
    }

    override fun onRoundCompleted(currentContent: String) {
        if (isValid && currentContent.length == maxLength && parsedValidDate != null) {
            val calendar = Calendar.getInstance()
            calendar.time = parsedValidDate

            creditCardTextChangeListener.onCardDateEntered(
                true,
                calendar.get(Calendar.MONTH) + MONTH_CURRECTION_DELTA,
                calendar.get(Calendar.YEAR) % TWO_DIGIT_YEAR_EXTRACTOR_MODULO
            )
        } else {
            creditCardTextChangeListener.onCardDateEntered()
        }
    }

    companion object {
        private const val MONTH_CURRECTION_DELTA = 1
        private const val DIVIDER_INDEX = 2
        private const val TWO_DIGIT_YEAR_EXTRACTOR_MODULO = 100
        private val FIXABLE_MONTH_RANGE = 2..9
        private const val DATE_MAX_LENGTH = 4
    }
}