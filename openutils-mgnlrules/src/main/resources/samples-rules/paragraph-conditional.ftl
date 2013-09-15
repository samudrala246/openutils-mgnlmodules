<section>
  <h3>Conditional sample (freemarker)</h3>
  <div>
    <!-- conditional evaluation is performed by the custom renderer -->
    ${cmsfn.decode(content).text!}
  </div>
</section>