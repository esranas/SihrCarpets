package com.esrannas.capstoneproject.data.mapper

import com.esrannas.capstoneproject.data.model.response.Product
import com.esrannas.capstoneproject.data.model.response.ProductEntity
import com.esrannas.capstoneproject.data.model.response.ProductUI


fun List<Product>.mapProductToProductUI(favorites: List<Int>) =
    map {
        ProductUI(
            id = it.id ?: 1,
            title = it.title.orEmpty(),
            price = it.price ?: 0.0,
            salePrice = it.salePrice ?: 0.0,
            description = it.description.orEmpty(),
            category = it.category.orEmpty(),
            imageOne = it.imageOne.orEmpty(),
            rate = it.rate ?: 0.0,
            count = it.count ?: 0,
            saleState = it.saleState ?: false,
            isFav = favorites.contains(it.id)
        )
    }

//For Detail Page
fun Product.mapToProductUI(favorites: List<Int>) =
    ProductUI(
        id = id ?: 1,
        title = title.orEmpty(),
        price = price ?: 0.0,
        salePrice = salePrice ?: 0.0,
        description = description.orEmpty(),
        category = category.orEmpty(),
        imageOne = imageOne.orEmpty(),
        rate = rate ?: 0.0,
        count = count ?: 0,
        saleState = saleState ?: false,
        isFav = favorites.contains(id),

        )

fun Product.mapToProductUI(): ProductUI {
    return ProductUI(
        id = id ?: 1,
        title = title.orEmpty(),
        price = price ?: 0.0,
        salePrice = salePrice ?: 0.0,
        description = description.orEmpty(),
        category = category.orEmpty(),
        imageOne = imageOne.orEmpty(),
        rate = rate ?: 0.0,
        count = count ?: 1,
        saleState = saleState ?: false
    )
}

fun ProductUI.mapToProductEntity(userId: String) =
    ProductEntity(
        productId = id,
        title = title,
        price = price,
        salePrice = salePrice,
        imageOne = imageOne,
        rate = rate,
        saleState = saleState,
        description = description,
        category = category,
        count = count,
        userId = userId

    )


fun List<ProductEntity>.mapProductEntityToProductUI() =
    map {
        ProductUI(
            id = it.productId ?: 1,
            title = it.title.orEmpty(),
            price = it.price ?: 0.0,
            salePrice = it.salePrice ?: 0.0,
            imageOne = it.imageOne.orEmpty(),
            rate = it.rate ?: 0.0,
            saleState = it.saleState ?: false,
            description = it.description.orEmpty(),
            category = it.category.orEmpty(),
            count = it.count ?: 0,


            )
    }


