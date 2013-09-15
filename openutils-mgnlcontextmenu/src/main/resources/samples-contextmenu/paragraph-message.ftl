[#assign contextmenu=JspTaglibs["http://openutils/mgnlcontextmenu"]]
<section>
  <h3>Message sample (freemarker)</h3>
  <p>Right click to edit the message below</p>
  <pre>${'[#assign contextmenu=JspTaglibs["http://openutils/mgnlcontextmenu"]]'}
${'[@contextmenu.element name="message"]'}${'$'}{content.message}${'[/@contextmenu.element]'}</pre>
  <div class="testcontent">
    [@contextmenu.element name="message"]${content.message!}[/@contextmenu.element]
  </div>
</section>