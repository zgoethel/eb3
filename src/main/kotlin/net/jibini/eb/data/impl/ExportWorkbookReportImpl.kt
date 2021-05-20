package net.jibini.eb.data.impl

import net.jibini.eb.auth.page.LoginPage
import net.jibini.eb.data.ReportFactory
import net.jibini.eb.impl.ClasspathAnnotationImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.io.FileInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Controller
class ExportWorkbookReportImpl
{
    // Required to authenticate the current session
    @Autowired
    private lateinit var loginPage: LoginPage

    @GetMapping("/report/{type}")
    fun generateReport(
        request: HttpServletRequest,
        response: HttpServletResponse,

        session: HttpSession,

        @PathVariable type: String
    ): ResponseEntity<Resource>
    {
        // Authenticate the current session
        loginPage.validate(session, request, response) ?: throw IllegalStateException("No active session was found")

        val reportType: ReportFactory = ClasspathAnnotationImpl.findAndCreate(type)
        val file = reportType.createReport(request.parameterMap)
        val resource = InputStreamResource(FileInputStream(file))

        val headers = HttpHeaders()
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${file.name}")

        val entity: ResponseEntity<Resource> = ResponseEntity.ok()
            .headers(headers)
            .contentLength(file.length())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource)

        file.delete()
        return entity
    }
}