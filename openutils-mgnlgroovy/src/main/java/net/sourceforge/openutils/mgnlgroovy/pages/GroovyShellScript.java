/**
 *
 * Groovy Shell for Magnolia CMS (http://www.openmindlab.com/lab/products/groovy.html)
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

package net.sourceforge.openutils.mgnlgroovy.pages;

import groovy.lang.GroovyShell;
import groovy.lang.GroovySystem;
import groovy.lang.Script;
import info.magnolia.cms.beans.runtime.Document;
import info.magnolia.cms.util.AlertUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.admininterface.TemplatedMVCHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Manuel Molaschi
 * @version $Id$
 */
public class GroovyShellScript extends TemplatedMVCHandler
{

    private static final String LINE_SEP = System.getProperty("line.separator");

    private static final String GROOVY_VERSION = GroovySystem.getVersion();

    private static Logger log = LoggerFactory.getLogger(GroovyShellScript.class);

    private String groovyScript;

    private Document groovyScriptFile;

    private String title;

    public GroovyShellScript(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);

    }

    public String save()
    {
        if (StringUtils.isBlank(title))
        {
            title = "script";
        }

        response.setHeader(
            "Content-Disposition",
            "attachment;filename=\""
                + title
                + (StringUtils.endsWith(title, ".groovy") ? StringUtils.EMPTY : ".groovy")
                + "\"");
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try
        {
            response.getWriter().print(groovyScript);
            response.flushBuffer();
        }
        catch (IOException e)
        {
            log.error("Error writing file to response", e);
        }

        return null;
    }

    public String run()
    {
        response.setContentType("text/html");
        response.setStatus(200);
        // response.setBufferSize(1);
        response.setCharacterEncoding("UTF-8");

        if (StringUtils.isBlank(groovyScript))
        {
            String msg = "No script to execute";
            log.warn(msg);
            AlertUtil.setMessage(msg);
            return this.show();
        }

        log.debug("executing script {}", groovyScript);

        Object retVal;
        // final OutputStream os = osTmp;

        PrintWriter w = null;
        try
        {
            w = response.getWriter();
        }
        catch (IOException e1)
        {
        }
        w
            .print("<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"><title>Groovy shell execution console</title><link href=\"");
        w.print(request.getContextPath());
        w
            .println("/.resources/groovyshell/css/console.css\" type=\"text/css\" rel=\"stylesheet\" /></head><body><pre>");
        w.println("Running script...");
        w.println("--------------------------------------\n");
        w.flush();

        try
        {
            GroovyShell shell = new GroovyShell();
            shell.setProperty("ctx", MgnlContext.getInstance());

            PrintWriter pout = new PrintWriter(w)
            {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void print(String s)
                {
                    super.print(s);
                    super.flush();
                }

            };
            shell.setProperty("out", pout);
            shell.setProperty("err", pout);
            Script script = shell.parse(getGroovyScript());
            retVal = script.run();
            if (retVal != null)
            {
                w.println("\n--------------------------------------");
                w.println("Return value is:");
                w.println(retVal);
            }
            // AlertUtil.setMessage("Script executed successfully. Return value is " + retVal);
        }

        catch (CompilationFailedException e)
        {
            log.error("An error occurred while parsing the script. Error message is {}", e.getMessage());
            AlertUtil.setMessage("An error occurred while parsing the script. Error message is " + e.getMessage());
            w.println("<span class=\"err\">");
            e.printStackTrace(w);
            w.println("</span>");
        }
        catch (Throwable e)
        {
            log.error("An error occurred while executing the script. Error message is " + e.getMessage(), e);
            AlertUtil.setMessage("An error occurred while executing the script. Error message is "
                + e.getMessage()
                + "\n"
                + ExceptionUtils.getFullStackTrace(e));
            w.println("<span class=\"err\">");
            e.printStackTrace(w);
            w.println("</span>");
        }
        w.print("</pre></body></html>");

        return null; // this.show();
    }

    public String load()
    {
        if (groovyScriptFile == null)
        {
            String msg = "Please, select a file";
            log.warn(msg);
            AlertUtil.setMessage(msg);
            return this.show();
        }
        if (!groovyScriptFile.getExtension().equalsIgnoreCase("groovy"))
        {
            String msg = groovyScriptFile.getFileNameWithExtension() + " doesn't seem to be a valid groovy file";
            log.warn(msg);
            AlertUtil.setMessage(msg);
            return this.show();
        }
        groovyScript = readFile(groovyScriptFile.getFile());

        String msg = groovyScriptFile.getFileNameWithExtension() + " loaded";
        log.info(msg);
        AlertUtil.setMessage(msg);
        return this.show();

    }

    private String readFile(File file)
    {
        StringBuilder contents = new StringBuilder();

        try
        {
            // use buffering, reading one line at a time
            // FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(file));
            try
            {
                String line = null; // not declared within while loop
                /*
                 * readLine is a bit quirky : it returns the content of a line MINUS the newline. it returns null only
                 * for the END of the stream. it returns an empty String if two newlines appear in a row.
                 */
                while ((line = input.readLine()) != null)
                {
                    contents.append(line);
                    contents.append(LINE_SEP);
                }
            }
            finally
            {
                IOUtils.closeQuietly(input);
            }
        }
        catch (IOException ex)
        {
            log.error(ex.getMessage());
        }
        return contents.toString();
    }

    public String getGroovyScript()
    {
        return groovyScript;
    }

    public void setGroovyScript(String groovyScript)
    {
        this.groovyScript = groovyScript;
    }

    public String getGroovyVersion()
    {
        return GROOVY_VERSION;
    }

    public Document getGroovyScriptFile()
    {
        return groovyScriptFile;
    }

    public void setGroovyScriptFile(Document groovyScriptFile)
    {
        this.groovyScriptFile = groovyScriptFile;
    }
}