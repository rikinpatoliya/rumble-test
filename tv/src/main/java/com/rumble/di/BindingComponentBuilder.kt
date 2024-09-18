package com.rumble.di

import dagger.hilt.DefineComponent

@DefineComponent.Builder
interface BindingComponentBuilder {
    fun build(): BindingComponent
}