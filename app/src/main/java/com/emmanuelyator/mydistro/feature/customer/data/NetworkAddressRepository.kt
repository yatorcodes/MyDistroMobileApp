package com.emmanuelyator.mydistro.feature.customer.data

import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.model.DeliveryAddress
import com.emmanuelyator.mydistro.feature.customer.domain.AddressRepository
import javax.inject.Inject
import javax.inject.Singleton

/** Saved addresses from `GET/POST /api/users/me/addresses`. */
@Singleton
class NetworkAddressRepository @Inject constructor(
    private val api: CustomerApiService
) : AddressRepository {

    override suspend fun list(): DataResult<List<DeliveryAddress>> = try {
        val response = api.getAddresses()
        if (response.isSuccessful) {
            DataResult.Success(response.body().orEmpty().mapNotNull { it.toDeliveryAddress() })
        } else {
            response.toFailure("Your addresses could not be loaded.")
        }
    } catch (e: Exception) {
        e.toConnectionFailure()
    }

    override suspend fun add(address: DeliveryAddress): DataResult<DeliveryAddress> = try {
        val response = api.createAddress(
            CustomerAddressRequest(
                label = address.label.trim(),
                recipientName = address.recipientName.trim(),
                phoneNumber = address.phoneNumber.trim(),
                addressLine = address.addressLine.trim(),
                city = address.city.trim().ifEmpty { null },
                county = address.county.trim().ifEmpty { null },
                latitude = address.latitude,
                longitude = address.longitude,
                defaultAddress = address.isDefault
            )
        )
        val saved = response.body()?.toDeliveryAddress()
        if (response.isSuccessful && saved != null) {
            DataResult.Success(saved)
        } else {
            response.toFailure("The address could not be saved.")
        }
    } catch (e: Exception) {
        e.toConnectionFailure()
    }
}

internal fun CustomerAddressDto.toDeliveryAddress(): DeliveryAddress? {
    val line = addressLine?.takeIf { it.isNotBlank() } ?: return null
    return DeliveryAddress(
        id = id,
        label = label.orEmpty().ifBlank { "Address" },
        recipientName = recipientName.orEmpty(),
        phoneNumber = phoneNumber.orEmpty(),
        addressLine = line,
        city = city.orEmpty(),
        county = county.orEmpty(),
        latitude = latitude,
        longitude = longitude,
        isDefault = defaultAddress
    )
}
