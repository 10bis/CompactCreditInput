package com.tenbis.compatcardinputsupport.views

import android.content.Context
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView

class CompatCreditInput(context: Context,
                        attrs: AttributeSet? = null,
                        defStyleAtr: Int = -1) : LinearLayoutCompat(context, attrs, defStyleAtr) {

    private val root: LinearLayoutCompat
    private val label: TextView
    private val cardRoot: LinearLayoutCompat


    init {

    }
}