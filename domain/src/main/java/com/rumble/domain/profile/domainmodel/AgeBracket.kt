package com.rumble.domain.profile.domainmodel

enum class AgeBracket(val bracketId: Int, private val start: Int, private val end: Int) {
    Bracket1(1, 18, 24),
    Bracket2(2, 25, 34),
    Bracket3(3, 35, 44),
    Bracket4(4, 45, 54),
    Bracket5(5, 55, 64),
    Bracket6(6, 65, Int.MAX_VALUE);

    companion object {
        fun findBracketForAge(age: Int?): AgeBracket? =
            age?.let {values().find { age >= it.start && age <= it.end }}
    }
}