<jsp:root version="2.1" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:cms="urn:jsptld:cms-taglib"
  xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core" xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt"
  xmlns:media="http://net.sourceforge.openutils/mgnlMedia" xmlns:cmsu="urn:jsptld:cms-util-taglib" xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld"
  xmlns:su="http://openutils.sf.net/openutils-stringutils" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:mu="mgnlutils"
  xmlns:du="dateutils" xmlns:fdt="urn:jsptagdir:/WEB-INF/tags/fieradigitale-tags" xmlns:mgnlt="urn:jsptagdir:/WEB-INF/tags/mgnltags"
  xmlns:fd="fieradigitale">
  <jsp:directive.attribute name="jqueryui" required="false" rtexprvalue="true" type="java.lang.Boolean" />
  <jsp:directive.attribute name="jquery" required="false" rtexprvalue="true" type="java.lang.Boolean" />
  <c:if test="${mgnl.editMode and cmsfn:canEdit()}">
    <c:if test="${not empty jquery and jquery}">
      <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"><!-- -->
      </script>
    </c:if>
    <c:if test="${not empty jqueryui and jqueryui}">
      <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.5/jquery-ui.min.js"><!-- -->
      </script>
    </c:if>
    <script type="text/javascript">
        <![CDATA[
        (function($) {
          var currentMsgEdit = null;
          var editLink = $('<a href="#" title="edit" \
              style="position:absolute;background:#000 url(${appCtx}/.resources/messages/icons/edit.png) center \
              center no-repeat;border-radius:4px;-moz-border-radius:4px;\
              -webkit-border-radius:4px;width:24px;height:24px;z-index:1000; top:-5px; left:-24px;"></a>');
        
          function init(container, dialog) {
            container.css({
              "-webkit-border-radius" : 4,
              "-moz-border-radius" : 4,
              "border-radius" : 4
            });
            var msgKey = container.attr("data-msgkey");
            var msgsWithSameKey = $("." + msgKey.replace(/\./g, '_'));
            var msgLocale = container.attr("data-msglocale");
            var msgDefaultLocale = container.attr("data-msgdefaultlocale");
            var defaultMsg = container.attr("data-msgdefault");
            
            container.append(editLink.clone()).css('position','relative').find('a')
           	.fadeTo('slow',0.3)
            .live('mouseenter',function(){
            	$(this).fadeTo('slow',1.0);
            	msgsWithSameKey.animate({"background-color" : 'yellow'});
            })
            .live('mouseleave',function(){
              editLink.fadeTo('slow',0.3);
              msgsWithSameKey.css({"background-color" : 'transparent'});
            })
            .live('click',function(evt){
              evt.preventDefault();
              currentMsgEdit = container;
              $("#dialog-msgEdit-default").css({
                display: (msgDefaultLocale == msgLocale) ? "none" : "block"
              });
              if (msgDefaultLocale != msgLocale) {
                $("#dialog-msgEdit-default-message").text(defaultMsg);
              }
              dialog.dialog( "open" );
            });
          };
          
          $.fn.mgnlMessageEdit = function(params) { 
            var dialog = $("#dialog-msgEdit").dialog({
              autoOpen: false,
              height: 220,
              width: 350,
              modal: true,
              buttons: {
                "Save": function() {
                  var valueTxt = $("#dialog-msgEdit-value").val();
                  var msgKey = currentMsgEdit.attr('data-msgkey');
                  if (valueTxt != currentMsgEdit.text().trim())
                  {
                    $("." + msgKey.replace(/\./g, '_')).text(valueTxt).append(editLink.clone()).find('a').fadeTo('slow',0.3);
                    $.ajax({
                      url: '${appCtx}/messages/save',
                      type: 'POST',
                      data: {
                        key: msgKey,
                        locale: currentMsgEdit.attr('data-msglocale'),
                        text: valueTxt
                      },
                      error: function(err) {
                        alert(err);
                      },
                      success: function(msg) {
                      }
                    });
                    $( this ).dialog( "close" );
                  }
                },
                "Cancel": function() {
                  $( this ).dialog( "close" );
                }
              },
              open: function() {
                $("#dialog-msgEdit-value").val(currentMsgEdit.text().trim());
              },
              close: function() {
              }
                      
            });
            $(this).each(function(i,el){
              init($(el), dialog); 
            });
          }; 
          
        })($);
        
        $('head').append('<link type="text/css" rel="stylesheet" href="${appCtx}/.resources/messages/css/messages.css" />');
        $('head').append('<link type="text/css" rel="stylesheet" href="${appCtx}/.resources/messages/css/jquery-ui-1.8.5.custom.css" />');
            
        $(document).ready(function(){            
          $(".msgEdit").mgnlMessageEdit();
        });
        ]]>
    </script>
    <div id="dialog-msgEdit" class="msgEditDialog" title="Edit message">
      <p id="dialog-msgEdit-default">
        Message in default locale is:
        <br />
        <span id="dialog-msgEdit-default-message" style="font-style:italic"><!--  -->
        </span>
      </p>
      <form id="dialog-msgEdit-form" action="${appCtx}/messages/save">
        <fieldset>
          <textarea id="dialog-msgEdit-value"><!--  -->
          </textarea>
        </fieldset>
      </form>
    </div>
  </c:if>
</jsp:root>