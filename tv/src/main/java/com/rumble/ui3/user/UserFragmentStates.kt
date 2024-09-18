package com.rumble.ui3.user

sealed class UserFragmentStates {

    object Loading : UserFragmentStates()
    object Error : UserFragmentStates()
    object NotLoggedIn : UserFragmentStates()
    object LoggedIn : UserFragmentStates()

}
