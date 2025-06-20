package com.example.shoppingapp.domain.usecase

import com.example.shoppingapp.data.model.Product
import com.example.shoppingapp.data.repository.SearchRepository

class PerformSearchUseCase(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String): Result<List<Product>> {
        if (query.isBlank()) {
            return Result.success(emptyList())
        }
        
        return try {
            searchRepository.searchProducts(query)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
