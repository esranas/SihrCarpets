package com.esrannas.capstoneproject.data.model.response

data class GetCartProductsResponse(
    val products: List<Product>?,
):BaseResponse()
