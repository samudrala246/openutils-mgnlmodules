// Same as mgnlOpenDialog, but with the possibility to specify extra parameters
function mgnlOpenDialogEx(path,nodeCollection,node,paragraph,repository,dialogPage,width,height,locale,extraParams)
    {

    //dialog window is resized in  dialog itself (window.resize)
    if (!width) width=800;
    if (!height) height=100;

    //magnolia edit window: add browser information (needed for rich editor)
    var agent=navigator.userAgent.toLowerCase();

    var richE="false";
    var richEPaste="";
    var richESupported=false;
    if (document.designMode)
        {
        //safari has designMode...
        if (agent.indexOf("safari")==-1) richESupported=true;
        }
    if (richESupported)
        {
        //richedit
        richE="true";
        if (agent.indexOf("mac")!=-1) richEPaste="false";
        else if (agent.indexOf("msie")!=-1)  richEPaste="button";
        else richEPaste="textarea";
        }

    if (!dialogPage){
        dialogPage = ".magnolia/dialogs/" + paragraph + ".html";
    }


    var url=contextPath;
    url+="/"+ dialogPage;
    if(path){
        url = mgnl.util.URLUtil.addParameter(url,"mgnlPath", path);
    }
    if (nodeCollection) {
        url = mgnl.util.URLUtil.addParameter(url,"mgnlNodeCollection", nodeCollection);
    }
    if (node) {
        url = mgnl.util.URLUtil.addParameter(url,"mgnlNode", node);
    }
    if (paragraph) {
        url = mgnl.util.URLUtil.addParameter(url,"mgnlParagraph", paragraph);
    }
    if(repository){
        url = mgnl.util.URLUtil.addParameter(url,"mgnlRepository", repository);
    }
    if(locale){
        url = mgnl.util.URLUtil.addParameter(url,"mgnlLocale", locale);
    }
    if(extraParams){
        for(var paramName in extraParams){
            url = mgnl.util.URLUtil.addParameter(url,paramName, extraParams[paramName]);
        }
    }
    url = mgnl.util.URLUtil.addParameter(url,"mgnlRichE", richE);
    url = mgnl.util.URLUtil.addParameter(url,"mgnlRichEPaste", richEPaste);

    url = mgnl.util.URLUtil.addParameter(url,"mgnlCK", mgnlGetCacheKiller());

    var w=window.open(mgnlEncodeURL(url),"mgnlDialog"+mgnlGetCacheKiller(),"width="+width+",height="+height+"scrollbars=no,status=yes,resizable=yes");
    if (w) w.focus();
}
