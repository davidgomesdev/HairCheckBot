package me.davidgomes.haircheckbot.model

enum class ResultType(val message: String, val color: Int) {
    Good("It is very good!! :heart_eyes:", 0x29c2ff),
    Bad("NO NO NO! :hot_face:", 0xff3224),
    NotSoGood("It has some bad stuff! :confused:", 0xffee00),
    Neutral("""It is neutral. ¯\_(ツ)_/¯""", 0xf5f2f2)
}
