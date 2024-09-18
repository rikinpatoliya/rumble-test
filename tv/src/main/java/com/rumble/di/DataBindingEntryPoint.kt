package com.rumble.di

import androidx.databinding.DataBindingComponent
import com.rumble.di.scope.BindingScope
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn

@EntryPoint
@BindingScope
@InstallIn(BindingComponent::class)
interface DataBindingEntryPoint : DataBindingComponent {
//
//    @BindingScope
//    fun getGlideImageAdapter() : GlideImageAdapter
}