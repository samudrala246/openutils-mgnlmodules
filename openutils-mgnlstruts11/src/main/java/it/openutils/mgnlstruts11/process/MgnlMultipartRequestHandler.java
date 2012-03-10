/**
 *
 * Struts 1.1 module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlstruts.html)
 * Copyright(C) ${project.inceptionYear}-2012, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlstruts11.process;

import info.magnolia.cms.beans.runtime.Document;
import info.magnolia.cms.beans.runtime.MultipartForm;

import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;


/**
 * A <code>MultipartRequestHandler</code> implementation that delegates to the stadard Magnolia Multipart parser.
 * @author fgiust
 * @version $Id$
 */
public class MgnlMultipartRequestHandler implements MultipartRequestHandler
{

    /**
     * Commons Logging instance.
     */
    protected static Log log = LogFactory.getLog(MgnlMultipartRequestHandler.class);

    /**
     * The combined text and file request parameters.
     */
    private Hashtable elementsAll;

    /**
     * The file request parameters.
     */
    private Hashtable<String, FormFile> elementsFile;

    /**
     * The action mapping with which this handler is associated.
     */
    private ActionMapping mapping;

    /**
     * The servlet with which this handler is associated.
     */
    private ActionServlet servlet;

    /**
     * Magnolia multipart form
     */
    private MultipartForm mpf;

    /**
     * Retrieves the servlet with which this handler is associated.
     * @return The associated servlet.
     */
    public ActionServlet getServlet()
    {
        return this.servlet;
    }

    /**
     * Sets the servlet with which this handler is associated.
     * @param servlet The associated servlet.
     */
    public void setServlet(ActionServlet servlet)
    {
        this.servlet = servlet;
    }

    /**
     * Retrieves the action mapping with which this handler is associated.
     * @return The associated action mapping.
     */
    public ActionMapping getMapping()
    {
        return this.mapping;
    }

    /**
     * Sets the action mapping with which this handler is associated.
     * @param mapping The associated action mapping.
     */
    public void setMapping(ActionMapping mapping)
    {
        this.mapping = mapping;
    }

    /**
     * Parses the input stream and partitions the parsed items into a set of form fields and a set of file items. In the
     * process, the parsed items are translated from Commons FileUpload <code>FileItem</code> instances to Struts
     * <code>FormFile</code> instances.
     * @param request The multipart request to be processed.
     * @throws ServletException if an unrecoverable error occurs.
     */
    @SuppressWarnings("unchecked")
    public void handleRequest(HttpServletRequest request) throws ServletException
    {

        // always made available by the renderer
        mpf = (MultipartForm) request.getAttribute(MultipartForm.REQUEST_ATTRIBUTE_NAME);

        // Create the hash tables to be populated.
        elementsFile = new Hashtable<String, FormFile>();
        elementsAll = new Hashtable();

        Map<String, Document> docs = mpf.getDocuments();
        for (Map.Entry<String, Document> doc : docs.entrySet())
        {
            addFileParameter(doc.getKey(), doc.getValue());
        }

        elementsAll.putAll(getTextElements());
        elementsAll.putAll(elementsFile);

    }

    /**
     * Returns a hash table containing the text (that is, non-file) request parameters.
     * @return The text request parameters.
     */
    @SuppressWarnings("unchecked")
    public Hashtable<String, String[]> getTextElements()
    {
        // not really safe, but internally mpf uses hashtables instances
        return ((Hashtable<String, String[]>) mpf.getParameters());
    }

    /**
     * Returns a hash table containing the file (that is, non-text) request parameters.
     * @return The file request parameters.
     */
    @SuppressWarnings("unchecked")
    public Hashtable getFileElements()
    {
        return this.elementsFile;
    }

    /**
     * Returns a hash table containing both text and file request parameters.
     * @return The text and file request parameters.
     */
    @SuppressWarnings("unchecked")
    public Hashtable getAllElements()
    {

        return this.elementsAll;
    }

    /**
     * Cleans up when a problem occurs during request processing.
     */
    public void rollback()
    {
        // nothing to do
    }

    /**
     * Cleans up at the end of a request.
     */
    public void finish()
    {
        // nothing to do
    }

    /**
     * Adds a file parameter to the set of file parameters for this request and also to the list of all parameters.
     * @param item The file item for the parameter to add.
     */
    protected void addFileParameter(String name, Document item)
    {
        FormFile formFile = new MgnlFormFile(item);
        elementsFile.put(name, formFile);
    }

}
