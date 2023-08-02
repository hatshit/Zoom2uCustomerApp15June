package com.zoom2u_customer.ui.log_in

import java.io.Serializable

data class LoginReprocess(
    val success: String,
    val isSuspendedWithUnpaidDues: String
): Serializable

/*data class LoginRespoce(val msg : String,
                        val isSuspendedWithUnpaidDues : String): Serializable*/
