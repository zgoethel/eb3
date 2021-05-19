package net.jibini.eb.data.impl

import net.jibini.eb.EasyButton
import net.jibini.eb.data.Document
import net.jibini.eb.data.DocumentDescriptor

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.client.RestTemplate

import java.lang.IllegalStateException
import java.lang.NullPointerException
import java.lang.RuntimeException

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

/**
 * REST API service for submitting cache contents and cached documents. Allows
 * a node client to submit data which can be queried by users via the main
 * search UI.
 *
 * Access to document caches relies on the server's secret key to validate the
 * client submission request.
 *
 * @author Zach Goethel
 */
@Controller
class DocumentSubmissionImpl
{
    // Required to check configured secret key
    @Autowired
    private lateinit var easyButton: EasyButton

    @PostMapping("/document/{repository}")
    fun putDocument(
        request: HttpServletRequest,
        response: HttpServletResponse,

        session: HttpSession,

        @PathVariable(name = "repository") repository: String,
        @RequestBody documentMap: Map<String, Any?>
    )
    {
        if (request.getHeader("secret") != easyButton.config.secret)
            throw IllegalStateException("Secrets do not match")

        val descriptor = try
        {
            DocumentDescriptor.forName(repository)
        } catch (ex: NullPointerException)
        {
            throw RuntimeException("Failed to fetch requested descriptor", ex)
        }

        val document = Document(descriptor)
        document.internal.putAll(documentMap)
        DocumentRepositoryCachesImpl.put(document)
    }

    fun sendDocument(address: String, document: Document)
    {
        val rest = RestTemplate()
        val headers = HttpHeaders()
        headers["secret"] = easyButton.config.secret
        headers.contentType = MediaType.APPLICATION_JSON

        val request = HttpEntity(document, headers)
        rest.postForObject(address, request, String::class.java)
    }
}