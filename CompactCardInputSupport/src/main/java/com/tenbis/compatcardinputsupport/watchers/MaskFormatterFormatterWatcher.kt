package com.tenbis.compatcardinputsupport.watchers

import android.support.annotation.CallSuper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import com.tenbis.compatcardinputsupport.listeners.MaskFormatterWatcherListener

abstract class MaskFormatterFormatterWatcher : TextWatcher, View.OnKeyListener,
    MaskFormatterWatcherListener {

    abstract var maxLength: Int
    private var deleteActionIntercepted = false
    private var isValid: Boolean = false
    private var internalStopFlag: Boolean = false

    open var errorAllowanceLevel: AllowanceLevel = AllowanceLevel.ALLOW_ALL

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        deleteActionIntercepted = after == 0 && start == 0
    }

    @CallSuper
    override fun onTextChanged(content: CharSequence?, start: Int, before: Int, count: Int) {
        if (content == null) return
        isValid = validateContent(content)
    }

    @CallSuper
    override fun afterTextChanged(editable: Editable?) {
        if (editable == null || internalStopFlag) return

        if (!validate(editable.length, editable)) return

        format(editable, editable.length)
    }
    
    private fun validate(textLength: Int, editable: Editable): Boolean {
        if (!isValid) {
            return when (errorAllowanceLevel) {

                AllowanceLevel.DISALLOW_CURRENT -> {
                    if (textLength == 0) return true
                    editable.truncate()
                    true
                }

                AllowanceLevel.ALLOW_ALL ->
                    onInvalidContent(editable)

                AllowanceLevel.SUGGEST_FIX -> {
                    if (textLength == 0) return true
                    internalStopFlag = true
                    val fixValidate = onInvalidContent(editable, true)
                    internalStopFlag = false
                    fixValidate
                }
            }
        }

        return true
    }

    private fun format(editable: Editable, textLength: Int) {
        var truncatedChar: Char? = null
        internalStopFlag = true
        removeAppliedMask(editable)

        if (maxLength >= 1 && maxLength <= textLength - 1) {
            truncatedChar = editable.truncate()
        }

        for (i in 1..textLength) {
            if (!shouldAppendMask(i)) continue
            applyMask(editable, i)
        }

        if (truncatedChar != null) {
            onNext(truncatedChar)
        }
        onRoundCompleted(editable.toString())
        internalStopFlag = false
    }

    override fun onKey(view: View, keyCode: Int, event: KeyEvent?): Boolean {
        if (view !is EditText) return true

        when (keyCode) {
            KeyEvent.KEYCODE_DEL -> {
                if (deleteActionIntercepted) {
                    deleteActionIntercepted = false
                    return true
                }
                if (view.text.isEmpty() || (view.selectionStart == 0 && view.selectionEnd == 0)) {
                    onPrevious()
                    return true
                }
            }

            KeyEvent.KEYCODE_ENTER -> {
               onNext()
                return true
            }
        }
        return false
    }

    /**
     * Removes the last char from the content
     * @return the char the was removed
     */
    fun Editable.truncate(): Char {
        val lastChar = get(length - 1)
        replace(length - 1, length, "")
        return lastChar
    }


    /**
     * Removes currently applied mask in order to update the mask
     */
    abstract fun removeAppliedMask(editable: Editable)

    /**
     * Verify if we need to apply a mask to the current index
     *
     * @param index the index in the target
     */
    abstract fun shouldAppendMask(index: Int): Boolean

    /**
     * Validates the current content while it is changed
     *
     * @param content the current text
     * @return true if valid else false
     */
    abstract fun validateContent(content: CharSequence): Boolean

    /**
     * Calls when an invalid content was detected on [onTextChanged]
     * this action will be executed on [afterTextChanged]
     *
     * @param editable the target that we want to show the invalid content
     */
    abstract fun onInvalidContent(editable: Editable, suggestFix: Boolean = false): Boolean

    /**
     * Applies the mask to the field
     *
     * @param editable mask target
     * @param index index to apply mask on
     */
    abstract fun applyMask(editable: Editable, index: Int)

}

enum class AllowanceLevel {
    ALLOW_ALL,
    DISALLOW_CURRENT,
    SUGGEST_FIX
}