package com.emmanuelyator.mydistro.core.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {

    @Test
    fun `accepts the local formats Kenyan drivers actually type`() {
        listOf(
            "0712345678",
            "0112345678",
            "712345678",
            "+254712345678",
            "254712345678",
            "0712 345 678",
            "0712-345-678"
        ).forEach { input ->
            assertTrue("expected $input to be valid", Validators.isValidKenyanPhone(input))
        }
    }

    @Test
    fun `rejects numbers of the wrong length or prefix`() {
        listOf(
            "071234567",      // one digit short
            "07123456789",    // one digit long
            "0812345678",     // 08 is not a mobile prefix
            "",
            "not a phone"
        ).forEach { input ->
            assertFalse("expected $input to be invalid", Validators.isValidKenyanPhone(input))
        }
    }

    @Test
    fun `normalises every accepted format to the same E164 value`() {
        val expected = "254712345678"
        listOf("0712345678", "712345678", "+254712345678", "254712345678", "0712 345 678")
            .forEach { input ->
                assertEquals(expected, Validators.toE164Kenya(input))
            }
    }

    @Test
    fun `leaves non-phone input untouched so emails survive normalisation`() {
        assertEquals(
            "alex.mwangi@mydistro.co.ke",
            Validators.toE164Kenya(" alex.mwangi@mydistro.co.ke ")
        )
    }

    @Test
    fun `driver identifier accepts both a phone number and an email`() {
        assertTrue(Validators.isValidPhoneOrEmail("0712345678"))
        assertTrue(Validators.isValidPhoneOrEmail("alex.mwangi@mydistro.co.ke"))
        assertFalse(Validators.isValidPhoneOrEmail("alex@"))
    }

    @Test
    fun `password must meet the minimum length`() {
        assertEquals("Enter your password.", PasswordRules.validate(""))
        assertTrue(PasswordRules.validate("abc")!!.contains("at least"))
        assertEquals(null, PasswordRules.validate("driver123"))
    }
}
