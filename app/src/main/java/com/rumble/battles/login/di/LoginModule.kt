package com.rumble.battles.login.di

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.rumble.battles.BuildConfig
import com.rumble.domain.login.model.LoginRepository
import com.rumble.domain.login.model.LoginRepositoryImpl
import com.rumble.domain.login.model.datasource.LoginRemoteDataSource
import com.rumble.domain.login.model.datasource.LoginRemoteDataSourceImpl
import com.rumble.network.api.LoginApi
import com.rumble.utils.HashCalculator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    @Provides
    @Singleton
    fun provideLoginRemoteDataSource(loginApi: LoginApi): LoginRemoteDataSource =
        LoginRemoteDataSourceImpl(loginApi = loginApi)

    @Provides
    @Singleton
    fun provideLoginRepository(
        loginApi: LoginApi,
        loginRemoteDataSource: LoginRemoteDataSource,
    ): LoginRepository =
        LoginRepositoryImpl(
            loginApi = loginApi,
            loginRemoteDataSource = loginRemoteDataSource,
            dispatcher = Dispatchers.IO,
            hashCalculator = HashCalculator
        )

    @Provides
    @Singleton
    fun provideGoogleSignInClient(application: Application): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(application, gso)
    }
}