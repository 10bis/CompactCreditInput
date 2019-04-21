package com.tenbis.compatcardinputsupport.views

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.tenbis.compatcardinputsupport.R
import com.tenbis.compatcardinputsupport.consts.CardType
import com.tenbis.compatcardinputsupport.listeners.CreditCardTextChangeListener
import com.tenbis.compatcardinputsupport.models.CreditCard
import com.tenbis.compatcardinputsupport.watchers.CreditCardCvvMaskWatcher
import com.tenbis.compatcardinputsupport.watchers.CreditCardNumberMaskWatcher
import com.tenbis.tbapp.refactor.fragments.card.add.watchers.CreditCardDateMaskWatcher

class CompatCreditInput(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAtr: Int = -1
) : LinearLayoutCompat(context, attrs, defStyleAtr),
    CreditCardTextChangeListener, LifecycleObserver {

    private val inputManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    private val cardNumberTextWatcher = CreditCardNumberMaskWatcher(this)
    private val cardDateTextWatcher = CreditCardDateMaskWatcher(this)
    private val cardCvvTextWatcher = CreditCardCvvMaskWatcher(this)

    private var creditCard = CreditCard()
    private var cardNumberValid = false
    private var cardDateValid = false
    private var cardCvvValid = false

    private val root: LinearLayoutCompat = findViewById(R.id.view_compat_credit_input_root)
    private val cardRoot: LinearLayoutCompat = findViewById(R.id.view_compat_credit_input_card_root)
    private val label: TextView = findViewById(R.id.view_compat_credit_input_card_label)

    private val cardNumberInput: EditText =
        findViewById(R.id.view_compat_credit_input_card_number_input)

    private val expirationDateInput: EditText =
        findViewById(R.id.view_compat_credit_input_card_date_input)

    private val cvvNumberInput: EditText =
        findViewById(R.id.view_compat_credit_input_card_cvv_input)


    init {
        cardNumberInput
            .addTextChangedListener(cardNumberTextWatcher)

        expirationDateInput
            .addTextChangedListener(cardDateTextWatcher)

        expirationDateInput
            .setOnKeyListener(cardDateTextWatcher)

        cvvNumberInput
            .addTextChangedListener(cardCvvTextWatcher)

        cvvNumberInput
            .setOnKeyListener(cardCvvTextWatcher)

        root.requestFocus()
        showKeyboard()
    }

    fun attachLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    override fun onCardTypeFound(cardType: CardType) {
        val cardTypeIconRes = when (cardType) {
            CardType.INSUFFICIENT_DIGITS,
            CardType.UNKNOWN ->
                R.drawable.ic_card_default

            CardType.DINERSCLUB ->
                R.drawable.ic_card_diners

            CardType.DISCOVER ->
                R.drawable.ic_card_discover

            CardType.AMEX ->
                R.drawable.ic_card_amex

            CardType.MASTERCARD ->
                R.drawable.ic_card_mastercard

            CardType.VISA ->
                R.drawable.ic_card_visa

            CardType.JCB ->
                R.drawable.ic_card_jcb

            CardType.MAESTRO ->
                R.drawable.ic_card_maestro
        }

        add_credit_card_fragment_card_type_image.setImageResource(cardTypeIconRes)
        creditCardCvvTextWatcher.cardType = cardType
    }

    override fun onCardNumberEntered(cardNumber: String, completed: Boolean) {
        Timber.w("Card number entered: $completed")
        creditCard.cardNumber = cardNumber
        if (completed) {
            add_credit_card_fragment_card_date_input.requestFocus()
        }
        cardNumberValid = true
        invalidateSubmission()
    }

    override fun onInvalidCardNumberEntered() {
        cardNumberValid = false
        invalidateSubmission()
    }

    override fun onCardValidDateEntered(month: Int, year: Int) {
        add_credit_card_fragment_card_cvv_input.requestFocus()
        creditCard.expiryMonth = month
        creditCard.expiryYear = year
        cardDateValid = true
        invalidateSubmission()
    }

    override fun onCardInvalidDateEntered() {
        cardDateValid = false
        invalidateSubmission()
    }

    override fun onCardCVVEntered(cvv: String, completed: Boolean) {
        if (completed) {
            add_credit_card_fragment_add_card_button.requestFocus()
            hideKeyboard()
        }
        creditCard.cvv = cvv
        cardCvvValid = true
        invalidateSubmission()
    }

    override fun onInvalidCardCVVEntered() {
        cardCvvValid = false
        invalidateSubmission()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState() ?: return null
        val savedState = SavedState(superState)
        savedState.creditCard = creditCard

        val focusedInputField = cardRoot.focusedChild as? EditText

        focusedInputField?.let {
            savedState.focusedChildIndex = cardRoot.indexOfChild(it)
            savedState.selectionStartIndex = it.selectionStart
            savedState.selectionEndIndex = it.selectionEnd
        }
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null) return

        val savedState = state as? SavedState ?: return

        creditCard = savedState.creditCard
        if (savedState.focusedChildIndex != -1) {
            val focusedInputField = cardRoot.getChildAt(savedState.focusedChildIndex) as EditText

            focusedInputField.requestFocus()

            focusedInputField.setSelection(
                savedState.selectionStartIndex,
                savedState.selectionStartIndex
            )
        }

        invalidateSubmission()

        super.onRestoreInstanceState(savedState.superState)
    }

    private fun invalidateSubmission() {
        add_credit_card_fragment_add_card_button.isEnabled =
            cardNumberValid && cardDateValid && cardCvvValid
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {

    }

    override fun onFocusNext(appendChar: Char?) {
        val child = shiftFocus(1) as? EditText ?: return
        child.setSelection(0)

        if (appendChar != null) {
            child.text.insert(0, StringBuilder().append(appendChar))
        }
    }

    override fun onFocusPrevious() {
        val child = shiftFocus(-1) as? EditText ?: return
        child.setSelection(child.text.length)
    }


    private fun shiftFocus(delta: Int): View? {
        val focusedChildIndex = cardRoot.indexOfChild(focusedChild)
        val nextIndex = focusedChildIndex + delta

        if (nextIndex in 0 until cardRoot.childCount) {
            val child = cardRoot.getChildAt(nextIndex)
            child.requestFocus()
            return child
        }
        return null
    }

    private fun showKeyboard() {
        inputManager.toggleSoftInputFromWindow(
            windowToken,
            InputMethodManager.SHOW_FORCED, 0
        )
    }

    private fun hideKeyboard() {
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private class SavedState : BaseSavedState {
        var creditCard: CreditCard = CreditCard()

        var focusedChildIndex: Int = -1
        var selectionStartIndex: Int = -1
        var selectionEndIndex: Int = -1

        constructor(superState: Parcelable) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            creditCard = parcel.readParcelable(javaClass.classLoader) ?: CreditCard()

            focusedChildIndex = parcel.readInt()
            selectionStartIndex = parcel.readInt()
            selectionEndIndex = parcel.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeParcelable(creditCard, 0)

            out.writeInt(focusedChildIndex)
            out.writeInt(selectionStartIndex)
            out.writeInt(selectionEndIndex)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {

            override fun createFromParcel(parcel: Parcel): SavedState =
                SavedState(parcel)

            override fun newArray(size: Int): Array<SavedState?> =
                arrayOfNulls(size)
        }
    }
}

