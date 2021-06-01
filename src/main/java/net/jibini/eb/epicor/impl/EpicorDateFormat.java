package net.jibini.eb.epicor.impl;

import net.jibini.eb.data.FieldFormat;
import net.jibini.eb.impl.Classpath;

import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Parses a stored date in Epicor into an instance of {@link Instant}.
 *
 * @author Zach Goethel
 */
@Classpath
public class EpicorDateFormat implements FieldFormat
{
    /**
     * The standard date format used by Epicor.
     */
    public static final DateFormat EPICOR_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault());

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Object format(Object date)
    {
        // If date is null, blank, or not a string, return null
        if (date == null || date.equals("") || (!(date instanceof String) && !(date instanceof Instant)))
            return null;

        try
        {
            // Attempt to parse the date
            return EPICOR_FORMAT.parse(date.toString()).toInstant();
        } catch (ParseException ex)
        {
            log.error(String.format("Failed to parse Epicor date '%s'", date.toString()));

            return null;
        }
    }

    @NotNull
    public String formatString(Object date)
    {
        Instant instant = (Instant)format(date);
        if (instant == null) return "-";

        return DISPLAY_FORMAT.format(instant)
            .replace(" 00:00:00", "");
    }

    @Override
    public boolean filter(
        @Nullable Object value,
        @NotNull String fieldName,
        @NotNull Map<String, String[]> args
    )
    {
        //TODO
        return true;
    }
}
