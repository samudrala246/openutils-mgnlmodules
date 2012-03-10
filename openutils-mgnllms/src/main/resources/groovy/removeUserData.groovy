package groovyscripts;
import info.magnolia.cms.beans.config.ContentRepository
import info.magnolia.cms.core.Content
import info.magnolia.cms.core.HierarchyManager
import info.magnolia.cms.core.ItemType
import info.magnolia.cms.core.NodeData
import info.magnolia.cms.core.MetaData
import info.magnolia.cms.core.Path
import info.magnolia.cms.security.AccessManager
import info.magnolia.cms.security.Permission
import info.magnolia.cms.util.ContentUtil
import info.magnolia.cms.util.NodeDataUtil
import info.magnolia.context.Context
import info.magnolia.context.MgnlContext
import java.util.Calendar
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.jcr.AccessDeniedException
import javax.jcr.RepositoryException
import org.apache.commons.lang.StringUtils
import org.apache.commons.io.FileUtils
import java.text.SimpleDateFormat

import info.magnolia.cms.core.search.Query
import info.magnolia.cms.core.search.QueryManager;
import info.magnolia.cms.core.search.QueryResult;
import java.util.Collection
import java.util.Set
import java.util.HashSet

Logger log = LoggerFactory.getLogger('groovy')

hm = MgnlContext.getSystemContext().getHierarchyManager("lms")
hmConfig = MgnlContext.getSystemContext().getHierarchyManager("config")
qm = MgnlContext.getInstance().getQueryManager("lms");

count = 0;
str = "";

Query q = qm.createQuery("//element(users,mgnl:contentNode)/*/*", Query.XPATH);
QueryResult qres = q.execute();
Collection<Content> users = qres.getContent("mgnl:contentNode");

Set<String> handlesToDelete = new HashSet<String>()

for(item in users)
{
    if(item.name != "adl_data")
    {
	    log.debug("user node: "+item.name);
	    Query q2 = qm.createQuery("//element("+item.name+",mgnl:contentNode)", Query.XPATH);
	    QueryResult qres2 = q2.execute();
	    Collection<Content> contents= qres2.getContent("mgnl:contentNode");
	
	    for(content in contents)
	    {
	        //log.debug("HANDLE node: "+content.handle);
	        handlesToDelete.add(content.handle);
	    }
    }
}

handlesToDelete.each{
    log.debug("delete node: "+it);
    hm.delete(it);
    count++;
    hm.save();
}



def today= new Date() 


return str + "\n" + count + " users deleted, "+today