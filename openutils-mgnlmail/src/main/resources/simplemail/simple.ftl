
[#assign cms=JspTaglibs["cms-taglib"]] [@cms.mainBar dialog="simplemail-simple" /]
<html>
<head>
<title>${content.title!}</title>
</head>
<body>
<table cellpadding="0" cellspacing="0" border="0" align="center" width="100%" bgcolor="#FFF">
  <tbody>
    <tr>
      <td align="center" style="margin:0;padding:0;background-color:#F1F1E5;padding:35px 0">
        <table cellpadding="0" cellspacing="0" border="0" align="center" width="650" style="font-family:Georgia, serif">
          <tbody>
            <tr>
              <td height="101" align="center" bgcolor="#102942" style="color: #fff">
                <a href="${content.link!}" target="_blank"> [#if content.logo! != ""] ${media.url(media.node(content.logo))} [/#if]</a>
                ${content.title!}
              </td>
            </tr>
          </tbody>
        </table>
        <table cellpadding="0" cellspacing="0" border="0" align="center" width="650" style="font-family:Georgia, serif;background:#fff" bgcolor="#ffffff">
          <tbody>
            <tr>
              <td width="14" style="font-size:0px" bgcolor="#ffffff">
              </td>
              <td width="620" valign="top" align="left" bgcolor="#ffffff" style="font-family:Georgia, serif;background:#fff">
                <div style="padding: 20px;">
[@cms.contentNodeIterator contentNodeCollectionName="main"]
    [@cms.editBar /]
    [@cms.includeTemplate /]
[/@cms.contentNodeIterator]
[@cms.newBar contentNodeCollectionName="main" newLabel="New Content" paragraph="simplemail-text" /]


</div>
              </td>
              <td width="16" bgcolor="#ffffff" style="font-family:Georgia, serif;background:#fff">
              </td>
            </tr>
          </tbody>
        </table>
        <table cellpadding="0" cellspacing="0" border="0" align="center" width="650" style="font-family:Georgia, serif;line-height:10px" bgcolor="#FFF">
          <tbody>
            <tr>
              <td align="center" valign="top" bgcolor="#102942" style="padding:15px 0 10px;font-size:11px;color:#fff;margin:0;line-height:1.2;font-family:Georgia, serif"> ${content.disclaimer!}</td>
            </tr>
            <tr>
              <td align="center" valign="top" style="padding:10px 0;font-size:11px;color:#000;margin:0;line-height:1.2;font-family:Georgia, serif;background-color:#FFF"
                bgcolor="#FFF"> ${content.footer!}</td>
            </tr>
          </tbody>
        </table>
      </td>
    </tr>
  </tbody>
</table>
</body>
</html>