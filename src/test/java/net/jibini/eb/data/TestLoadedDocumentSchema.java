package net.jibini.eb.data;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

public class TestLoadedDocumentSchema
{
    @Test
    public void canLoadSchemaFromFile()
    {
        File schemaFile = new File("schema/erp.bo.partsvc_parts.json");
        DocumentDescriptor descriptor = DocumentDescriptor.load(schemaFile);

        Assert.assertTrue("Schema is missing \"PartNun\"",
                descriptor.getFields()
                        .containsKey("PartNum"));

        Assert.assertEquals("Hello, world!",
                descriptor.getFields()
                        .get("PartNum")
                        .getFormat()
                        .invoke("Hello, world!"));
    }

    @Test
    public void canLoadSchemasFromFolder()
    {
        File schemaFolder = new File("schema");
        Collection<DocumentDescriptor> descriptors = DocumentDescriptor.loadAll(schemaFolder);

        boolean has = false;

        for (DocumentDescriptor descriptor : descriptors)
        {
            if (descriptor.getName().equals("Erp.BO.PartSvc/Parts"))
                has = true;
        }

        Assert.assertTrue("Loaded schema for Epicor Part is missing", has);
    }
}
