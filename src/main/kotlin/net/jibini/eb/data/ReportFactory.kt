package net.jibini.eb.data

import java.io.File

interface ReportFactory
{
    fun createReport(args: Map<String, Array<String>>): File
}