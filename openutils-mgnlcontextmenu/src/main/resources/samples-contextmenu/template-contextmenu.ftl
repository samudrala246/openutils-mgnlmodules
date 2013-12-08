<!DOCTYPE html>
<html lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>${content.title!}</title>
    
    ${contextmenu.init()}

    <script src="${contextPath}/.resources/contextmenu/js/jquery-1.4.2.min.js">/**/</script>
    ${contextmenu.links()}
    
    [@cms.init /]
    
    <link rel="stylesheet" type="text/css" href="${contextPath}/.resources/bootstrap/3.0/css/bootstrap.min.css" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    
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
            <li>add <code>${'$'}{contextmenu.init()}</code> if you are using blossom (just before <code>&lt;/head&gt;</code>)</li>
            <li>add the jquery library</li>
            <li>add <code>${'$'}{contextmenu.links()}</code> (just before <code>&lt;/head&gt;</code>)</li>
            <li>add <code>${'$'}{contextmenu.scripts()}</code> (just before <code>&lt;/body&gt;</code>)</li>
          </ul>
        </p>
      </section>
      [@cms.area name="main" /]
    </div>
    ${contextmenu.scripts()}
  </body>
</html>