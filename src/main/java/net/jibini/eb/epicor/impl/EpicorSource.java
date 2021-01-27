package net.jibini.eb.epicor.impl;

import net.jibini.eb.auth.AuthDetails;
import net.jibini.eb.data.DataSource;
import net.jibini.eb.data.Document;
import net.jibini.eb.data.DocumentDescriptor;
import net.jibini.eb.data.Field;
import net.jibini.eb.epicor.Epicor;
import net.jibini.eb.epicor.EpicorCall;
import net.jibini.eb.impl.Classpath;

import org.jetbrains.annotations.NotNull;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An data source which loads documents from an Epicor service. This source can
 * load all documents from an Epicor service, or all entries which have been
 * changed since the previous sync. This implementation can perform incremental
 * syncs for a cache of Epicor data.
 *
 * This implementation is operationally complex and is subject to optimization.
 * All cached Epicor services share this common data source.
 *
 * @author Zach Goethel
 */
@Classpath
public class EpicorSource implements DataSource
{
    // Required to access Epicor configuration
    @Autowired
    private Epicor epicor;

    private final Map<String, Integer> lastLoaded = new HashMap<>();

    @NotNull
    @Override
    public Collection<Document> retrieveFor(@NotNull DocumentDescriptor descriptor)
    {
        // Reset the last loaded number to load all documents
        lastLoaded.put(descriptor.getName(), 0);

        return this.retrieveIncrementalFor(descriptor);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public Collection<Document> retrieveIncrementalFor(@NotNull DocumentDescriptor descriptor)
    {
        // Create the Epicor service for the document
        EpicorCall call = new EpicorCall(descriptor.getName());
        Map<String, String> args = new HashMap<>();

        // Select only the fields that matter
        args.put("$select", descriptor
                .getFields()
                .values()
                .stream()
                .map(Field::getName)
                .collect(Collectors.joining(",")));
        // Filter down to only newly changed entries
        args.put("$filter", String.format("SysRevID gt %d", lastLoaded.getOrDefault(descriptor.getName(), 0)));

        // Call the service and create documents
        return call.call(new AuthDetails(epicor.config.getUsername(), epicor.config.getPassword()), args)
                .getJSONArray("value")
                // Conversion to List converts child elements to Map or List
                // as well; no `JSONObject.toMap()` required later on
                .toList()
                .stream()
                .map((entry) ->
                {
                    Document document = new Document(descriptor);
                    document.getInternal().putAll((Map<String, ?>)entry);

                    return document;
                })
                .collect(Collectors.toList());
    }
}
