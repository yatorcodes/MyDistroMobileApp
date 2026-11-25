package com.emmanuelyator.mydistro.feature.customer.domain

import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.model.DeliveryAddress

/** The customer's saved delivery addresses. */
interface AddressRepository {
    suspend fun list(): DataResult<List<DeliveryAddress>>

    /** Saves a new address and returns it with the id the server assigned. */
    suspend fun add(address: DeliveryAddress): DataResult<DeliveryAddress>
}
