package me.davidgomes.haircheckbot.model

data class Config(val bot: Bot, val alcohols: Alcohols)

data class Bot(val token: String)

data class Alcohols(val good: List<String>, val bad: List<String>)
