package net.jibini.eb.teststand.impl;

import net.jibini.eb.data.Document;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.util.*;

public class TestStandDefinitionImpl
{
    private final List<TestStandDefinition> definitions = new ArrayList<>();

    public TestStandDefinitionImpl(String directory)
    {
        try
        {
            File dir = new File(directory);
            if (!dir.isDirectory())
                throw new IllegalStateException("Test stand definition directory is not a directory");

            File[] files = dir.listFiles((d, fileName) -> fileName.endsWith(".txt"));
            for (File file : Objects.requireNonNull(files))
                definitions.add(new TestStandDefinition(file));
        } catch (IOException ex)
        {
            throw new RuntimeException("Failed to load definition file for workbooks", ex);
        }
    }

    public boolean fill(Document document, Workbook workbook)
    {
        Sheet sheet = workbook.getSheetAt(0);
        Header head = sheet.getHeader();
        String headMatch = head.getLeft() + ", " + head.getCenter() + ", " + head.getRight();

        TestStandDefinition applicable = null;

        for (TestStandDefinition def : definitions)
            if (headMatch.trim().matches(def.regexMatch))
            {
                applicable = def;
                break;
            }
        if (applicable == null)
            return false;

        final TestStandDefinition appFinal = applicable;

        applicable.fieldToCell.forEach((k, v) ->
        {
            CellReference ref = new CellReference(v);
            Cell cell = sheet.getRow(ref.getRow()).getCell(ref.getCol());

            String contents = "";

            if (cell != null)
            {
                switch (cell.getCellType())
                {
                    case BLANK:
                        contents = "";
                        break;
                    case NUMERIC:
                        if (cell.getCellStyle().getDataFormatString().contains("%"))
                            contents = (cell.getNumericCellValue() * 100) + "%";
                        else
                            contents = cell.getNumericCellValue() + "";
                        break;
                    case STRING:
                        contents = cell.getStringCellValue();
                        break;
                    case BOOLEAN:
                        contents = cell.getBooleanCellValue() ? "True" : "False";
                        break;
                    case ERROR:
                        contents = "ERR" + cell.getErrorCellValue();
                        break;
                    case FORMULA:
                        contents = cell.getCellFormula();
                }

                if (appFinal.fieldRegex.containsKey(k))
                {
                    String regex = appFinal.fieldRegex.get(k);
                    Pattern p = Pattern.compile(regex);
                    Matcher m = p.matcher(contents);

                    contents = m.find() ? m.group(m.groupCount()) : "";
                }
            }

            document.getInternal().put(k, contents);
        });

        return true;
    }

    private static class TestStandDefinition
    {
        String category, version;
        String regexMatch;

        final Map<String, String> fieldToCell = new HashMap<>();
        final Map<String, String> fieldRegex = new HashMap<>();

        TestStandDefinition(File file) throws IOException
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            boolean inDescriptors = false;

            while ((line = reader.readLine()) != null)
                if (inDescriptors)
                {
                    if (line.startsWith("#") || line.length() == 0) continue;

                    String[] elements = line.split(",");
                    for (int i = 0; i < elements.length; i++)
                        elements[i] = elements[i].trim();

                    fieldToCell.put(elements[0], elements[2]);
                    if (elements.length > 3)
                        fieldRegex.put(elements[0], elements[3]);
                } else
                {
                    if (line.startsWith("#") || line.length() == 0) continue;

                    if (line.equals("field_descriptors"))
                    {
                        inDescriptors = true;
                        continue;
                    }

                    String[] elements = line.split(":", 2);
                    switch (elements[0])
                    {
                        case "category":
                            this.category = elements[1].trim();
                            break;
                        case "version":
                            this.version = elements[1].trim();
                            break;
                        case "odd_header":
                            this.regexMatch = elements[1].trim();
                            break;
                    }
                }

            reader.close();
        }
    }
}
