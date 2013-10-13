<section>
  <h3>Dependent select list sample (freemarker)</h3>
  [#if content.foo?has_content]${cmsfn.nodeById(content.foo, "config")}[#else]-[/#if]<br />
  [#if content.bar?has_content]${cmsfn.nodeById(content.bar, "config")}[#else]-[/#if]<br />
  [#if content.baz?has_content]${cmsfn.nodeById(content.baz, "config")}[#else]-[/#if]<br />
</section>
