package com.proyecto.huellitas_callejeras

import android.app.Application
import android.util.Log
import com.proyecto.huellitas_callejeras.data.DependencyProvider

class HuellitasApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DependencyProvider.initialize(this)
        DependencyProvider.setup(this)
    }
}