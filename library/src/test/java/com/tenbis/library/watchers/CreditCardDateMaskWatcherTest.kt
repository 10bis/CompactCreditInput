package com.tenbis.library.watchers

import com.tenbis.library.listeners.CreditCardTextChangeListener
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import android.app.Activity
import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.robolectric.Robolectric
import kotlin.test.assertEquals
import android.text.SpannableStringBuilder
import android.text.TextWatcher


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [17])
class CreditCardDateMaskWatcherTest {

    private lateinit var textChangeListener: CreditCardTextChangeListener

    private lateinit var dateMaskWathcer: CreditCardDateMaskWatcher

    private lateinit var activity: Activity

    private lateinit var editText: EditText

    @Before
    fun setup() {
        activity = Robolectric.buildActivity(Activity::class.java)
            .create()
            .resume()
            .get()

        textChangeListener = mock()
        dateMaskWathcer = spy(CreditCardDateMaskWatcher(textChangeListener))
        editText = EditText(activity)
        editText.addTextChangedListener(dateMaskWathcer)
        editText.setOnKeyListener(dateMaskWathcer)
    }

    @Test
    fun first_number_is_between_2_9_appends_0_before_char() {
        assertEquals("05", simulateTextInput(dateMaskWathcer, "", "5"))
    }

    @Test
    fun date_typed_returns_as_valid_int() {
        editText.setText("1223")
        verify(textChangeListener, times(1))
            .onCardDateEntered(true, 12, 23)
    }

    private fun simulateTextInput(tw: TextWatcher, original: String, input: String): String {
        // TODO review all the int parameters
        tw.beforeTextChanged(original, 0, original.length, input.length)

        val newText = original + input
        tw.onTextChanged(newText, 1, newText.length - 1, 1)


        val editable = SpannableStringBuilder(newText)
        tw.afterTextChanged(editable)
        return editable.toString()
    }
}