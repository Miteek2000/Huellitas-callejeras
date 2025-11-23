package com.proyecto.huellitas_callejeras.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Patient(
    val id: Int,
    val name: String,
    val breed: String,
    val isAdopted: Boolean,
    val description: String,
    val isRecovering: Boolean = false,
    val imageUrl: String = ""
) : Parcelable
