package net.jibini.eb.teststand.impl;

import net.jibini.eb.data.Document;
import net.jibini.eb.data.DocumentDescriptor;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Stores pre-loaded data in a binary file for easier pre-parsed access later
 * upon software restart; allows avoidance of loading the same data twice.
 *
 * @author Zach Goethel
 */
public class StoreFile implements Closeable
{
    /**
     * Max string length for field names in the header.
     */
    private static final int MAX_FIELD_NAME_LENGTH = 32;

    /**
     * Max string length for field values in the body.
     */
    private static final int MAX_FIELD_VALUE_LENGTH = 128;

    /**
     * The file in which this store file's data is stored.
     */
    private final RandomAccessFile file;

    /**
     * Document descriptor for the type of document stored in this random-access
     * file; required for header data.
     */
    private final DocumentDescriptor descriptor;

    /**
     * Tracks the order which fields are stored in the file.
     */
    private final Map<Integer, String> fieldIndices = new HashMap<>();

    /**
     * Address of the first datapoint entry in the file.
     */
    private final long dataStart;

    /**
     * Loads or creates the provided file and its header data.
     *
     * @param descriptor Document descriptor for the type of document stored in
     * `    this random-access file; required for header data.
     * @param file File where the data will be stored (binary file).
     */
    public StoreFile(DocumentDescriptor descriptor, File file)
    {
        try
        {
            this.descriptor = descriptor;

            if (file.getParentFile() != null)
                file.getParentFile().mkdirs();
            boolean created = file.createNewFile();

            this.file = new RandomAccessFile(file, "rw");
            this.file.seek(0L);

            if (created)
                this.writeHeader();
            else
                this.readHeader();

            this.dataStart = this.file.getFilePointer();
        } catch (IOException ex)
        {
            throw new RuntimeException("Failed to open random-access store file", ex);
        }
    }

    private void writeHeader() throws IOException
    {
        file.seek(0L);
        file.writeInt(descriptor.getFields().size());

        int[] j = { 0 };

        descriptor.getFields().forEach((k, v) ->
        {
            try
            {
                int i = 0;

                for (; i < v.getName().length() && i < MAX_FIELD_NAME_LENGTH; i++)
                    file.writeChar(v.getName().charAt(i));
                for (; i < MAX_FIELD_NAME_LENGTH; i++)
                    file.writeChar('\0');

                fieldIndices.put(j[0]++, v.getName());
            } catch (IOException ex)
            {
                throw new RuntimeException("Failed to initialize random-access store file header", ex);
            }
        });
    }

    private void readHeader() throws IOException
    {
        file.seek(0L);
        int size = file.readInt();

        byte[] nameBuffer = new byte[MAX_FIELD_NAME_LENGTH * 2];

        for (int i = 0; i < size; i++)
        {
            file.read(nameBuffer, 0, nameBuffer.length);

            String str = new String(nameBuffer, StandardCharsets.UTF_16).trim();
            fieldIndices.put(i, str);
        }
    }

    /**
     * @return Collection of all documents cached in this store file.
     */
    public Collection<Document> loadAll()
    {
        try
        {
            List<Document> result = new ArrayList<>();
            file.seek(dataStart);
            byte[] buffer = new byte[MAX_FIELD_VALUE_LENGTH * 2];

            while (file.getFilePointer() < file.length())
                try
                {
                    Document document = new Document(descriptor);

                    for (int i = 0; i < fieldIndices.size(); i++)
                    {
                        file.read(buffer, 0, buffer.length);

                        String str = new String(buffer, StandardCharsets.UTF_16).trim();
                        document.getInternal().put(fieldIndices.get(i), str);
                    }

                    result.add(document);
                } catch (IOException ex)
                {
                    throw new RuntimeException("Failed to read a store file entry", ex);
                }

            return result;
        } catch (IOException ex)
        {
            throw new RuntimeException("Failed to load store file data", ex);
        }
    }

    /**
     * Writes the provided document into the store file cache.
     *
     * @param document Document with data to write.
     */
    public void write(Document document)
    {
        try
        {
            file.seek(file.length());

            for (int i = 0; i < fieldIndices.size(); i++)
            {
                String value = document.getString(fieldIndices.get(i));
                if (value == null) value = "null";

                int j = 0;
                for (; j < MAX_FIELD_VALUE_LENGTH && j < value.length(); j++)
                    file.writeChar(value.charAt(j));
                for (; j < MAX_FIELD_VALUE_LENGTH; j++)
                    file.writeChar('\0');
            }
        } catch (IOException ex)
        {
            throw new RuntimeException("Failed to write provided document");
        }
    }

    @Override
    public void close() throws IOException
    {
        file.close();
    }
}
