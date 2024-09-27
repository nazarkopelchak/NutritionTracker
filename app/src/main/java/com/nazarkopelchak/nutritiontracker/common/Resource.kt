package com.nazarkopelchak.nutritiontracker.common

/**
 * This sealed class serves as a API response class
 *
 * @param data data acquired from an API call
 * @param message message to be sent based on an API response
 */
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)
    class Loading<T>(data: T? = null): Resource<T>(data)
}