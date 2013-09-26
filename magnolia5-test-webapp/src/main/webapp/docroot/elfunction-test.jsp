<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:su="urn:jsptld:http://openutils.sf.net/openutils-stringutils"
  xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <html xmlns:javaee="http://java.sun.com/xml/ns/javaee" xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <head>
      <title>elfunction test</title>
      <style>
      <![CDATA[
      .function {
      border: 1px solid #ccc;
      padding: 20px;
      }      
      .sampleresult {
        color: red;
      }
      * {
        font-family: tahoma, verdana, arial;
      }
      h3{
        color: blue;
      }
      .funcclass{
       font-style:italic;
      }
      .sampletext{
        margin:5px 0 5px;
      }
      ]]>
      </style>
    </head>
    <body>
      <div class="function">
        <h2>capitaliseAllWords</h2>
        <h3>Capitalizes all the whitespace separated words in a String. Only the first letter of each word is changed
        </h3>
        <div class="funcclass">org.apache.commons.lang.StringUtils</div>
        <div class="sampletext">java.lang.String capitaliseAllWords(java.lang.String)</div>
        <div class="sampleresult">capitaliseAllWords("test one two"): ${su:capitaliseAllWords("test one two")}</div>
      </div>
      <div class="function">
        <h2>substringAfterLast</h2>
        <h3>Gets the substring after the last occurrence of a separator. The separator is not returned.</h3>
        <div class="funcclass">org.apache.commons.lang.StringUtils</div>
        <div class="sampletext">java.lang.String substringAfterLast(java.lang.String, java.lang.String)</div>
        <div class="sampleresult">substringAfterLast("test|xyz","|"): ${su:substringAfterLast("test|xyz","|")}</div>
      </div>
      <div class="function">
        <h2>substringBeforeLast</h2>
        <h3>Gets the substring before the last occurrence of a separator. The separator is not returned.</h3>
        <div class="funcclass">org.apache.commons.lang.StringUtils</div>
        <div class="sampletext">java.lang.String substringBeforeLast(java.lang.String, java.lang.String)</div>
        <div class="sampleresult">substringBeforeLast("test|xyz","|"): ${su:substringBeforeLast("test|xyz","|")}</div>
      </div>
      <div class="function">
        <h2>isBlank</h2>
        <h3>Checks if a String is whitespace, empty ("") or null.</h3>
        <div class="funcclass">org.apache.commons.lang.StringUtils</div>
        <div class="sampletext">boolean isBlank(java.lang.String )</div>
        <div class="sampleresult">isBlank("test"): ${su:isBlank("test")}</div>
      </div>
      <div class="function">
        <h2>randomAlphanumeric</h2>
        <h3>Creates a random string whose length is the number of characters specified.</h3>
        <div class="funcclass">org.apache.commons.lang.RandomStringUtils</div>
        <div class="sampletext">java.lang.String randomAlphanumeric(int)</div>
        <div class="sampleresult">randomAlphanumeric(2): ${su:randomAlphanumeric(2)}</div>
      </div>
      <div class="function">
        <h2>randomInt</h2>
        <h3>Returns a pseudorandom, uniformly distributed int value between 0 (inclusive) and the specified value
          (exclusive), from the Math.random() sequence.</h3>
        <div class="funcclass">org.apache.commons.lang.math.RandomUtils</div>
        <div class="sampletext">int nextInt(int)</div>
        <div class="sampleresult">randomInt(2): ${su:randomInt(2)}</div>
      </div>
      <div class="function">
        <h2>unescapeXml</h2>
        <h3>Unescapes a String</h3>
        <div class="funcclass">org.apache.commons.lang.StringEscapeUtils</div>
        <div class="sampletext">java.lang.String unescapeXml(java.lang.String)</div>
        <div class="sampleresult">unescapeXml("t&#32;est &#33;"): '${su:unescapeXml("t&#32;est &#33;")}'</div>
      </div>
      <div class="function">
        <h2>space</h2>
        <h3>Output a white space.</h3>
        <div class="funcclass">net.sourceforge.openutils.elfunctions.ElStringUtils</div>
        <div class="sampletext">java.lang.String space()</div>
        <div class="sampleresult">space(): ${su:space()}</div>
      </div>
      <div class="function">
        <h2>tab</h2>
        <h3>Output a tab character.</h3>
        <div class="funcclass">net.sourceforge.openutils.elfunctions.ElStringUtils</div>
        <div class="sampleresult">
          <c:set var="stringtoevaluate">first tab ${su:tab()} second tab</c:set>
          stringtoevaluate = first tab tab() second tab
          <br />
          splitOnTabs(stringtoevaluate)[1] : ${su:splitOnTabs(stringtoevaluate)[1]}
        </div>
      </div>
      <div class="function">
        <h2>newline</h2>
        <h3>Output a newline character.</h3>
        <div class="funcclass">net.sourceforge.openutils.elfunctions.ElStringUtils</div>
        <div class="sampletext">java.lang.String newline()</div>
        <div class="sampleresult">
          <c:set var="stringtoevaluate">first row ${su:newline()} secondrow</c:set>
          stringtoevaluate = first row newline() secondrow
          <br />
          splitOnTabs(stringtoevaluate)[1] : ${su:splitOnTabs(stringtoevaluate)[1]} splitNewlines(stringtoevaluate)[1] :
          ${su:splitNewlines(stringtoevaluate)[1]}
        </div>
      </div>
      <div class="function">
        <h2>escJsTxt</h2>
        <h3>Escapes a javascript string. If "true" is passed as parameter, the string is between ", if false is bewteen
          '</h3>
        <div class="funcclass">net.sourceforge.openutils.elfunctions.ElStringUtils</div>
        <div class="sampletext">java.lang.String escapeJsText(java.lang.String, boolean)</div>
        <div class="sampleresult">
          escJsTxt("xyz\\23",true): ${su:escJsTxt("xyz\\23",true)}
          <br />
          escJsTxt("xyz\\23",false): ${su:escJsTxt("xyz\\23",false)}
        </div>
      </div>
      <div class="function">
        <h2>adaptStringLength</h2>
        <h3>Crops a String to a given length, adding a suffix if needed.</h3>
        <div class="funcclass">net.sourceforge.openutils.elfunctions.ElStringUtils</div>
        <div class="sampletext">
          java.lang.String adaptStringLength(java.lang.String, int, java.lang.String)
          <br />
          <strong>java.lang.String:</strong>
          The string to be adapted
          <br />
          <strong>int:</strong>
          The number of chars of the string to be kept
          <br />
          <strong>java.lang.String:</strong>
          The suffix to be added if the string is not complete
        </div>
        <div class="sampleresult">adaptStringLength("pre",2,"post"): ${su:adaptStringLength("pre",2,"post")}</div>
      </div>
      <div class="function">
        <h2>stripHtmlTags</h2>
        <h3>Strip any html tag from a String.</h3>
        <div class="funcclass">net.sourceforge.openutils.elfunctions.ElStringUtils</div>
        <div class="sampletext">java.lang.String stripHtmlTags(java.lang.String)</div>
        <div class="sampleresult">
       <![CDATA[stripHtmlTags("<strong>test</strong>"):]]></div>
        <div class="sampleresult"><![CDATA[${su:stripHtmlTags("<strong>test</strong>")}]]></div>
      </div>
      <div class="function">
        <h2>splitNewlines</h2>
        <h3>Splits the given strings on newlines</h3>
        <div class="funcclass">net.sourceforge.openutils.elfunctions.ElStringUtils</div>
        <c:set var="stringtoevaluate" value="'line1'${su:newline()}'line2'" />
        <div class="sampletext">java.lang.String[] splitNewlines(java.lang.String)</div>
        <div class="sampleresult">
          splitNewlines(${stringtoevaluate})[1]: ${su:splitNewlines(stringtoevaluate)[1]}
          <br />
        </div>
      </div>
      <div class="function">
        <h2>splitOnTabs</h2>
        <h3>Splits the given string on tab characters</h3>
        <div class="funcclass">net.sourceforge.openutils.elfunctions.ElStringUtils</div>
        <div class="sampletext">java.lang.String[] splitOnTabs(java.lang.String)</div>
        <c:set var="stringtoevaluate" value="'tab1'$su.f:tab()}'tab2'" />
        <div class="sampleresult">
          splitOnTabs(${stringtoevaluate})[1]: ${su:splitOnTabs(stringtoevaluate)[1]}
          <br />
        </div>
      </div>
      <div class="function">
        <h2>strip</h2>
        <h3>Strips whitespaces from the start and the end of a String</h3>
        <div class="funcclass">org.apache.commons.lang.StringUtils</div>
        <div class="sampletext">java.lang.String strip(java.lang.String)</div>
        <div class="sampleresult">strip(" strip "): '${su:strip(" strip ")}'</div>
      </div>
      <div class="function">
        <h2>defaultIfEmpty</h2>
        <h3>Returns either the first String parameter or, if it is empty or null, the value of the second String
          parameter</h3>
        <div class="funcclass">org.apache.commons.lang.StringUtils</div>
        <div class="sampletext">
          java.lang.String defaultIfEmpty(java.lang.String, java.lang.String)
          <br />
          <strong>java.lang.String:</strong>
          The string to check if empty or null
          <br />
          <strong>java.lang.String:</strong>
          The default string
        </div>
        <div class="sampleresult">
          defaultIfEmpty(null, "NULL"): ${su:defaultIfEmpty(null, "NULL")}
          <br />
          defaultIfEmpty("", "NULL"): ${su:defaultIfEmpty("", "NULL")}
          <br />
          defaultIfEmpty("bat", "NULL"): ${su:defaultIfEmpty("bat", "NULL")}
          <br />
        </div>
      </div>
      <div class="function">
        <h2>endsWith</h2>
        <h3>A wrapper around java.lang.String#endsWith(..). Tests if this string ends with the specified suffix.</h3>
        <div class="funcclass">net.sourceforge.openutils.elfunctions.ElStringUtils</div>
        <div class="sampletext">
          boolean endsWith(java.lang.String, java.lang.String)
          <br />
          <strong>java.lang.String:</strong>
          The string to compare
          <br />
          <strong>java.lang.String:</strong>
          Specified suffix to search in the first string
        </div>
        <div class="sampleresult">endsWith("otherString","String"): ${su:endsWith("otherString","String")}</div>
      </div>
      <div class="function">
        <h2>shorten</h2>
        <h3>Shorten a text with a number of lines and a number of chars per line to be displayed. Display optional
          ellipses where the line is shortened.</h3>
        <div class="funcclass">net.sourceforge.openutils.elfunctions.ElStringUtils</div>
        <div class="sampletext">
          java.lang.String shorten(java.lang.String, int, int, java.lang.String)
          <br />
          <strong>java.lang.String:</strong>
          The string to be shortened
          <br />
          <strong>int:</strong>
          The number of lines
          <br />
          <strong>int:</strong>
          The number of chars per line
          <br />
          <strong>java.lang.String:</strong>
          Optional ellipses ('...') to display optional ellipses where the line is shortened.
        </div>
        <div class="sampleresult">shorten("text to be shorten",1,3,"..."): ${su:shorten("text to be shorten",1,3,"...")}</div>
      </div>
      <div class="function">
        <h2>toLong</h2>
        <h3>Parse a number, passed as String, and return his Long</h3>
        <div class="funcclass">net.sourceforge.openutils.elfunctions.ElStringUtils</div>
        <div class="sampletext">java.lang.Long toLong(java.lang.String)</div>
        <div class="sampleresult">toLong("27"): ${su:toLong("27")}</div>
      </div>
      <div class="function">
        <h2>toRoundedLong</h2>
        <h3>Parse a number, passed as String, and return his closest rounding as a long (or 0L if parse fails)</h3>
        <div class="funcclass">net.sourceforge.openutils.elfunctions.ElStringUtils</div>
        <div class="sampletext">java.lang.Long toRoundedLong(java.lang.String)</div>
        <div class="sampleresult">toRoundedLong("27.5"): ${su:toRoundedLong("27.5")}</div>
      </div>
      <div class="function">
        <h2>toFloorLong</h2>
        <h3>Parse a double, passed as String, and return his floor as a long (or 0L if parse fails)</h3>
        <div class="funcclass">net.sourceforge.openutils.elfunctions.ElStringUtils</div>
        <div class="sampletext">java.lang.Long toFloorLong(java.lang.String)</div>
        <div class="sampleresult">toFloorLong("27.5"): ${su:toFloorLong("27.5")}</div>
      </div>
      <div class="function">
        <h2>toCeilLong</h2>
        <h3>Parse a double, passed as String, and return his ceil as a long (or 0L if parse fails)</h3>
        <div class="funcclass">net.sourceforge.openutils.elfunctions.ElStringUtils</div>
        <div class="sampletext">java.lang.Long toCeilLong(java.lang.String)</div>
        <div class="sampleresult">toCeilLong("27.5"): ${su:toCeilLong("27.5")}</div>
      </div>
    </body>
  </html>
</jsp:root>