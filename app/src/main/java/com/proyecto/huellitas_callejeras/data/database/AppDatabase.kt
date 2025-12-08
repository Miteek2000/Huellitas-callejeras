package com.proyecto.huellitas_callejeras.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.proyecto.huellitas_callejeras.data.dao.MedicamentoDao
import com.proyecto.huellitas_callejeras.data.dao.TratamientoDao
import com.proyecto.huellitas_callejeras.data.model.Medicamento
import com.proyecto.huellitas_callejeras.data.model.Tratamiento

@Database(
    entities = [Tratamiento::class, Medicamento::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tratamientoDao(): TratamientoDao
    abstract fun medicamentoDao(): MedicamentoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "huellitas_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
