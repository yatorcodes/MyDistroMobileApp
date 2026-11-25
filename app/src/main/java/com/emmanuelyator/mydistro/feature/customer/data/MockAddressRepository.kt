package com.emmanuelyator.mydistro.feature.customer.data

import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.model.DeliveryAddress
import com.emmanuelyator.mydistro.feature.customer.domain.AddressRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay

/** Presentation-mode addresses. Matches the sample shops on the profile screen. */
@Singleton
class MockAddressRepository @Inject constructor() : AddressRepository {

    private var addresses = listOf(
        DeliveryAddress(
            id = "addr-1",
            label = "Main Shop",
            recipientName = "Jane Wanjiru",
            phoneNumber = "0712345678",
            addressLine = "Jane's Mini Mart, Moi Avenue",
            city = "Nairobi",
            county = "Nairobi",
            isDefault = true
        ),
        DeliveryAddress(
            id = "addr-2",
            label = "Branch 2",
            recipientName = "Jane Wanjiru",
            phoneNumber = "0712345678",
            addressLine = "Jane's Groceries, Woodvale Grove",
            city = "Westlands",
            county = "Nairobi"
        )
    )

    override suspend fun list(): DataResult<List<DeliveryAddress>> {
        delay(200)
        return DataResult.Success(addresses)
    }

    override suspend fun add(address: DeliveryAddress): DataResult<DeliveryAddress> {
        delay(300)
        val saved = address.copy(id = "addr-${addresses.size + 1}")
        addresses = if (saved.isDefault) {
            addresses.map { it.copy(isDefault = false) } + saved
        } else {
            addresses + saved
        }
        return DataResult.Success(saved)
    }
}
