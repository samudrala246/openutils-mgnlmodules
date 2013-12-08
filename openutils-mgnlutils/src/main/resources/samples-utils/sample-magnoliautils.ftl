<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title> ${content.title}
    </title>
    <link rel="stylesheet" type="text/css" href="${contextPath}/.resources/bootstrap/3.0/css/bootstrap.min.css" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <style>
    <![CDATA[
      code { display: block}
      ]]>
    </style>
    [@cms.init /]
  </head>
  <body>
    <div id="main" class="container">
      <div id="header">
        <h1>Magnolia Utils Samples</h1>
      </div>
      
      [#assign "oldcontent" = state.currentContent]
      [#assign "contentMap" = content]
      [#assign "node" = cmsfn.asJCRNode(oldcontent)]
      
      <table class="table table-striped table-bordered table-hover">
        <thead>
          <tr>
            <th>Property</th>
            <th>Node</th>
            <th>ContentMap</th>
            <th>Content</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <th><!--  -->
            </th>
            <td>${node}
            </td>
            <td>${contentMap}
            </td>
            <td>${oldcontent}
            </td>
          </tr>
          <tr>
            <th>class
            </th>
            <td>${node.class.name}
            </td>
            <td>${contentMap.class.name }
            </td>
            <td>${oldcontent.class.name}
            </td>
          </tr>
          <tr>
            <th>UUID
            </th>
            <td>${node.UUID}
            </td>
            <td>${contentMap.UUID}
            </td>
            <td>${oldcontent.UUID}
            </td>
          </tr>
          <tr>
            <th>@uuid
            </th>
            <td>${node['@uuid']}
            </td>
            <td>${contentMap['@uuid']}
            </td>
            <td>${oldcontent['@uuid']}
            </td>
          </tr>
          <tr>
            <th>@UUID
            </th>
            <td>${node['@UUID']}
            </td>
            <td>${contentMap['@UUID']}
            </td>
            <td>${oldcontent['@UUID']}
            </td>
          </tr>
          <tr>
            <th>@id
            </th>
            <td>${node['@id']}
            </td>
            <td>${contentMap['@id']}
            </td>
            <td>${oldcontent['@id']}
            </td>
          </tr>
          <tr>
            <th>handle
            </th>
            <td>${node.handle}
            </td>
            <td>${contentMap.handle}
            </td>
            <td>${oldcontent.handle}
            </td>
          </tr>
          <tr>
            <th>path
            </th>
            <td>${node.path}
            </td>
            <td>${contentMap.path}
            </td>
            <td>${oldcontent.path}
            </td>
          </tr>
          <tr>
            <th>@handle
            </th>
            <td>${node['@handle']}
            </td>
            <td>${contentMap['@handle']}
            </td>
            <td>${oldcontent['@handle']}
            </td>
          </tr>
          <tr>
            <th>@path
            </th>
            <td>${node['@path']}
            </td>
            <td>${contentMap['@path']}
            </td>
            <td>${oldcontent['@path']}
            </td>
          </tr>
          <tr>
            <th>@depth
            </th>
            <td>${node['@depth']}
            </td>
            <td>${contentMap['@depth']}
            </td>
            <td>${oldcontent['@depth']}
            </td>
          </tr>
          <tr>
            <th>@level
            </th>
            <td>${node['@level']}
            </td>
            <td>${contentMap['@level']}
            </td>
            <td>${oldcontent['@level']}
            </td>
          </tr>
          <tr>
            <th>level
            </th>
            <td>${node.level}
            </td>
            <td>${contentMap.level}
            </td>
            <td>${oldcontent.level}
            </td>
          </tr>
          <tr>
            <th>title
            </th>
            <td>${node.title}
            </td>
            <td>${contentMap.title}
            </td>
            <td>${oldcontent.title}
            </td>
          </tr>
          <tr>
            <th>@name
            </th>
            <td>${node['@name']}
            </td>
            <td>${contentMap['@name']}
            </td>
            <td>${oldcontent['@name']}
            </td>
          </tr>
          <tr>
            <th>name
            </th>
            <td>${node.name}
            </td>
            <td>${contentMap.name}
            </td>
            <td>${oldcontent.name}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </body>
</html>