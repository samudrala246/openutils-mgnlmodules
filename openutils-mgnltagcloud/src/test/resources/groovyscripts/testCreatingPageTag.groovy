package groovyscripts

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.Path;
import info.magnolia.cms.core.search.Query;
import info.magnolia.cms.core.search.QueryResult;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;

HierarchyManager hm = MgnlContext.getSystemContext().getHierarchyManager(RepositoryConstants.WEBSITE)
for (int i = 0; i < 300; i++) {
	content = ContentUtil.getOrCreateContent(hm.getContent("/test"), Path.getValidatedLabel("u" + i), ItemType.CONTENT)
	content.getMetaData().setTemplate("temp")
	NodeDataUtil.getOrCreateAndSet(content, "tags", "pippo");
	hm.save()  
}

return ""