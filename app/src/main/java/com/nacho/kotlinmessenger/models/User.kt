package com.nacho.kotlinmessenger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// en el build.gradle: app aplicamos
// androidExtensions{
//     experimental = true
// }
// para que sea posible heredar de parcelable sin cambiar las propiedades de la clase
//y agregamos las sig. anotación
@Parcelize
class User(val uid: String, val username: String, val profleImageUrl: String?): Parcelable {
    constructor() : this("", "", "")
}