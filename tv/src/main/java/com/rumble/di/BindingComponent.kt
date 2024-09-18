package com.rumble.di

import com.rumble.di.scope.BindingScope
import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent

@BindingScope
@DefineComponent(parent = SingletonComponent::class)
interface BindingComponent