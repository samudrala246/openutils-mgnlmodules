<!DOCTYPE html>
<html lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>${content.title!}</title>
    
    <script src="${contextPath}/.resources/contextmenu/js/jquery-1.4.2.min.js">/**/</script>
    
    ${contextmenu.links()}
    
    [@cms.init /]
    
    <link rel="stylesheet" type="text/css" href="${contextPath}/.resources/contextmenu/css/bootstrap.min.css" />
    
    <!-- samples css/js -->
    <link rel="stylesheet" type="text/css" href="${contextPath}/.resources/contextmenu/css/contextmenu-samples.css" />
    <script src="${contextPath}/.resources/contextmenu/js/contextmenu-samples.js">/**/</script>
    <!-- end samples css/js -->
  </head>
  <body>
    <div class="container">
      <h1>${content.title!}</h1>
      <section>
        <h2>Freemarker sample</h2>
        <p>
          <span>Add the following to your template in order to initialize the contextmenu module:</span>
          <ul>
            <li>add <code>${'$'}{contextmenu.links()}</code> (just before <code>&lt;/head&gt;</code>)</li>
            <li>add <code>${'$'}{contextmenu.scripts()}</code> (just before <code>&lt;/body&gt;</code>)</li>
            <li>add the jquery library</li>
          </ul>
        </p>
      </section>
      [@cms.area name="main" /]
    </div>
    ${contextmenu.scripts()}
  </body>
</html>