package ru.example.demo.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class Loggable {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)
}