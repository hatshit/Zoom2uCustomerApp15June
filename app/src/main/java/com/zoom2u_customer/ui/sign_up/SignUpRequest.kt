package com.zoom2u_customer.ui.sign_up

import java.io.Serializable

class SignUpRequest(val firstName : String,
                    val lastName: String,
                    val company : String,
                    val userName : String,
                    val mobile : String,
                    val howDidYouFindUs : String,
                    val password : String,
                    val confirmPassword : String,
                    val terms : String,
                    val courier : String,
                    val customerType : String): Serializable