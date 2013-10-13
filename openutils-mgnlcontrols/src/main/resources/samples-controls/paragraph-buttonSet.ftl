<section>
  <h3>Buttonset sample (freemarker)</h3>
  ${content.foo!'-'}<br />
  [#list content.bar as option] ${option} [/#list] <br />
  ${content.baz!'-'}<br />
 </section>