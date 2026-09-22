package com.emmanuelyator.mydistro.core.common

/**
 * Input validation shared by the driver and customer auth screens.
 *
 * This is client-side convenience only — it exists to give immediate feedback,
 * never to enforce a rule. The backend must re-validate everything.
 */
object Validators {

    /**
     * Kenyan mobile numbers, accepting the formats people actually type:
     * `0712345678`, `712345678`, `+254712345678`, `254712345678`.
     * Safaricom/Airtel/Telkom prefixes all begin 01 or 07 in local form.
     */
    private val KENYAN_PHONE = Regex("^(?:\\+?254|0)?(?:1|7)\\d{8}$")

    private val EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isValidKenyanPhone(input: String): Boolean =
        KENYAN_PHONE.matches(input.normalisePhoneInput())

    fun isValidEmail(input: String): Boolean = EMAIL.matches(input.trim())

    /** Drivers may sign in with either, so the field accepts both. */
    fun isValidPhoneOrEmail(input: String): Boolean =
        isValidKenyanPhone(input) || isValidEmail(input)

    /**
     * Converts any accepted local format to `2547XXXXXXXX` for the API. Returns
     * the trimmed input unchanged if it isn't a recognisable phone number.
     */
    fun toE164Kenya(input: String): String {
        val digits = input.normalisePhoneInput()
        if (!KENYAN_PHONE.matches(digits)) return input.trim()
        val national = digits.removePrefix("+").removePrefix("254").trimStart('0')
        return "254$national"
    }

    private fun String.normalisePhoneInput(): String =
        trim().replace(" ", "").replace("-", "")
}

/** Password rules the prototype enforces locally; the backend owns the real policy. */
object PasswordRules {
    const val MIN_LENGTH = 6

    fun validate(password: String): String? = when {
        password.isEmpty() -> "Enter your password."
        password.length < MIN_LENGTH -> "Password must be at least $MIN_LENGTH characters."
        else -> null
    }
}
