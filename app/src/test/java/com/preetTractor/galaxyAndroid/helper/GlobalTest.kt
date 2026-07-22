package com.preetTractor.galaxyAndroid.helper

import org.junit.Assert.*

import org.junit.Test

class GlobalTest {

    @Test
    fun isPalindrome() {
        var result = Globals.isPalindrome("level")
        assertEquals(true, result)


    }

    @Test
    fun isPalindrome_expected_false(){
        var result = Globals.isPalindrome("hello")
        assertNotEquals(true, result)
    }
}