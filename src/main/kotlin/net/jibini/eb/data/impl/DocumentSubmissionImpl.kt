package net.jibini.eb.data.impl

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

import net.jibini.eb.EasyButton
import net.jibini.eb.data.Document
import net.jibini.eb.data.DocumentDescriptor
import net.jibini.eb.teststand.impl.StoreFile

import org.slf4j.LoggerFactory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.client.RestTemplate

import java.io.File
import java.lang.IllegalStateException
import java.lang.NullPointerException
import java.lang.RuntimeException

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

import kotlin.concurrent.thread

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
    fun putDocuments(
        request: HttpServletRequest,
        response: HttpServletResponse,

        session: HttpSession,

        @PathVariable(name = "repository") repository: String,
        @RequestBody documents: Array<Map<String, Any?>>
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

        val storeFile = StoreFile(descriptor, File(".stores/$repository.bin"))

        for (documentMap in documents)
        {
            val document = Document(descriptor)
            document.internal.putAll(documentMap)
            DocumentRepositoryCachesImpl.put(document)

            storeFile.write(document)
        }

        storeFile.close()
    }

    fun loadCache(descriptor: DocumentDescriptor)
    {
        val storeFile = StoreFile(descriptor, File(".stores/${descriptor.name}.bin"))
        val loaded = storeFile.loadAll()
        storeFile.close()

        for (document in loaded)
            DocumentRepositoryCachesImpl.put(document)
    }

    private val log = LoggerFactory.getLogger(javaClass)

    private val sendBuffers = mutableMapOf<String, MutableList<Document>>()
    private val bufferMutex = Mutex(locked = false)

    init
    {
        thread(isDaemon = true, name = "Bulk sender")
        {
            log.info("Transfer thread is active")

            while (true)
            {
                runBlocking {
                    bufferMutex.withLock {
                        for ((address, sendBuffer) in sendBuffers)
                        {
                            if (sendBuffer.size > 0)
                                log.info("Transfer of {} documents to central database", sendBuffer.size)

                            try
                            {
                                doSend(address, sendBuffer)
                            } catch (ex: Exception)
                            {
                                log.error("Failed to send documents", ex)
                            }

                            sendBuffer.clear()
                        }

                    }
                }

                Thread.sleep(200);
            }
        }
    }

    private fun doSend(address: String, documents: List<Document>)
    {
            val rest = RestTemplate()
//            val headers = HttpHeaders()
//            headers["secret"] = easyButton.config.secret
//            headers.contentType = MediaType.APPLICATION_JSON

//            val request = HttpEntity(documents, headers)
            rest.postForObject(address, documents, ResponseEntity::class.java)
    }

    fun sendDocument(address: String, document: Document)
    {
        runBlocking {
            bufferMutex.withLock {
                val properSend = sendBuffers.putIfAbsent(address, mutableListOf())!!
                properSend += document
            }
        }
    }
}