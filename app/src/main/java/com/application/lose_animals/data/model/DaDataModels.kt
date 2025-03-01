package com.application.lose_animals.data.model

import com.google.gson.annotations.SerializedName

data class DaDataRequest(
    val query: String,
    val count: Int = 10
)

data class DaDataResponse(
    val suggestions: List<AddressSuggestion>
)

data class AddressSuggestion(
    val value: String,
    val unrestricted_value: String,
    val data: AddressData
)

data class AddressData(
    @SerializedName("postal_code") val postalCode: String?,
    val country: String?,
    @SerializedName("country_iso_code") val countryIsoCode: String?,
    @SerializedName("federal_district") val federalDistrict: String?,
    @SerializedName("region_fias_id") val regionFiasId: String?,
    @SerializedName("region_kladr_id") val regionKladrId: String?,
    @SerializedName("region_iso_code") val regionIsoCode: String?,
    @SerializedName("region_with_type") val regionWithType: String?,
    @SerializedName("region_type") val regionType: String?,
    @SerializedName("region_type_full") val regionTypeFull: String?,
    val region: String?,
    val city: String?,
    @SerializedName("city_with_type") val cityWithType: String?,
    val street: String?,
    @SerializedName("street_with_type") val streetWithType: String?,
    val house: String?,
    @SerializedName("geo_lat") val geoLat: String?,
    @SerializedName("geo_lon") val geoLon: String?
) 