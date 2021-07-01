package net.jibini.eb.data

import java.io.File

interface ReportFactory
{
    fun createReport(args: MutableMap<String, Array<String>>): File
}