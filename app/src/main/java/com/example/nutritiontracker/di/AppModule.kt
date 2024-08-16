package com.example.nutritiontracker.di

import android.app.Application
import androidx.room.Room
import com.example.nutritiontracker.common.Constants
import com.example.nutritiontracker.data.local.NutritionDatabase
import com.example.nutritiontracker.data.remote.NutritionAPI
import com.example.nutritiontracker.data.reposiitory.NutritionRepositoryImpl
import com.example.nutritiontracker.domain.repository.NutritionRepository
import com.example.nutritiontracker.domain.use_case.ClearLocalData
import com.example.nutritiontracker.domain.use_case.DeleteLocalNutritionData
import com.example.nutritiontracker.domain.use_case.DeleteRecentLocalNutritionData
import com.example.nutritiontracker.domain.use_case.GetNutritionLocalData
import com.example.nutritiontracker.domain.use_case.GetRecentNutritionLocalData
import com.example.nutritiontracker.domain.use_case.GetTotalNutrition
import com.example.nutritiontracker.domain.use_case.InsertLocalNutritionData
import com.example.nutritiontracker.domain.use_case.InsertLocalRecentNutritionData
import com.example.nutritiontracker.domain.use_case.LocalNutritionUseCases
import com.example.nutritiontracker.domain.use_case.RecentNutritionUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNutritionRepository(api: NutritionAPI): NutritionRepository {
        return NutritionRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideNutritionDatabase(app: Application): NutritionDatabase {
        return Room.databaseBuilder(
            app, NutritionDatabase::class.java, "nutrition_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNutritionApi(): NutritionAPI {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NutritionAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideNutritionUseCases(dt: NutritionDatabase): LocalNutritionUseCases {
        return LocalNutritionUseCases(
            getNutritionData = GetNutritionLocalData(dt),
            insertLocalNutritionData = InsertLocalNutritionData(dt),
            deleteLocalNutritionData = DeleteLocalNutritionData(dt),
            getTotalNutrition = GetTotalNutrition(dt),
            clearAllLocalData = ClearLocalData(dt)
        )
    }

    @Provides
    @Singleton
    fun provideRecentNutritionLocalData(dt: NutritionDatabase): RecentNutritionUseCases {
        return RecentNutritionUseCases(
            getRecentNutritionLocalData = GetRecentNutritionLocalData(dt),
            insertLocalRecentNutritionData = InsertLocalRecentNutritionData(dt),
            deleteRecentLocalNutritionData = DeleteRecentLocalNutritionData(dt)
        )
    }
}