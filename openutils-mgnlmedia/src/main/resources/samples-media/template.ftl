<!DOCTYPE html>
<html lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>${content.title}</title>
    [@cms.init /]
    <link rel="stylesheet" type="text/css" href="${contextPath}/.resources/media-samples/css/bootstrap.min.css" />
    <link rel="stylesheet" type="text/css" href="${contextPath}/.resources/contextmenu/css/media-samples.css" />
  </head>
  <body>
    <div class="container">
      <h1>${content.title}</h1>
      [@cms.area name="main" /]
    </div>
  </body>
</html>