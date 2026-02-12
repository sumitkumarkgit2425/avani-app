package com.example.navya.data.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow

sealed class PaymentResult {
    data class Success(val orderId: String) : PaymentResult()
    data class Error(val code: Int, val message: String) : PaymentResult()
    object Idle : PaymentResult()
}

interface PaymentRepository {
    fun createOrderId(amount: Double): Flow<String>

    val paymentResult: SharedFlow<PaymentResult>
    suspend fun updatedPaymentResult(result: PaymentResult)
}

class PaymentRepositoryImpl @Inject constructor() : PaymentRepository {
    private val _paymentResult = MutableSharedFlow<PaymentResult>()
    override val paymentResult: SharedFlow<PaymentResult> = _paymentResult.asSharedFlow()

    override suspend fun updatedPaymentResult(result: PaymentResult) {
        _paymentResult.emit(result)
    }

    override fun createOrderId(amount: Double): Flow<String> = flow {
        kotlinx.coroutines.delay(500)
        val mockId = "order_mock_${System.currentTimeMillis()}"
        emit(mockId)
    }
}
