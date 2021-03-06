package net.jibini.eb.epicor.impl;

import net.jibini.eb.auth.AuthDetails;
import net.jibini.eb.data.Document;
import net.jibini.eb.data.DocumentDescriptor;
import net.jibini.eb.data.impl.AbstractCachedDataSourceImpl;
import net.jibini.eb.epicor.Epicor;
import net.jibini.eb.epicor.EpicorCall;
import net.jibini.eb.impl.Classpath;
import net.jibini.eb.impl.EasyButtonContextImpl;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A data source which loads documents from an Epicor service. This source can
 * load all documents from an Epicor service, or all entries which have been
 * changed or added since the previous sync. This implementation can perform
 * incremental syncs for a cache of Epicor data.
 *
 * A document cache will be built from the Epicor service with the same name as
 * the document descriptor. For example, a document for part data will be named
 * "Erp.BO.PartSvc/Parts," and this data source will load part data from the
 * Epicor API v1 service with the same name.
 *
 * Incremental cache updates are performed by comparing records' "SysRevID"
 * element as reported by Epicor. This is a unique modification counter which
 * globally increments with each row written to Epicor's SQL server. Each cached
 * document type has a mapped "last loaded" index. Incremental updates will
 * request entries from the document's Epicor service with a "SysRevID" greater
 * than the previously imported index. Existing cached documents will be updated
 * with modified data, and newly created entries will be added to the cache.
 * For more information, see this article about SQL transaction IDs on
 * <a href="https://rb.gy/1qjvaq">Microsoft docs</a> and this
 * <a href="https://rb.gy/wyiquz">forum thread</a> on the EpiUsers forum.
 *
 * Only the fields required by the document descriptor will be selected. This
 * should reduce the bandwidth consumed by reading from Epicor's API. For more
 * information on OData selection ($select) and filter ($filter) arguments, see
 * this article about OData queries on <a href="https://rb.gy/tkopre">Microsoft
 * docs</a>.
 *
 * All cached Epicor services share this common data source. Services are
 * differentiated by the name of the document descriptor. Updates and new
 * records will be written to the document descriptor's repository as well as
 * returned when the sync is requested.
 *
 * @author Zach Goethel
 */
@Classpath
public class EpicorSource extends AbstractCachedDataSourceImpl
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Required to access Epicor configuration
    private final Epicor epicor = EasyButtonContextImpl.getBean(Epicor.class);

    /**
     * Recorded "last imported" indices for each document descriptor type. Zero
     * indicates the next incremental update will load the entirety of the
     * service values. Values are updated here each time an update is performed.
     *
     * These indices are the greatest value of "SysRevID" loaded for a specific
     * Epicor service name.
     */
    private final Map<String, Integer> lastLoaded = new HashMap<>();

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public Collection<Document> performIncrementalFor(@NotNull DocumentDescriptor descriptor)
    {
        log.info(String.format("Performing incremental update for '%s' . . .", descriptor.getName()));

        // Create the Epicor service for the document
        EpicorCall call = new EpicorCall("BaqSvc/" + descriptor.getName());
        Map<String, String> args = new HashMap<>();
        // Filter down to only new entries
        args.put("$filter", String.format("%s gt %d",
                descriptor.getTrackIndex(),
                lastLoaded.getOrDefault(descriptor.getName(), 0)));

        // Call the service and create documents
        //TODO RELY ON CURRENT SESSION FOR CREDENTIALS; DON'T HAVE IN CONFIG
        return call.call(new AuthDetails(epicor.config.getUsername(), epicor.config.getPassword()), args)
                .getJSONArray("value")

                // Conversion to List converts child elements to Map or List
                // as well; no `JSONObject.toMap()` required later on
                .toList()
                .stream()

                .map((entry) ->
                {
                    Map<String, ?> map = (Map<String, ?>)entry;

                    int newlyLoaded = Integer.parseInt(map.get(descriptor.getTrackIndex()).toString());
                    int previouslyLoaded = lastLoaded.getOrDefault(descriptor.getName(), 0);

                    if (newlyLoaded > previouslyLoaded)
                        lastLoaded.put(descriptor.getName(), newlyLoaded);

                    Document document = new Document(descriptor);
                    document.getInternal().putAll(map);

                    return document;
                })

                .collect(Collectors.toList());
    }
}
