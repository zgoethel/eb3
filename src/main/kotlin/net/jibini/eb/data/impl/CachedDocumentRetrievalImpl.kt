package net.jibini.eb.data.impl

import net.jibini.eb.auth.page.LoginPage
import net.jibini.eb.data.Document

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

/**
 * REST API service for retrieving cache contents and cached documents.
 * Allows access to entire collections of documents, or a specific
 * document given its primary key.
 *
 * Access to document caches relies on the [LoginPage] to validate the
 * current session. Unauthenticated access will be redirected.
 *
 * Incremental updates and cache maintenance will be left up to the
 * implementation of the [DocumentRepositoryCachesImpl], as the API
 * access to the repositories must be through that utility class.
 *
 * @author Zach Goethel
 */
@Controller
class CachedDocumentRetrievalImpl
{
    // Required to authenticate the current session
    @Autowired
    private lateinit var loginPage: LoginPage

    @GetMapping("/document/{repository}")
    @ResponseBody
    fun getDocumentRepository(
        request: HttpServletRequest,
        response: HttpServletResponse,

        session: HttpSession,

        @PathVariable repository: String
    ): Collection<Document>?
    {
        // Authenticate the current session
        loginPage.validate(session, request, response) ?: return null

        return DocumentRepositoryCachesImpl.get(repository).values
    }

    @GetMapping("/document/{repository}/{primary-key}")
    @ResponseBody
    fun getDocumentByPrimaryKey(
        request: HttpServletRequest,
        response: HttpServletResponse,

        session: HttpSession,

        @PathVariable(name = "repository") repository: String,
        @PathVariable(name = "primary-key") primaryKey: String
    ): Document?
    {
        // Authenticate the current session
        loginPage.validate(session, request, response) ?: return null

        return DocumentRepositoryCachesImpl.get(repository, primaryKey)
    }
}