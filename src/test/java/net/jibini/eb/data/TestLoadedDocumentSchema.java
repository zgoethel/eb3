package net.jibini.eb.data;

import net.jibini.eb.epicor.impl.EpicorDateFormat;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

public class TestLoadedDocumentSchema
{
    @Test
    public void canLoadSchemaFromFile()
    {
        File schemaFile = new File("epicor_schema/erp.bo.partsvc_parts.json");
        DocumentDescriptor descriptor = DocumentDescriptor.load(schemaFile);

        // Assert the fields were loaded
        Assert.assertTrue("Schema is missing \"PartNun\"", descriptor
                .getFields()
                .containsKey("PartNum"));

        // Assert certain fields are formatted correctly
        Assert.assertEquals("Hello, world!", descriptor
                .getFields()
                .get("PartNum")
                .getFormat()
                .invoke("Hello, world!"));
        Assert.assertEquals(32.0f, descriptor
                .getFields()
                .get("NetWeight")
                .getFormat()
                .invoke("32"));
        Assert.assertNull(descriptor
                .getFields()
                .get("PartNum")
                .getFormat()
                .invoke(null));
        Assert.assertNull(descriptor
                .getFields()
                .get("NetWeight")
                .getFormat()
                .invoke(null));

        // Get the current time (snapshot)
        Calendar calendar = new GregorianCalendar();
        Instant date = calendar.getTime()
                .toInstant()
                .truncatedTo(ChronoUnit.SECONDS);
        // Use the format as defined in the formatter
        String formatted = EpicorDateFormat.EPICOR_FORMAT.format(date.toEpochMilli());

        // Assert dates are formatted correctly
        Assert.assertEquals(date, descriptor
                .getFields()
                .get("CreatedOn")
                .getFormat()
                .invoke(formatted));
        Assert.assertNull(descriptor
                .getFields()
                .get("CreatedOn")
                .getFormat()
                .invoke(null));
    }

    @Test
    public void canLoadSchemasFromFolder()
    {
        File schemaFolder = new File("epicor_schema");
        Collection<DocumentDescriptor> descriptors = DocumentDescriptor.loadAll(schemaFolder);

        boolean has = false;

        for (DocumentDescriptor descriptor : descriptors)
        {
            if (descriptor.getName().equals("Erp.BO.PartSvc/Parts"))
                has = true;
        }

        // Assert schema(s) were loaded from directory
        Assert.assertTrue("Loaded schema for Epicor Part is missing", has);
    }
}
