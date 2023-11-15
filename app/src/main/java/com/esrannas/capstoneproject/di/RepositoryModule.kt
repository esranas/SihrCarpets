package com.esrannas.capstoneproject.di

import com.esrannas.capstoneproject.data.repository.AuthRepository
import com.esrannas.capstoneproject.data.repository.ProductRepository
import com.esrannas.capstoneproject.data.source.local.ProductDao
import com.esrannas.capstoneproject.data.source.remote.ProductService
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProductsRepository(
        productService: ProductService,
        productDao: ProductDao
    ) = ProductRepository(productService, productDao)

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface RepositoryModuleInterface {
        fun getAuthRepo(): AuthRepository
    }
}