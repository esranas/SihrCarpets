package com.esrannas.capstoneproject.data.repository

import com.esrannas.capstoneproject.common.Resource
import com.esrannas.capstoneproject.data.mapper.mapProductEntityToProductUI
import com.esrannas.capstoneproject.data.mapper.mapProductToProductUI
import com.esrannas.capstoneproject.data.mapper.mapToProductEntity
import com.esrannas.capstoneproject.data.mapper.mapToProductUI
import com.esrannas.capstoneproject.data.model.request.AddToCartRequest
import com.esrannas.capstoneproject.data.model.request.ClearCartRequest
import com.esrannas.capstoneproject.data.model.request.DeleteFromCartRequest
import com.esrannas.capstoneproject.data.model.response.BaseResponse
import com.esrannas.capstoneproject.data.model.response.ProductUI
import com.esrannas.capstoneproject.data.source.local.ProductDao
import com.esrannas.capstoneproject.data.source.remote.ProductService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProductRepository(
    private val productService: ProductService,
    private val productDao: ProductDao
) {


    suspend fun getProducts(saleState: Boolean = false): Resource<List<ProductUI>> =
        withContext(Dispatchers.IO) {
            try {
                val favorites = productDao.getFavProductIds()
                val response = productService.getProducts().body()

                if (response?.status == 200) {
                    val allProducts = response.products.orEmpty().mapProductToProductUI(favorites)
                    val filteredProducts = if (saleState) {
                        allProducts.filter { it.saleState }
                    } else {
                        allProducts
                    }

                    Resource.Success(filteredProducts)
                } else {
                    Resource.Fail(response?.message.orEmpty())
                }
            } catch (e: Exception) {
                Resource.Error(e.message.orEmpty())
            }
        }


    suspend fun getProductDetail(id: Int): Resource<ProductUI> =
        withContext(Dispatchers.IO) {
            try {
                val favorites = productDao.getFavProductIds()
                val response = productService.getProductDetail(id).body()

                if (response?.status == 200 && response.product != null) {
                    Resource.Success(response.product.mapToProductUI(favorites))
                } else {
                    Resource.Fail(response?.message.orEmpty())
                }
            } catch (e: Exception) {
                //internet sorunu
                Resource.Error(e.message.orEmpty())
            }
        }

    //FAVORITES OPERATIONS
    suspend fun addToFavorites(productUI: ProductUI, userId: String) {
        productDao.addProduct(productUI.mapToProductEntity(userId))
    }

    suspend fun deleteFromFavorites(productUI: ProductUI, userId: String) {
        productDao.deleteProduct(productUI.mapToProductEntity(userId))
    }

    suspend fun clearFavorites(userId: String) {
        productDao.clearFavorites(userId)
    }

    suspend fun getFavorites(userId: String): Resource<List<ProductUI>> =
        withContext(Dispatchers.IO) {
            try {
                val products = productDao.getProducts(userId)

                if (products.isEmpty()) {
                    Resource.Fail("Seems you do not have favorites yet.")
                } else {
                    Resource.Success(products.mapProductEntityToProductUI())
                }
            } catch (e: Exception) {
                Resource.Error(e.message.orEmpty())
            }
        }

    //CART OPERATIONS
    suspend fun getCartProducts(userId: String): Resource<List<ProductUI>> =
        withContext(Dispatchers.IO) {
            try {
                val response = productService.getCartProducts(userId).body()
                val favorites = productDao.getFavProductIds()

                if (response?.status == 200) {
                    Resource.Success(response.products.orEmpty().mapProductToProductUI(favorites))
                } else {
                    Resource.Fail(response?.message.orEmpty())
                }
            } catch (e: Exception) {
                Resource.Error("Something went wrong! We are working on it!")
            }
        }

    suspend fun deleteFromCart(deleteFromCartRequest: DeleteFromCartRequest): Resource<BaseResponse> =

        withContext(Dispatchers.IO) {
            try {
                val result = productService.deleteFromCart(deleteFromCartRequest)

                if (result.status == 200) {
                    Resource.Success(result)
                } else {
                    Resource.Fail("Something went wrong!")
                }
            } catch (e: Exception) {
                Resource.Error(e.message.orEmpty())
            }
        }

    suspend fun clearCart(clearCartRequest: ClearCartRequest): Resource<BaseResponse> =
        withContext(Dispatchers.IO) {
            try {
                val result = productService.clearCart(clearCartRequest)

                if (result.status == 200) {
                    Resource.Success(result)
                } else {
                    Resource.Error("Something went wrong!")
                }
            } catch (e: Exception) {
                Resource.Error(e.message.orEmpty())
            }
        }

    suspend fun addToCart(
        addToCartRequest: AddToCartRequest,
        completion: ((Boolean) -> Unit)?
    ): Resource<BaseResponse> =
        withContext(Dispatchers.IO) {
            try {

                val result = productService.addToCart(addToCartRequest)
                if (result.status == 200) {
                    completion?.let { launch(Dispatchers.Main) { it(true) } }
                    Resource.Success(result)
                } else {
                    completion?.let { launch(Dispatchers.Main) { it(false) } }
                    Resource.Fail("Something went wrong!")
                }

            } catch (e: Exception) {
                Resource.Error(e.message.orEmpty())
            }
        }

    suspend fun getSearchProduct(query: String): Resource<List<ProductUI>> {
        return try {
            val response = productService.getSearchProduct(query)
            Resource.Success(response.products?.map { it.mapToProductUI() }.orEmpty())
        } catch (e: Exception) {
            Resource.Error(e.message.orEmpty())
        }
    }

}



