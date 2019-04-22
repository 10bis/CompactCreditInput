package com.tenbis.support.consts

/**
 * Represents a credit card type
 *
 * @param numberLength the amount of numbers a full card contains
 * @param cvvLength the amount of numbers a full card cvv contains
 */
enum class CardType(
    val numberLength: Int = -1,
    val cvvLength: Int = -1,
    val cardSpacingPattern: Set<Int> = hashSetOf(4, 8, 12)
) {

    /**
     * American Express cards start in 34 or 37
     * And look like this **** ****** *****
     */
    AMERICAN_EXPRESS(15, 4, hashSetOf(4, 10)),

    /**
     * Diners Club
     */
    DINERS_CLUB(14, 3, hashSetOf(4, 10)),

    /**
     * Discover starts with 6x for some values of x.
     */
    DISCOVER(16, 3),
    /**
     * JCB (see http://www.jcbusa.com/) cards start with 35
     */
    JCB(16, 3),
    /**
     * Mastercard starts with 51-55
     */
    MASTERCARD(16, 3),
    /**
     * Visa starts with 4
     */
    VISA(16, 3),
    /**
     * Maestro
     */
    MAESTRO(16, 3),
    /**
     * Unknown card type.
     */
    UNKNOWN(-1, -1, emptySet());

    companion object {
        private val intervalLookup: HashMap<Pair<String, String>, CardType> = hashMapOf(
            "4" to "4" to VISA,
            "2221" to "2720" to MASTERCARD,
            "51" to "55" to MASTERCARD,
            "300" to "305" to DINERS_CLUB,
            "309" to "309" to DINERS_CLUB,
            "36" to "36" to DINERS_CLUB,
            "38" to "39" to DINERS_CLUB,
            "34" to "34" to AMERICAN_EXPRESS,
            "37" to "37" to AMERICAN_EXPRESS,
            "3528" to "3589" to JCB,
            "50" to "50" to MAESTRO,
            "56" to "59" to MAESTRO,
            "61" to "61" to MAESTRO,
            "63" to "63" to MAESTRO,
            "66" to "69" to MAESTRO,
            "6011" to "6011" to DISCOVER,
            "62" to "62" to DISCOVER,
            "644" to "649" to DISCOVER,
            "65" to "65" to DISCOVER,
            "88" to "88" to DISCOVER
        )

        /**
         * Determine if a number matches a prefix interval
         *
         * @param number credit card number
         * @param intervalStart prefix (e.g. "4") or prefix interval start (e.g. "51")
         * @param intervalEnd prefix interval end (e.g. "55") or null for non-intervals
         */
        private fun isNumberInInterval(
            number: String,
            intervalStart: String,
            intervalEnd: String
        ): Boolean {

            val numCompareStart = Math.min(number.length, intervalStart.length)
            val numCompareEnd = Math.min(number.length, intervalEnd.length)

            val numberStartValue = number.substring(0, numCompareStart).toInt()
            val numberEndValue = number.substring(0, numCompareEnd).toInt()

            val intervalStartValue = intervalStart.substring(0, numCompareStart).toInt()
            val intervalEndValue = intervalEnd.substring(0, numCompareEnd).toInt()

            return !(numberStartValue < intervalStartValue || numberEndValue > intervalEndValue)
        }

        /**
         * Infer the CardType from the number string.
         * for these ranges (last checked: 19 Feb 2013)
         *
         * @param cardNumber A string containing only the card number.
         * @return the inferred card type
         */
        fun fromCardNumber(cardNumber: String): CardType {
            if (cardNumber.isEmpty()) return CardType.UNKNOWN

            val possibleCardTypes = HashSet<CardType>()

            for ((key, value) in intervalLookup.entries) {
                if (!isNumberInInterval(cardNumber, key.first, key.second)) continue
                possibleCardTypes.add(value)
            }

            return when {
                possibleCardTypes.size > 1 ->
                    UNKNOWN

                possibleCardTypes.size == 1 ->
                    possibleCardTypes.iterator().next()

                else ->
                    UNKNOWN
            }
        }
    }
}