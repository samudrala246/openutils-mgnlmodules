classDef("mgnl.lms", {
  searchView :false,

  // this variable is used by the context menu
  selectedPath :"",
  selectedIsFolder :false,

  // this is the object used in the context menu to check the flag
  selectedIsNotFolderCondition : {
    test : function() {
      return !mgnl.lms.selectedIsFolder
    }
  },

  show : function(path, dialog) {
    mgnlOpenWindow(".magnolia/dialogs/" + dialog + ".html?mgnlPath=" + path
        + "&mgnlRepository=lms");
  },

  createNew : function(path, dialog) {
    mgnlOpenWindow(".magnolia/dialogs/" + dialog + ".html?mgnlPath=" + path
        + "&mgnlNode=mgnlNew&mgnlRepository=scorm");// parent.OpenWindow
  },

  // check if it is a folder
  showDialogInTree : function(tree, dialog) {
    if (tree.selectedNode.itemType == "mgnl:scorm") {
      mgnlTreeMenuOpenDialog(tree, '.magnolia/dialogs/' + dialog + '.html');
    }
    if (tree.selectedNode.itemType == "mgnl:course") {
      mgnlTreeMenuOpenDialog(tree, '.magnolia/dialogs/' + dialog + '.html');
    }
  },

  reloadAfterEdit : function(path) {
    if (!mgnl.lms.searchView) {
      document.location = contextPath + "/.magnolia/trees/lms.html?mgnlCK="
          + mgnlGetCacheKiller() + "&pathSelected=" + path +"&pathOpen=" + path;
    } else {
      document.location.reload();
    }
  },

  showInTree : function(path) {
    MgnlAdminCentral.showTree('lms', path);
  },

  showTree : function() {
    top.mgnlAdminCentralSwitchExtractTree('lms');
  },

  simulate : function(path) {

   // decommentare prima di rilasciare!
   // mgnlOpenWindow(".magnolia/pages/scormplayer.html?mgnlPath=" + path
   //     + "&mgnlRepository=lms" ,800, 600);

   // commentami prima del rilascio, usami solo per i test
    var w=window.open(contextPath+"/.magnolia/pages/scormplayer.html?mgnlPath=" + path+ "&mgnlRepository=scorm");
    if (w) w.focus();

  },
  
  userreport : function(uuid) {

	   // decommentare prima di rilasciare!
	   // mgnlOpenWindow(".magnolia/pages/scormplayer.html?mgnlPath=" + path
	   //     + "&mgnlRepository=lms" ,800, 600);

	   // commentami prima del rilascio, usami solo per i test
	    var w=window.open(contextPath+"/.magnolia/pages/userReport.html?handle=" + uuid);
	    if (w) w.focus();

   },
	  
   userreportadmin : function(uuid) {

		   // decommentare prima di rilasciare!
		   // mgnlOpenWindow(".magnolia/pages/scormplayer.html?mgnlPath=" + path
		   //     + "&mgnlRepository=lms" ,800, 600);

		   // commentami prima del rilascio, usami solo per i test
		    var w=window.open(contextPath+"/.magnolia/pages/userReportAdmin.html?handle=" + uuid);
		    if (w) w.focus();

	}
});