/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
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

package net.sourceforge.openutils.mgnlmedia.media.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.Binary;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidLifecycleTransitionException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.MergeException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.ActivityViolationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;


/**
 * @author fgiust
 * @version $Id: FileNodeData.java 4331 2013-09-20 12:38:39Z fgiust $
 */
public class FileNode implements Node
{

    private byte[] content;

    private String extension;

    /**
     * @param classpathLocation
     * @throws IOException
     */
    public FileNode(String classpathLocation) throws IOException
    {
        InputStream is = getClass().getResourceAsStream(classpathLocation);

        if (is == null)
        {
            throw new IllegalArgumentException("Classpath resource " + classpathLocation + " cannot be found");
        }
        try
        {
            extension = StringUtils.substringAfterLast(classpathLocation, ".");

            content = IOUtils.toByteArray(is);
        }
        finally
        {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getAttribute(String key)
    {
        if (StringUtils.equals(key, "extension"))
        {
            return this.extension;
        }
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getStream()
    {
        return new ByteArrayInputStream(content);
    }

    /**
     * {@inheritDoc}
     */
    public String getPath() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Item getAncestor(int depth) throws ItemNotFoundException, AccessDeniedException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Node getParent() throws ItemNotFoundException, AccessDeniedException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int getDepth() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public Session getSession() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNode()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNew()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isModified()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSame(Item otherItem) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void accept(ItemVisitor visitor) throws RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void save() throws AccessDeniedException, ItemExistsException, ConstraintViolationException,
        InvalidItemStateException, ReferentialIntegrityException, VersionException, LockException,
        NoSuchNodeTypeException, RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void refresh(boolean keepChanges) throws InvalidItemStateException, RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void remove() throws VersionException, LockException, ConstraintViolationException, AccessDeniedException,
        RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public Node addNode(String relPath) throws ItemExistsException, PathNotFoundException, VersionException,
        ConstraintViolationException, LockException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Node addNode(String relPath, String primaryNodeTypeName) throws ItemExistsException, PathNotFoundException,
        NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void orderBefore(String srcChildRelPath, String destChildRelPath)
        throws UnsupportedRepositoryOperationException, VersionException, ConstraintViolationException,
        ItemNotFoundException, LockException, RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public Property setProperty(String name, Value value) throws ValueFormatException, VersionException, LockException,
        ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Property setProperty(String name, Value value, int type) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Property setProperty(String name, Value[] values) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Property setProperty(String name, Value[] values, int type) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Property setProperty(String name, String[] values) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Property setProperty(String name, String[] values, int type) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Property setProperty(String name, String value) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Property setProperty(String name, String value, int type) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Property setProperty(String name, InputStream value) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Property setProperty(String name, Binary value) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Property setProperty(String name, boolean value) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Property setProperty(String name, double value) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Property setProperty(String name, BigDecimal value) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Property setProperty(String name, long value) throws ValueFormatException, VersionException, LockException,
        ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Property setProperty(String name, Calendar value) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Property setProperty(String name, Node value) throws ValueFormatException, VersionException, LockException,
        ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Node getNode(String relPath) throws PathNotFoundException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public NodeIterator getNodes() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public NodeIterator getNodes(String namePattern) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public NodeIterator getNodes(String[] nameGlobs) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Property getProperty(String relPath) throws PathNotFoundException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public PropertyIterator getProperties() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public PropertyIterator getProperties(String namePattern) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public PropertyIterator getProperties(String[] nameGlobs) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Item getPrimaryItem() throws ItemNotFoundException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getUUID() throws UnsupportedRepositoryOperationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getIdentifier() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int getIndex() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public PropertyIterator getReferences() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public PropertyIterator getReferences(String name) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public PropertyIterator getWeakReferences() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public PropertyIterator getWeakReferences(String name) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNode(String relPath) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasProperty(String relPath) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNodes() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasProperties() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public NodeType getPrimaryNodeType() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public NodeType[] getMixinNodeTypes() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNodeType(String nodeTypeName) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setPrimaryType(String nodeTypeName) throws NoSuchNodeTypeException, VersionException,
        ConstraintViolationException, LockException, RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void addMixin(String mixinName) throws NoSuchNodeTypeException, VersionException,
        ConstraintViolationException, LockException, RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void removeMixin(String mixinName) throws NoSuchNodeTypeException, VersionException,
        ConstraintViolationException, LockException, RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public boolean canAddMixin(String mixinName) throws NoSuchNodeTypeException, RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public NodeDefinition getDefinition() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Version checkin() throws VersionException, UnsupportedRepositoryOperationException,
        InvalidItemStateException, LockException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void checkout() throws UnsupportedRepositoryOperationException, LockException, ActivityViolationException,
        RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void doneMerge(Version version) throws VersionException, InvalidItemStateException,
        UnsupportedRepositoryOperationException, RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void cancelMerge(Version version) throws VersionException, InvalidItemStateException,
        UnsupportedRepositoryOperationException, RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void update(String srcWorkspace) throws NoSuchWorkspaceException, AccessDeniedException, LockException,
        InvalidItemStateException, RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public NodeIterator merge(String srcWorkspace, boolean bestEffort) throws NoSuchWorkspaceException,
        AccessDeniedException, MergeException, LockException, InvalidItemStateException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getCorrespondingNodePath(String workspaceName) throws ItemNotFoundException,
        NoSuchWorkspaceException, AccessDeniedException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public NodeIterator getSharedSet() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void removeSharedSet() throws VersionException, LockException, ConstraintViolationException,
        RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void removeShare() throws VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public boolean isCheckedOut() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void restore(String versionName, boolean removeExisting) throws VersionException, ItemExistsException,
        UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void restore(Version version, boolean removeExisting) throws VersionException, ItemExistsException,
        InvalidItemStateException, UnsupportedRepositoryOperationException, LockException, RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void restore(Version version, String relPath, boolean removeExisting) throws PathNotFoundException,
        ItemExistsException, VersionException, ConstraintViolationException, UnsupportedRepositoryOperationException,
        LockException, InvalidItemStateException, RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void restoreByLabel(String versionLabel, boolean removeExisting) throws VersionException,
        ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException,
        RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public VersionHistory getVersionHistory() throws UnsupportedRepositoryOperationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Version getBaseVersion() throws UnsupportedRepositoryOperationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Lock lock(boolean isDeep, boolean isSessionScoped) throws UnsupportedRepositoryOperationException,
        LockException, AccessDeniedException, InvalidItemStateException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Lock getLock() throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException,
        RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void unlock() throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException,
        InvalidItemStateException, RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public boolean holdsLock() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLocked() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void followLifecycleTransition(String transition) throws UnsupportedRepositoryOperationException,
        InvalidLifecycleTransitionException, RepositoryException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public String[] getAllowedLifecycleTransistions() throws UnsupportedRepositoryOperationException,
        RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
