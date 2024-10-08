package com.nazarkopelchak.nutritiontracker.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.work.WorkManager
import com.nazarkopelchak.nutritiontracker.common.Constants
import com.nazarkopelchak.nutritiontracker.data.local.NutritionDatabase
import com.nazarkopelchak.nutritiontracker.data.remote.NutritionAPI
import com.nazarkopelchak.nutritiontracker.data.reposiitory.NutritionRepositoryImpl
import com.nazarkopelchak.nutritiontracker.domain.repository.NutritionRepository
import com.nazarkopelchak.nutritiontracker.domain.use_case.ClearLocalData
import com.nazarkopelchak.nutritiontracker.domain.use_case.DeleteLocalNutritionData
import com.nazarkopelchak.nutritiontracker.domain.use_case.DeleteRecentLocalNutritionData
import com.nazarkopelchak.nutritiontracker.domain.use_case.GetNutritionLocalData
import com.nazarkopelchak.nutritiontracker.domain.use_case.GetRecentNutritionLocalData
import com.nazarkopelchak.nutritiontracker.domain.use_case.GetTotalNutrition
import com.nazarkopelchak.nutritiontracker.domain.use_case.InsertLocalNutritionData
import com.nazarkopelchak.nutritiontracker.domain.use_case.InsertLocalRecentNutritionData
import com.nazarkopelchak.nutritiontracker.domain.use_case.LocalNutritionUseCases
import com.nazarkopelchak.nutritiontracker.domain.use_case.NukeNutritionTable
import com.nazarkopelchak.nutritiontracker.domain.use_case.RecentNutritionUseCases
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
        )
            //.fallbackToDestructiveMigration()
            .build()
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
            getTotalNutrition = GetTotalNutrition(),
            nukeTable = NukeNutritionTable(dt),
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

    @Provides
    @Singleton
    fun provideSharedPreference(app: Application): SharedPreferences {
        return app.applicationContext.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideWorkManager(app: Application) : WorkManager {
        return WorkManager.getInstance(app.applicationContext)
    }
}