package it.supabase.remembermy.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.admin.LinkType
import io.github.jan.supabase.auth.admin.generateLinkFor
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Github
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.providers.builtin.OTP

class SupabaseAuth(val supabase: SupabaseClient) {
    val auth = supabase.auth

    suspend fun signUp(signEmail: String, signPw: String) = auth.signUpWith(Email) {
            email = signEmail
            password = signPw
        }

    suspend fun resendEmailOTP(email: String) = auth.resendEmail(OtpType.Email.SIGNUP, email);

    suspend fun recoveryEmail(emailRecovery: String) = auth.resetPasswordForEmail(
        email = emailRecovery,
        redirectUrl = "it.supabase.remembermy://login-callback?type=recovery")

    suspend fun signInEmail(signEmail: String, signPw: String) = auth.signInWith(Email) {
        email = signEmail
        password = signPw
    }


    suspend fun signInToken() = auth.signInWith(IDToken) {
        idToken = "token"
        provider = Google
    }

    suspend fun signInOTP(signEmail: String) = auth.signInWith(OTP, "it.supabase.remembermy://login-callback") {
        email = signEmail
        createUser = false
    }

    suspend fun signInGit() = auth.signInWith(Github, "it.supabase.remembermy://login-callback"){

    }

    suspend fun pwResetRequest(email: String) = auth.resetPasswordForEmail(email = email)


    suspend fun verifyEmailOTP(checkEmail: String) =
        auth.verifyEmailOtp(type = OtpType.Email.EMAIL, email = checkEmail, token = "token")

    fun getSession() = auth.currentSessionOrNull();

    suspend fun newSession() = auth.refreshCurrentSession();

    suspend fun getUser() = auth.retrieveUserForCurrentSession(updateSession = true)

    suspend fun codeForSession(code : String) = auth.exchangeCodeForSession(code)

    suspend fun importSession(code : String) = auth.importSession(codeForSession(code))

    suspend fun changePassword(passwordChange : String) = auth.updateUser {
        password = passwordChange
    }

}