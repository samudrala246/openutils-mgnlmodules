/**
 *
 * Messages Module for Magnolia CMS (http://www.openmindlab.com/lab/products/messages.html)
 * Copyright(C) 2008-2013, Openmind S.r.l. http://www.openmindonline.it
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sourceforge.openutils.mgnlmessages.pages;

import info.magnolia.cms.beans.runtime.Document;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.util.AlertUtil;
import info.magnolia.module.admininterface.TemplatedMVCHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlmessages.MessagesUtils;
import net.sourceforge.openutils.mgnlmessages.configuration.MessagesConfigurationManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author molaschi
 * @version $Id: MessagesImpExpPage.java 5694 2008-12-18 13:49:18Z manuel $
 */
public class MessagesImpExpPage extends TemplatedMVCHandler
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(MessagesImpExpPage.class);

    private Document uploadFile;

    /**
     * @param name
     * @param request
     * @param response
     */
    public MessagesImpExpPage(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    /**
     * Export translations to an excel file
     * @return view
     */
    public String exportExcel()
    {
        HSSFWorkbook wb = new HSSFWorkbook();

        Map<String, Set<Messages>> messages = new HashMap<String, Set<Messages>>();
        List<Locale> locales = MessagesConfigurationManager.getAvaiableLocales();

        for (String basename : MessagesConfigurationManager.getBaseNames())
        {

            Set<Messages> msgs = new LinkedHashSet<Messages>();
            for (Locale locale : locales)
            {
                log.debug("Adding locale {}", locale);
                NoDefaultBundlesMessagesImpl bundle = new NoDefaultBundlesMessagesImpl(basename, locale);
                msgs.add(bundle);
                bundle.get("");
            }

            messages.put(basename, msgs);
        }

        // style for the header
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(font);
        // for each messages basename, create a worksheet

        for (String basename : messages.keySet())
        {

            // Sets the sheet name. Will throw IllegalArgumentException if the name is duplicated or contains /\?*[]
            // Note - Excel allows sheet names up to 31 chars in length but other applications allow more. Excel does
            // not crash with names longer than 31 chars, but silently truncates such names to 31 chars. POI enforces
            // uniqueness on the first 31 chars.
            String sheetname = StringUtils.replace(basename, "info.magnolia.module.", "i.m.m.");

            HSSFSheet sheet;

            try
            {
                sheet = wb.createSheet(sheetname);
            }
            catch (IllegalArgumentException e)
            {
                log.warn("Duplicated basename found, skipping: \"{}\"", basename);
                continue;
            }

            // create sheet header (key + locales)
            HSSFRow header = sheet.createRow((short) 0);
            HSSFCell headerCell = header.createCell(0);
            headerCell.setCellStyle(style);
            headerCell.setCellValue("KEY");

            // get the keys for this basename and sort them alphabetically
            Set<Messages> msgs = messages.get(basename);

            log.debug("Processing {} with {} locales", basename, msgs.size());

            Set<String> keys = new TreeSet<String>();
            for (Messages smessage : msgs)
            {
                log.debug("Extracting keys from locale {}", smessage.getLocale());
                @SuppressWarnings("unchecked")
                Iterator<String> it = smessage.keys();
                while (it.hasNext())
                {
                    keys.add(it.next());
                }
            }

            log.debug("Bundle {} has {} keys", basename, keys.size());

            int r = 1;
            // for each key create a row in the sheet
            for (String key : keys)
            {
                int c = 0;
                HSSFRow row = sheet.createRow(r);
                HSSFCell cell = row.createCell(c);
                cell = row.createCell(c++);
                // cell.setEncoding(HSSFWorkbook.ENCODING_UTF_16);
                cell.setCellValue(key);

                for (Messages entry : msgs)
                {

                    // get back to header row and set the locale for the corresponding value
                    header = sheet.getRow(0);
                    headerCell = header.createCell(c);
                    headerCell.setCellStyle(style);
                    Locale locale = entry.getLocale();
                    String localeCode = locale.getLanguage();
                    if (!StringUtils.isEmpty(locale.getCountry()))
                    {
                        localeCode += "_" + locale.getCountry();
                    }
                    headerCell.setCellValue(localeCode);
                    String value = entry.get(key);
                    cell = row.createCell(c);
                    if (value != null)
                    {
                        cell.setCellValue(value);
                    }
                    c++;
                }
                r++;
            }
        }

        response.setContentType("application/vnd.ms-excel; name=\"messages.xls\"");
        response.addHeader("content-disposition", "attachment; filename=\"messages.xls\"");

        try
        {
            wb.write(response.getOutputStream());
        }
        catch (IOException e)
        {
            log.error("Exception writing excel to response", e);
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * Import an excel file.
     * @return view name
     */
    public String importExcel()
    {
        if (uploadFile == null)
        {
            AlertUtil.setMessage("Please select an excel file for upload");
            return this.show();
        }
        FileInputStream fis;
        try
        {
            fis = new FileInputStream(uploadFile.getFile());
        }
        catch (FileNotFoundException e)
        {
            log.error("Error opening uploaded file", e);
            AlertUtil.setMessage("Error opening uploaded file, check that it is an Excel file", e);
            return this.show();
        }

        HSSFWorkbook wb;
        try
        {
            /*
             * File tmp = File.createTempFile("excel", ".xls"); FileOutputStream fos = new FileOutputStream(tmp);
             * IOUtils.copy(fis, fos); fis.close(); fos.close();
             */
            ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
            IOUtils.copy(fis, byteOS);
            fis.close();
            byte[] allBytes = byteOS.toByteArray();

            // create workbook from array:
            InputStream byteIS = new ByteArrayInputStream(allBytes);
            wb = new HSSFWorkbook(byteIS);
            byteIS.close();
        }
        catch (IOException e)
        {
            log.error("Error opening uploaded file", e);
            AlertUtil.setMessage("Error opening uploaded file, check that it is an Excel file", e);
            return this.show();
        }
        for (int k = 0; k < wb.getNumberOfSheets(); k++)
        {
            HSSFSheet sh = wb.getSheetAt(k);
            HSSFRow row = sh.getRow(0);
            List<String> locales = new ArrayList<String>();
            for (short i = 2; i <= row.getLastCellNum(); i++)
            {
                HSSFCell cell = row.getCell((i - 1));
                String locale = cell.getStringCellValue();
                if (StringUtils.isEmpty(locale))
                {
                    break;
                }
                locales.add(locale);
            }

            for (int r = 1; r < sh.getLastRowNum(); r++)
            {
                row = sh.getRow(r);
                String key = row.getCell(0).getStringCellValue();
                if (StringUtils.isEmpty(key))
                {
                    break;
                }
                for (int c = 1; c < row.getLastCellNum(); c++)
                {
                    if (row.getCell(c) == null)
                    {
                        continue;
                    }
                    String value = row.getCell(c).getStringCellValue();
                    if (!StringUtils.isEmpty(value))
                    {
                        try
                        {
                            MessagesUtils.saveKeyValue(key, value, locales.get(c - 1));
                        }
                        catch (RepositoryException e)
                        {
                            log.error("Error saving key " + key + " on locale " + locales.get(c - 1), e);
                        }
                    }
                }
            }
        }

        AlertUtil.setMessage("Translations successfully loaded");

        return this.show();
    }

    /**
     * Returns the uploadFile.
     * @return the uploadFile
     */
    public Document getUploadFile()
    {
        return uploadFile;
    }

    /**
     * Sets the uploadFile.
     * @param uploadFile the uploadFile to set
     */
    public void setUploadFile(Document uploadFile)
    {
        this.uploadFile = uploadFile;
    }

    @Override
    public Messages getMsgs()
    {
        return super.getMsgs();
    }

}