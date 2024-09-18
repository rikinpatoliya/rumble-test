//package com.rumble.util
//
//import androidx.core.content.ContextCompat
//import androidx.test.filters.SmallTest
//import androidx.test.platform.app.InstrumentationRegistry
//import androidx.test.runner.AndroidJUnit4
//import com.rumble.R
//import org.junit.Assert.assertEquals
//import org.junit.Test
//import org.junit.runner.RunWith
//
//
//@RunWith(AndroidJUnit4::class)
//@SmallTest
//class RumbleUIUtilTest {
//
//    @Test
//    fun useAppContext() {
//        // Context of the app under test.
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        assertEquals("com.rumble.battles.dev", appContext.packageName)
//    }
//
//    @Test
//    fun getPlaceholderColor() {
//
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//
//        val user1ColorResId = com.rumble.utils.RumbleUIUtil.getPlaceholderColorResId("User1")
//        assertEquals(ContextCompat.getColor(appContext, R.color.placeholderColor6), ContextCompat.getColor(appContext, user1ColorResId))
//
//        val user2ColorResId = com.rumble.utils.RumbleUIUtil.getPlaceholderColorResId("User2")
//        assertEquals(ContextCompat.getColor(appContext, R.color.placeholderColor1), ContextCompat.getColor(appContext, user2ColorResId))
//
//        val user3ColorResId = com.rumble.utils.RumbleUIUtil.getPlaceholderColorResId("User3")
//        assertEquals(ContextCompat.getColor(appContext, R.color.placeholderColor2), ContextCompat.getColor(appContext, user3ColorResId))
//
//        val user4ColorResId = com.rumble.utils.RumbleUIUtil.getPlaceholderColorResId("User4")
//        assertEquals(ContextCompat.getColor(appContext, R.color.placeholderColor3), ContextCompat.getColor(appContext, user4ColorResId))
//
//        val user5ColorResId = com.rumble.utils.RumbleUIUtil.getPlaceholderColorResId("User5")
//        assertEquals(ContextCompat.getColor(appContext, R.color.placeholderColor2), ContextCompat.getColor(appContext, user5ColorResId))
//    }
//}