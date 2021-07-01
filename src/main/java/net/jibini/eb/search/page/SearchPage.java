package net.jibini.eb.search.page;

import kotlin.Pair;

import net.jibini.eb.EasyButton;
import net.jibini.eb.auth.AuthDetails;
import net.jibini.eb.auth.page.LoginPage;
import net.jibini.eb.data.Document;
import net.jibini.eb.data.DocumentDescriptor;
import net.jibini.eb.data.impl.CachedDocumentRetrievalImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Collection;

/**
 * The primary configurable search page. This is the primary page from which the
 * user will access and search for data. Each search function is flexible and
 * supports a wide variety of backend data sources.
 *
 * Search fields are aligned vertically on the left side of the page. Search
 * results are populated in a table in the center of the page. Additional fields
 * and elements may be added by extensions.
 *
 * No data on this page should be more than three clicks away.
 *
 * @author Zach Goethel
 */
@Controller
public class SearchPage
{
    // Required to authenticate the current session
    @Autowired
    private LoginPage loginPage;

    // Used to retrieve document repositories
    @Autowired
    private CachedDocumentRetrievalImpl retrieval;

    // Required to access configuration settings
    @Autowired
    private EasyButton easyButton;

    @GetMapping("/s")
    public String searchPage(
        HttpServletRequest request,
        HttpServletResponse response,

        HttpSession session,

        Model model,

        @RequestParam(defaultValue = "") String document,
        @RequestParam(defaultValue = "50") int top,
        @RequestParam(defaultValue = "0") int skip,
        @RequestParam(defaultValue = "") String search
    )
    {
        // Authenticate the current session
        AuthDetails authDetails = loginPage.validate(session, request, response);
        if (authDetails == null)
            return "login";
        // Default to the configured document type
        if (document.equals(""))
            document = easyButton.config.getDefaultSearchDocument();
        model.addAttribute("username", authDetails.getUsername());

        Pair<Collection<Document>, Integer> filtered = retrieval.getCountedDocumentRepository(document, top, skip, search, request.getParameterMap());
        // Filter the results for paging and search
        Collection<Document> repo = filtered.getFirst();

        // Add results and document type data
        model.addAttribute("repo", repo);
        model.addAttribute("descriptor", DocumentDescriptor.forName(document));
        // Add data for paging UI
        model.addAttribute("top", top);
        model.addAttribute("skip", skip);
        model.addAttribute("size", filtered.getSecond());
        // Add data for search UI and filling URLs
        model.addAttribute("search", search);
        model.addAttribute("args", request.getParameterMap());

        return "search";
    }
}
