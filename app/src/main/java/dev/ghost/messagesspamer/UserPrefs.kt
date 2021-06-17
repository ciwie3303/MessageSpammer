package dev.ghost.messagesspamer

import com.chibatching.kotpref.KotprefModel

object UserPrefs : KotprefModel() {
    var phones by stringPref("")
    var message by stringPref("")
}