package com.tenbis.library.views

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.tenbis.library.R
import com.tenbis.library.consts.CardType
import com.tenbis.library.listeners.CreditCardTextChangeListener
import com.tenbis.library.listeners.OnCreditCardStateChanged
import com.tenbis.library.models.CreditCard
import com.tenbis.library.watchers.CreditCardCvvMaskWatcher
import com.tenbis.library.watchers.CreditCardDateMaskWatcher
import com.tenbis.library.watchers.CreditCardNumberMaskWatcher

class CompactCreditCardInput @JvmOverloads constructor(
    context: Context,
    val attrs: AttributeSet? = null,
    val defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),
    CreditCardTextChangeListener,
    LifecycleObserver {

    private var defaultLabelTextColor = ContextCompat.getColor(context, DEFAULT_LABEL_COLOR)
    private var defaultTextColor = ContextCompat.getColor(context, DEFAULT_TEXT_COLOR)
    private var defaultFieldsBackgroundColor =
        ContextCompat.getColor(context, DEFAULT_FIELDS_BACKGROUND_COLOR)

    private val inputManager: InputMethodManager? = if (!isInEditMode)
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager else null

    private val cardNumberTextWatcher = CreditCardNumberMaskWatcher(this)
    private val cardDateTextWatcher = CreditCardDateMaskWatcher(this)
    private val cardCvvTextWatcher = CreditCardCvvMaskWatcher(this)

    private val listeners: MutableSet<OnCreditCardStateChanged> = HashSet()

    private var creditCard = CreditCard()
    private var cardNumberValid = false
    private var cardDateValid = false
    private var cardCvvValid = false

    val root: View = LayoutInflater.from(context)
        .inflate(R.layout.view_compact_credit_input, this, true)

    val cardRoot: ConstraintLayout =
        root.findViewById(R.id.view_compat_credit_input_card_root)

    val label: AppCompatTextView =
        root.findViewById(R.id.view_compat_credit_input_card_label)

    val cardTypeImage: AppCompatImageView =
        root.findViewById(R.id.view_compat_credit_input_card_type_image)

    val cardNumberInput: AppCompatEditText =
        root.findViewById(R.id.view_compat_credit_input_card_number_input)

    val cardExpirationDateInput: AppCompatEditText =
        root.findViewById(R.id.view_compat_credit_input_card_date_input)

    val cardCvvNumberInput: AppCompatEditText =
        root.findViewById(R.id.view_compat_credit_input_card_cvv_input)

    var cardBackground: Drawable =
        ContextCompat.getDrawable(context, R.drawable.background_input_field)!!
        set(value) {
            field = value
            cardRoot.background = value
        }

    var cardNumberBackgroundColor: Int = defaultFieldsBackgroundColor
        set(value) {
            field = value
            cardNumberInput.setBackgroundColor(value)
        }

    var cardDateBackgroundColor: Int = defaultFieldsBackgroundColor
        set(value) {
            field = value
            cardExpirationDateInput.setBackgroundColor(value)
        }

    var cardCvvBackgroundColor: Int = defaultFieldsBackgroundColor
        set(value) {
            field = value
            cardCvvNumberInput.setBackgroundColor(value)
        }

    var labelText: String = ""
        set(value) {
            field = value
            label.text = value
        }

    var labelTextColor: Int = defaultLabelTextColor
        set(value) {
            field = value
            label.setTextColor(value)
        }

    var labelTextFont: String? = DEFAULT_LABEL_FONT
        set(value) {
            field = value
            label.typeface = Typeface.createFromAsset(resources.assets, value)
        }

    var textFont: String? = DEFAULT_TEXT_FONT
        set(value) {
            field = value
            val typeface = Typeface.createFromAsset(resources.assets, value)
            cardNumberInput.typeface = typeface
            cardExpirationDateInput.typeface = typeface
            cardCvvNumberInput.typeface = typeface
        }

    var textColor: Int = defaultTextColor
        set(value) {
            field = value
            cardNumberInput.setTextColor(value)
            cardExpirationDateInput.setTextColor(value)
            cardCvvNumberInput.setTextColor(value)
        }

    var hintColor: Int = -1
        set(value) {
            if (value == -1) return
            field = value
            cardNumberInput.setHintTextColor(value)
            cardExpirationDateInput.setHintTextColor(value)
            cardCvvNumberInput.setHintTextColor(value)
        }

    var cardNumberHint: String = context.getString(R.string.credit_card_input_number_hint)
        set(value) {
            field = value
            cardNumberInput.hint = value
        }

    var cardDateHint: String = context.getString(R.string.credit_card_input_date_hint)
        set(value) {
            field = value
            cardExpirationDateInput.hint = value
        }

    var cardCvvHint: String = context.getString(R.string.credit_card_input_cvv_hint)
        set(value) {
            field = value
            cardCvvNumberInput.hint = value
        }

    var closeKeyboardOnValidCard: Boolean = true

    init {
        initializeAttributes()

        cardNumberInput
            .addTextChangedListener(cardNumberTextWatcher)

        cardExpirationDateInput
            .addTextChangedListener(cardDateTextWatcher)

        cardExpirationDateInput
            .setOnKeyListener(cardDateTextWatcher)

        cardCvvNumberInput
            .addTextChangedListener(cardCvvTextWatcher)

        cardCvvNumberInput
            .setOnKeyListener(cardCvvTextWatcher)
    }

    /**
     * Makes the view aware to the owner lifecycle
     * this allows the view to automatically clear resources
     */
    fun attachLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    /**
     * Subscribe to events so you can know if the card is valid or not
     *
     * @see onDestroy removes all listeners
     */
    fun addOnCreditCardStateChangedListener(onCreditCardStateChanged: OnCreditCardStateChanged) {
        listeners.add(onCreditCardStateChanged)
    }

    /**
     * Unsubscribe from events
     */
    fun removeOnCreditCardStateChangedListener(onCreditCardStateChanged: OnCreditCardStateChanged) {
        listeners.remove(onCreditCardStateChanged)
    }

    override fun onCardTypeFound(cardType: CardType) {
        cardCvvTextWatcher.cardType = cardType

        cardTypeImage.setImageResource(getCardTypeImageId(cardType))

        listeners.forEach { it.onCreditCardTypeFound(cardType) }
    }

    override fun onCardNumberEntered(cardNumber: String?, completed: Boolean) {
        if (cardNumber != null) {
            creditCard.cardNumber = cardNumber
            if (completed) {
                cardExpirationDateInput.requestFocus()
            }
            listeners.forEach {
                it.onCreditCardNumberValid(cardNumber)
            }
            cardNumberValid = true
            invalidateSubmission()
        } else {
            if (!cardNumberValid) return
            cardNumberValid = false
            invalidateSubmission()
        }
    }

    override fun onCardDateEntered(dateValid: Boolean, month: Int, year: Int) {
        if (!dateValid) {
            if (!cardDateValid) return
            cardDateValid = false
            invalidateSubmission()
            return
        }
        cardCvvNumberInput.requestFocus()
        creditCard.expiryMonth = month
        creditCard.expiryYear = year
        cardDateValid = true
        listeners.forEach {
            it.onCreditCardExpirationDateValid(month, year)
        }
        invalidateSubmission()
    }

    override fun onCardCVVEntered(cvv: String?, completed: Boolean) {
        if (cvv == null) {
            if (!cardCvvValid) return
            cardCvvValid = false
            invalidateSubmission()
            return
        }

        if (closeKeyboardOnValidCard && completed) {
            inputManager?.hideSoftInputFromWindow(windowToken, 0)
        }

        creditCard.cvv = cvv
        cardCvvValid = true
        listeners.forEach {
            it.onCreditCardCvvValid(cvv)
        }
        invalidateSubmission()
    }

    override fun onNext(appendChar: Char?) {
        val current = cardRoot.focusedChild as? EditText ?: return
        val shouldAppendChar = current.selectionStart == current.length()

        val child = cardRoot.shiftFocus(1) as? EditText ?: return
        child.setSelection(0)

        if (appendChar != null && shouldAppendChar) {
            child.text.insert(0, StringBuilder().append(appendChar))
        }
    }

    override fun onPrevious() {
        val child = cardRoot.shiftFocus(-1) as? EditText ?: return
        child.setSelection(child.text.length)
    }

    /**
     * Initialize the views based on the provided [attrs]
     */
    private fun initializeAttributes() {
        context.obtainStyledAttributes(attrs, R.styleable.CompactCreditCardInput, defStyleAttr, 0)
            ?.let {
                it.getDrawable(R.styleable.CompactCreditCardInput_card_background)
                    ?.let { background ->
                        cardBackground = background
                    }

                cardNumberBackgroundColor = it.getColor(
                    R.styleable.CompactCreditCardInput_card_number_background_color,
                    defaultFieldsBackgroundColor
                )

                cardDateBackgroundColor = it.getColor(
                    R.styleable.CompactCreditCardInput_card_date_background_color,
                    defaultFieldsBackgroundColor
                )

                cardCvvBackgroundColor = it.getColor(
                    R.styleable.CompactCreditCardInput_card_cvv_background_color,
                    defaultFieldsBackgroundColor
                )

                it.getString(R.styleable.CompactCreditCardInput_label_text)?.let { label ->
                    labelText = label
                }

                labelTextColor =
                    it.getColor(
                        R.styleable.CompactCreditCardInput_label_text_color,
                        defaultLabelTextColor
                    )

                it.getString(R.styleable.CompactCreditCardInput_label_text_font)?.let { font ->
                    labelTextFont = font
                }

                it.getString(R.styleable.CompactCreditCardInput_text_font)?.let { font ->
                    textFont = font
                }

                textColor =
                    it.getColor(R.styleable.CompactCreditCardInput_text_color, defaultTextColor)

                hintColor = it.getColor(R.styleable.CompactCreditCardInput_hint_color, -1)

                it.getString(R.styleable.CompactCreditCardInput_card_number_hint)?.let { hint ->
                    cardNumberHint = hint
                }

                it.getString(R.styleable.CompactCreditCardInput_card_date_hint)?.let { hint ->
                    cardDateHint = hint
                }

                it.getString(R.styleable.CompactCreditCardInput_card_cvv_hint)?.let { hint ->
                    cardCvvHint = hint
                }

                it.recycle()
            }
        invalidate()
    }

    @DrawableRes
    fun getCardTypeImageId(cardType: CardType): Int {
        return when (cardType) {
            CardType.UNKNOWN ->
                R.drawable.ic_card_default

            CardType.DINERS_CLUB ->
                R.drawable.ic_card_diners

            CardType.DISCOVER ->
                R.drawable.ic_card_discover

            CardType.AMERICAN_EXPRESS ->
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
    }

    /**
     * Shifts the focus of the currently focused child
     *
     * @receiver Parent of the views
     * @param delta used to calculate what position should be focused next
     * positive numbers will shift the focus forward
     * negative numbers will shift the focus backward
     *
     * @return the currently focused child or null if the shift is out of range
     */
    private fun ViewGroup.shiftFocus(delta: Int): View? {
        val focusedChildIndex = indexOfChild(focusedChild)
        val nextIndex = focusedChildIndex + delta

        if (nextIndex in 0 until childCount) {
            val child = getChildAt(nextIndex)
            child.requestFocus()
            return child
        }
        return null
    }

    /**
     * Notifies the listeners on evey change
     */
    private fun invalidateSubmission() {
        if (cardNumberValid && cardDateValid && cardCvvValid) {
            listeners.forEach {
                it.onCreditCardValid(creditCard)
            }
        } else {
            listeners.forEach {
                it.onInvalidCardTyped()
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState() ?: return null
        val savedState = SavedState(superState)
        savedState.creditCard = creditCard

        val focusedInputField = cardRoot.focusedChild as? EditText

        focusedInputField?.let {
            savedState.focusedChildIndex = cardRoot.indexOfChild(it)
        }
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null) return

        val savedState = state as? SavedState ?: return

        creditCard = savedState.creditCard

        cardNumberInput.setText(savedState.cardNumberString)
        cardExpirationDateInput.setText(savedState.cardDateString)
        cardCvvNumberInput.setText(savedState.cardCvvString)

        cardNumberValid = savedState.cardNumberValid
        cardDateValid = savedState.cardDateValid
        cardCvvValid = savedState.cardCvvValid

        if (savedState.focusedChildIndex != -1) {
            val focusedInputField = cardRoot.getChildAt(savedState.focusedChildIndex) as EditText

            focusedInputField.requestFocus()
        }

        invalidateSubmission()

        super.onRestoreInstanceState(savedState.superState)
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        outAttrs?.imeOptions = EditorInfo.IME_ACTION_NEXT
        return super.onCreateInputConnection(outAttrs)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        listeners.clear()

        cardTypeImage.setImageDrawable(null)

        cardNumberInput.removeTextChangedListener(cardNumberTextWatcher)

        cardExpirationDateInput.removeTextChangedListener(cardDateTextWatcher)

        cardExpirationDateInput.setOnKeyListener(null)

        cardCvvNumberInput.removeTextChangedListener(cardCvvTextWatcher)

        cardCvvNumberInput.setOnKeyListener(null)
    }

    private class SavedState : BaseSavedState {

        var creditCard: CreditCard = CreditCard()
        var cardNumberString: String = ""
        var cardDateString: String = ""
        var cardCvvString: String = ""

        var cardNumberValid = false
        var cardDateValid = false
        var cardCvvValid = false

        var focusedChildIndex: Int = -1

        constructor(superState: Parcelable) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            creditCard = parcel.readParcelable(javaClass.classLoader) ?: CreditCard()

            cardNumberString = parcel.readString() ?: ""
            cardDateString = parcel.readString() ?: ""
            cardCvvString = parcel.readString() ?: ""

            cardNumberValid = parcel.readInt() == 1
            cardDateValid = parcel.readInt() == 1
            cardCvvValid = parcel.readInt() == 1

            focusedChildIndex = parcel.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeParcelable(creditCard, 0)

            out.writeString(cardNumberString)
            out.writeString(cardDateString)
            out.writeString(cardCvvString)

            out.writeInt(if (cardNumberValid) 1 else 0)
            out.writeInt(if (cardDateValid) 1 else 0)
            out.writeInt(if (cardCvvValid) 1 else 0)

            out.writeInt(focusedChildIndex)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {

            override fun createFromParcel(parcel: Parcel): SavedState =
                SavedState(parcel)

            override fun newArray(size: Int): Array<SavedState?> =
                arrayOfNulls(size)
        }
    }

    companion object {
        private const val DEFAULT_LABEL_FONT = "assets/fonts/takeaway_sans_bold"
        private const val DEFAULT_TEXT_FONT = "assets/fonts/takeaway_sans_regular"

        private val DEFAULT_LABEL_COLOR = R.color.dark_blue
        private val DEFAULT_TEXT_COLOR = R.color.medium_grey
        private val DEFAULT_FIELDS_BACKGROUND_COLOR = R.color.white
    }
}
