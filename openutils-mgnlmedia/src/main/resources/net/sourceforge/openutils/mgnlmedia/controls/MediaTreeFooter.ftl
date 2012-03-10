     <!-- line for moving to the bottom -->
    <div id="${lineId}" class="mgnlTreeLineInter mgnlLineEnabled" onmouseover="${tree.javascriptTree}.moveNodeHighlightLine('${lineId}');" onmouseout="${tree.javascriptTree}.moveNodeResetLine('${lineId}');"
        onmousedown="${tree.javascriptTree}.pasteNode('${tree.path}',${PASTETYPE_SUB},${permissionWrite?string},'${lineId}');"></div>

    <!-- this is needed for IE else you can't scroll. Do not ask me why! -->
    <div style="position:absolute;" >&nbsp;</div>

    <!-- do we have write permission on the root? -->
    <input type="hidden" name="${tree.javascriptTree}_${tree.path}_PermissionWrite" id="${tree.javascriptTree}_${tree.path}_PermissionWrite" value="${permissionWrite?string}" />

<!--  close the div opened in the header -->
</div>

<!-- for moving -->
<div id="${tree.javascriptTree}_MoveShadow" style="position:absolute;top:0px;left:0px;visibility:hidden;background-color:#fff;"></div>

<img src="${contextPath}${DOCROOT}move_denied.gif" id="${tree.javascriptTree}_MoveDenied" style="position:absolute;top:0px;left:0px;visibility:hidden;" />

<!-- menu divs -->
<#if menu?exists && (menu.menuItems?size >0)>
    ${menu.html}
</#if>
<#if tree.browseMode>
    <#include "/info/magnolia/cms/gui/control/TreeAddressBar.ftl"/>
<#else>
</#if>

<#if !tree.browseMode && tree.functionBar?exists>
    <#assign functionBarHtml = tree.functionBar.html>
    <#assign p = functionBarHtml?index_of('>')>
    ${functionBarHtml?substring(0, p)} style="height: 59px;"><input id="${tree.javascriptTree}AddressBar" type="text" onkeydown="if (mgnlIsKeyEnter(event)) {try{mgnlTreeControl.expandNode(this.value);mgnlTreeControl.openFolder('${contextPath}',this.value,false)}catch(e){}}" class="mgnlDialogControlEdit" style="width: 100%;" value="" />${functionBarHtml?substring(p + 1)}
</#if>

<#include "/info/magnolia/cms/gui/control/TreeJavascript.ftl"/>

<script type="text/javascript">
    // <![CDATA[
    ${tree.javascriptTree}.resize = function(columnNumber){
        //no columnNumber passed: resize all columns (@ resizing window)
        //columnNumber passed: resize only this column and re-clip the one before (@ resizing column)

        if (this.divMain){
            var sizeObj=mgnlGetWindowSize();

            //resize tree div
            var agent=navigator.userAgent.toLowerCase();
            //todo: to be tested!
            if (agent.indexOf("msie")!=-1)
                    this.divMain.style.width=sizeObj.w;
            else
                this.divMain.style.width=sizeObj.w-this.paddingLeft-this.paddingRight-2;

            if(this.browseMode)
                this.divMain.style.height=sizeObj.h - 60;
            else
                this.divMain.style.height=sizeObj.h - 85;

            //resize columns
            var quotient=sizeObj.w/this.getColumnsWidth();
            var sizeObjX=sizeObj;

            //todo: move to init (tree or column?)!!!

            //rules property differs in browsers
            var rulesKey;
            if (document.all){
                rulesKey="rules";
            }
            else if (document.getElementById){
                rulesKey="cssRules";
            }
            mgnlDebug("mgnlTree.resize: running with rules: " + rulesKey, "tree");

            // for each stylesheet included
            var treeColumnClasses=new Object();

            var columnCSSClassFound = false;
            for (var elem0 = document.styleSheets.length-1; elem0>=0; elem0--) {
                mgnlDebug("mgnlTree.resize: styleSheets[elem0].href = " + document.styleSheets[elem0].href, "tree");
                var styleSheet = document.styleSheets[elem0];

                try{
                  var rules=styleSheet[rulesKey];
                }
                catch(e){
                    // in some cases we might not be able to access the style sheets
                    // this is not problematic as we only want to change the inlined styles
                  continue;
                }
                mgnlDebug("mgnlTree.resize: rules", "tree", rules);

                for (var elem1=0; elem1<rules.length; elem1++){

                    var cssClass=rules[elem1].selectorText;
                    // in safar 1.3 the selectorText is in lower case
                    if (cssClass && cssClass.toLowerCase().indexOf("." + this.name.toLowerCase() + "cssclasscolumn")!=-1){
                        treeColumnClasses[cssClass.toLowerCase()]=rules[elem1];
                        columnCSSClassFound = true;
                    }
                }
            }

            // this ensures that we don't silently fail but should never happen
            if(!columnCSSClassFound){
              throw new Error("Can't find column CSS class!");
            }

            mgnlDebug("mgnlTree.resize: treeColumnClasses", "tree", treeColumnClasses);

            var left=0;
            for (var elem=0; elem<this.columns.length; elem++){
                // in safari is it lowercase
                var cssClass="."+this.name.toLowerCase()+"cssclasscolumn"+elem;
                var cssClassObj=treeColumnClasses[cssClass];
                var columnWidth=parseInt(this.columns[elem].width*quotient);
                //resize columne (by setting the left and clip attribute of its css class
                if (cssClassObj){
                    if (!columnNumber || elem==columnNumber){
                        cssClassObj.style.left=left + 'px';
                    }
                    if (!columnNumber || elem==columnNumber || elem==columnNumber-1){
                        cssClassObj.style.clip="rect(0, " + (columnWidth-8) + "px, 100px, 0)";
                    }
                    this.columns[elem].width=columnWidth;

                }

                //place the column resizer
                var columnResizer=document.getElementById(this.name+"ColumnResizer"+elem);
                var columnResizerLine=document.getElementById(this.name+"ColumnLine"+elem);

                if (columnResizer){
                    if (!columnNumber || elem==columnNumber){
                        columnResizer.style.left=(left-this.columnResizerGifWidthHalf-1) + 'px';
                     }
                }

                if (columnResizerLine){
                    if (!columnNumber || elem==columnNumber){
                        columnResizerLine.style.left=left + 'px';
                        columnResizerLine.style.height=this.divMain.style.height;
                     }
                }

                left+=columnWidth;
            }
        }
    };
    // ]]>
</script>
