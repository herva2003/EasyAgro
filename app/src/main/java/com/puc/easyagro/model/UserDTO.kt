package com.puc.easyagro.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.Parceler

@Parcelize
data class UserDTO(
    var name: String?,
    var nickname: String?,
    val login: String?,
    val password: String?,
    var phoneNumber: String?,
    var imagem: String?,
    val cpf: String?
) : Parcelable {


    companion object : Parceler<UserDTO> {

        override fun UserDTO.write(dest: Parcel, flags: Int) {
            dest?.writeString(name)
            dest?.writeString(nickname)
            dest?.writeString(login)
            dest?.writeString(password)
            dest?.writeString(phoneNumber)
            dest?.writeString(imagem)
            dest?.writeString(cpf)
        }

        override fun create(parcel: Parcel): UserDTO {
            return UserDTO(parcel)
        }
    }

    private constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )
}
