<!DOCTYPE html>
<html lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>${content.title!}</title>
    [@cms.init /]
    <link rel="stylesheet" type="text/css" href="${contextPath}/.resources/bootstrap/3.0/css/bootstrap.min.css" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" type="text/css" href="${contextPath}/.resources/controls/samples/css/contextmenu-samples.css" />
  </head>
  <body>
    <div class="container">
      <header>
        <h1>${content.title!}</h1>
      </header>
      <div> [@cms.area name="main" /]
      </div>
    </div>
  </body>
</html>