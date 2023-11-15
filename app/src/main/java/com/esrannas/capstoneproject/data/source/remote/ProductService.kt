package com.esrannas.capstoneproject.data.source.remote

import com.esrannas.capstoneproject.data.model.request.AddToCartRequest
import com.esrannas.capstoneproject.data.model.request.ClearCartRequest
import com.esrannas.capstoneproject.data.model.request.DeleteFromCartRequest
import com.esrannas.capstoneproject.data.model.response.BaseResponse
import com.esrannas.capstoneproject.data.model.response.GetCartProductsResponse
import com.esrannas.capstoneproject.data.model.response.GetProductDetailResponse
import com.esrannas.capstoneproject.data.model.response.GetProductsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface ProductService {
    @GET("get_products.php")
    suspend fun getProducts(): Response<GetProductsResponse>

    @GET("get_product_detail.php")
    suspend fun getProductDetail(
        @Query("id") id: Int?,
    ): Response<GetProductDetailResponse>

    @GET("get_cart_products.php")
    suspend fun getCartProducts(
        @Query("userId") userId: String
    ): Response<GetCartProductsResponse>

    @POST("add_to_cart.php")
    suspend fun addToCart(
        @Body addToCartRequest: AddToCartRequest
    ): BaseResponse

    @POST("delete_from_cart.php")
    suspend fun deleteFromCart(
        @Body deleteFromCartRequest: DeleteFromCartRequest
    ): BaseResponse

    @POST("clear_cart.php")
    suspend fun clearCart(
        @Body clearCartRequest: ClearCartRequest
    ): BaseResponse

    @GET("search_product.php")
    suspend fun getSearchProduct(@Query("query") queryValue: String): GetProductsResponse
}

