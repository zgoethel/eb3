package net.jibini.eb.data.impl

import net.jibini.eb.data.DocumentDescriptor
import net.jibini.eb.data.ReportFactory
import net.jibini.eb.impl.Classpath
import net.jibini.eb.impl.EasyButtonContextImpl

import org.apache.poi.xssf.usermodel.XSSFWorkbook

import java.io.File
import java.io.FileOutputStream
import java.util.*

@Classpath
class ExportWorkbook : ReportFactory
{
    // Required to access cached document repositories
    private val retrieval = EasyButtonContextImpl.getBean(CachedDocumentRetrievalImpl::class.java)

    override fun createReport(args: MutableMap<String, Array<String>>): File
    {
        File(".reports").mkdirs()
        val f = File(".reports/${UUID.randomUUID()}.xlsx")
        f.createNewFile()
        f.deleteOnExit()

        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Data")

        val document = args["document"]!![0]
        val top = args["top"]?.get(0)?.toIntOrNull() ?: -1
        val skip = args["skip"]?.get(0)?.toIntOrNull() ?: 0
        val search = args["search"]?.get(0) ?: ""

        val elements = retrieval.getDocumentRepository(document, top, skip, search, args)
        val head = sheet.createRow(0)

        val boldStyle = workbook.createCellStyle()
        boldStyle.font.bold = true
        val regularStyle = workbook.createCellStyle()
        regularStyle.font.bold = false

        for ((i, field) in DocumentDescriptor.forName(document).fields.values.withIndex())
        {
            val cell = head.createCell(i)
            cell.setCellValue(field.title)
            cell.cellStyle = boldStyle
        }

        for ((i, d) in elements.withIndex())
        {
            val row = sheet.createRow(i + 1)

            for ((j, field) in DocumentDescriptor.forName(document).fields.values.withIndex())
            {
                val cell = row.createCell(j)
                cell.setCellValue(d.getString(field.name))
                cell.cellStyle = regularStyle
            }
        }

        val output = FileOutputStream(f)
        workbook.write(output)
        output.flush()
        output.close()

        return f
    }
}