package com.tenbis.support.listeners

interface MaskFormatterWatcherListener {
    /**
     * Called when a text change round has completed
     */
    fun onRoundCompleted(currentContent: String)

    /**
     * When the previous field is requested
     */
    fun onPrevious() = Unit

    /**
     * When the next field is requested
     *
     * @param appendNext when the entered text has too many characters we truncate the text
     * and pass the extra char. If no extra chars have been typed this will be @null
     */
    fun onNext(appendNext: Char? = null) = Unit
}