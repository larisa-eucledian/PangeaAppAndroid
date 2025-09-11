package com.example.pangeaapp.di

import android.app.Application
import com.example.pangeaapp.data.MockPlansRepository
import com.example.pangeaapp.data.PlansRepository


/**
 * TODO(MIGRATE_TO_HILT): Reemplazar este singleton por Hilt (@HiltAndroidApp + @Module @Provides)
 * cuando conectemos la API real y haya más dependencias.
 */
object AppDependencies {
    lateinit var plansRepository: PlansRepository
        private set

    fun init(app: Application) {
        plansRepository = MockPlansRepository(app.applicationContext)
    }
}