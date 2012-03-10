/**
 *
 * Spring integration module for Magnolia CMS (http://openutils.sourceforge.net/openutils-mgnlspring)
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

package it.openutils.mgnlspring;

import info.magnolia.cms.beans.runtime.Document;
import info.magnolia.cms.beans.runtime.MultipartForm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsFileUploadSupport;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;


/**
 * Spring MagnoliaMultipartResolver which interacts with magnolia.
 * @author fgiust
 * @version $Id:MagnoliaMultipartResolver.java 344 2007-06-30 15:31:28Z fgiust $
 */
public class MagnoliaMultipartResolver extends CommonsFileUploadSupport
    implements
    MultipartResolver,
    ServletContextAware
{

    /**
     * Constructor for use as bean. Determines the servlet container's temporary directory via the ServletContext passed
     * in as through the ServletContextAware interface (typically by a WebApplicationContext).
     * @see #setServletContext
     * @see org.springframework.web.context.ServletContextAware
     * @see org.springframework.web.context.WebApplicationContext
     */
    public MagnoliaMultipartResolver()
    {
        super();
    }

    /**
     * Constructor for standalone usage. Determines the servlet container's temporary directory via the given
     * ServletContext.
     * @param servletContext the ServletContext to use
     */
    public MagnoliaMultipartResolver(ServletContext servletContext)
    {
        this();
        setServletContext(servletContext);
    }

    /**
     * Initialize the underlying <code>org.apache.commons.fileupload.servlet.ServletFileUpload</code> instance. Can be
     * overridden to use a custom subclass, e.g. for testing purposes.
     * @param fileItemFactory the Commons FileItemFactory to use
     * @return the new ServletFileUpload instance
     */
    @Override
    protected FileUpload newFileUpload(FileItemFactory fileItemFactory)
    {
        return new ServletFileUpload(fileItemFactory);
    }

    public void setServletContext(ServletContext servletContext)
    {
        if (!isUploadTempDirSpecified())
        {
            getFileItemFactory().setRepository(WebUtils.getTempDir(servletContext));
        }
    }

    public boolean isMultipart(HttpServletRequest request)
    {
        return FileUploadBase.isMultipartContent(new ServletRequestContext(request));
    }

    @SuppressWarnings("unchecked")
    public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException
    {
        // already parsed by magnolia
        MultipartForm form = (MultipartForm) request.getAttribute("multipartform");

        Map<String, Document> documents = form.getDocuments();
        MultiValueMap<String, MultipartFile> files = new LinkedMultiValueMap<String, MultipartFile>();

        for (String key : documents.keySet())
        {
            Document doc = documents.get(key);
            ArrayList<MultipartFile> filelist = new ArrayList<MultipartFile>();
            filelist.add(new DocWrapperMultipartFile(doc));
            files.put(key, filelist);
        }

        Map<String, String[]> singleParamters = form.getParameters();

        return new DefaultMultipartHttpServletRequest(request, files, singleParamters);

    }

    public static class DocWrapperMultipartFile implements MultipartFile
    {

        Document document;

        public DocWrapperMultipartFile(Document document)
        {
            this.document = document;
        }

        /**
         * {@inheritDoc}
         */
        public byte[] getBytes() throws IOException
        {
            return IOUtils.toByteArray(getInputStream());
        }

        /**
         * {@inheritDoc}
         */
        public String getContentType()
        {
            return document.getType();
        }

        /**
         * {@inheritDoc}
         */
        public InputStream getInputStream() throws IOException
        {
            return document.getStream();
        }

        /**
         * {@inheritDoc}
         */
        public String getName()
        {
            return document.getFileNameWithExtension();
        }

        /**
         * {@inheritDoc}
         */
        public String getOriginalFilename()
        {
            return document.getFileNameWithExtension();
        }

        /**
         * {@inheritDoc}
         */
        public long getSize()
        {
            return document.getLength();
        }

        /**
         * {@inheritDoc}
         */
        public boolean isEmpty()
        {
            return document.getLength() == 0;
        }

        /**
         * {@inheritDoc}
         */
        public void transferTo(File dest) throws IOException, IllegalStateException
        {
            FileUtils.copyFile(document.getFile(), dest);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void cleanupMultipart(MultipartHttpServletRequest request)
    {
        // nothing to do

    }

}
