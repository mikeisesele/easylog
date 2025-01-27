package com.michael.easylog


fun interface BuilderAction {
    fun apply(builder: EasyLog.Builder)
}