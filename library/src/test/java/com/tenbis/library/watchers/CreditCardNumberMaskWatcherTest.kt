package com.tenbis.library.watchers

import com.tenbis.library.listeners.CreditCardTextChangeListener
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.tenbis.library.consts.CardType
import android.app.Activity
import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.robolectric.Robolectric
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [17])
class CreditCardNumberMaskWatcherTest {

    private lateinit var textChangeListener: CreditCardTextChangeListener

    private lateinit var numberMaskWathcer: CreditCardNumberMaskWatcher

    private lateinit var activity: Activity

    private lateinit var editText: EditText


    @Before
    fun setup() {
        activity = Robolectric.buildActivity(Activity::class.java)
            .create()
            .resume()
            .get()

        textChangeListener = mock()
        numberMaskWathcer = spy(CreditCardNumberMaskWatcher(textChangeListener))
        editText = EditText(activity)
        editText.addTextChangedListener(numberMaskWathcer)
    }

    @Test
    fun `test_two_digits_typed_call_onCardTypeFound_with_visa`() {
        editText.setText("47")
        verify(textChangeListener, times(1))
            .onCardTypeFound(CardType.VISA)
    }

    @Test
    fun `test_two_digits_typed_call_onCardTypeFound_with_mastercard`() {
        editText.setText("54")
        verify(textChangeListener, times(1))
            .onCardTypeFound(CardType.MASTERCARD)
    }

    @Test
    fun `test_two_digits_typed_call_onCardTypeFound_with_amex`() {
        editText.setText("37")
        verify(textChangeListener, times(1))
            .onCardTypeFound(CardType.AMERICAN_EXPRESS)
    }

    @Test
    fun `test_complete_credit_card_number_calles_onCardNumberEntered`() {
        editText.setText("374558721489487")
        verify(textChangeListener, times(1))
            .onCardNumberEntered("374558721489487", true)
    }

    @Test
    fun `test_complete_plus_one_credit_card_number_calles_onNext_with_appendable_char`() {
        editText.setText("3745587214894875")
        verify(textChangeListener, times(1))
            .onNext('5')
    }

    @Test
    fun `test_visa_credit_card_apply_mask_called_three_times`() {
        editText.setText("4580742319820962")
        val captur = ArgumentCaptor.forClass(Int::class.java)

        verify(numberMaskWathcer, times(3))
            .applyMask(any(), captur.capture())

        val arguments = captur.allValues
        assertEquals(4, arguments[0])
        assertEquals(8, arguments[1])
        assertEquals(12, arguments[2])
    }

    @Test
    fun `test_amex_credit_card_apply_mask_called_two_times`() {
        editText.setText("374558721489487")
        val captur = ArgumentCaptor.forClass(Int::class.java)

        verify(numberMaskWathcer, times(2))
            .applyMask(any(), captur.capture())

        val arguments = captur.allValues
        assertEquals(4, arguments[0])
        assertEquals(10, arguments[1])
    }
}