package net.jibini.eb.data.impl

import net.jibini.eb.auth.page.LoginPage
import net.jibini.eb.data.Document
import net.jibini.eb.data.DocumentDescriptor

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

import java.util.stream.Collectors

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

    fun getDocumentRepository(
        document: String,
        top: Int = -1,
        skip: Int = 0,
        search: String = "",
        args: MutableMap<String, Array<String>> = mutableMapOf()
    ) = getCountedDocumentRepository(document, top, skip, search, args).first

    fun getCountedDocumentRepository(
        document: String,
        top: Int = -1,
        skip: Int = 0,
        search: String = "",
        args: MutableMap<String, Array<String>> = mutableMapOf()
    ): Pair<Collection<Document>, Int>
    {
        val i = intArrayOf(0, 0)

        return Pair(DocumentRepositoryCachesImpl.get(document).values
            .stream()
            .filter {
                val passed = booleanArrayOf(false)

                if (search.isNotBlank())
                    it.internal
                        .forEach { (k, v) ->
                            try
                            {
                                passed[0] = passed[0] || it.descriptor
                                    .fields[k]!!
                                    .format
                                    .formatString(v)
                                    .toLowerCase()
                                    .contains(search.toLowerCase())
                            } catch (_: Exception)
                            {  }
                        }
                else
                    passed[0] = true

                for (each in DocumentDescriptor.forName(document).fields.values)
                {
                    if (!passed[0]) break
                    passed[0] = passed[0] && each.format.filter(it[each.name], each.name, args)
                }

                if (passed[0]) ++i[0]
                ++i[1]

                passed[0] && ((i[0] > skip && i[0] <= top + skip) || top == -1)
            }
            .collect(Collectors.toList()), i[0])
    }

    @GetMapping("/document/{repository}")
    @ResponseBody
    fun getDocumentRepository(
        request: HttpServletRequest,
        response: HttpServletResponse,

        session: HttpSession,

        @PathVariable repository: String,

        @RequestParam(defaultValue = "-1") top: Int,
        @RequestParam(defaultValue = "0") skip: Int,
        @RequestParam(defaultValue = "") search: String
    ): Collection<Document>?
    {
        // Authenticate the current session
        loginPage.validate(session, request, response) ?: return null

        return getDocumentRepository(repository, top, skip, search, request.parameterMap)
    }

    fun getDocumentByPrimaryKey(repository: String, primaryKey: String) = DocumentRepositoryCachesImpl.get(repository, primaryKey)

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

        return getDocumentByPrimaryKey(repository, primaryKey)
    }
}