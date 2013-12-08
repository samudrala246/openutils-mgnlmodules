<jsp:root version="2.1" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:du="dateutils"
  xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core">
  <jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
  <jsp:directive.page import="java.util.Calendar" />
  <jsp:scriptlet> Calendar rightNow = Calendar.getInstance(); request.setAttribute("data", rightNow);</jsp:scriptlet>
  <jsp:scriptlet> Calendar datadefault = Calendar.getInstance(); datadefault.set(11,03,2,12,45,22);request.setAttribute("datadefault", datadefault);</jsp:scriptlet>
  <html xmlns:javaee="http://java.sun.com/xml/ns/javaee" xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <head>
      <title>datafunction test</title>
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
      p{
        color: blue;
      }
      ]]>
      </style>
    </head>
    <body>
      <div class="function">
        <h2>formatDateTime</h2>
        <p>Format a date and time based on a given pattern, or a builtin style (short, medium, long or full). Also
          supports different styles for date and time with the syntax "date_style;time_style" (e.g. "short;long")</p>
        <div class="sampletext">java.lang.String formatDateTime(java.util.Calendar, java.lang.String, java.lang.String)</div>
        <div class="sampleresult">${du:formatDateTime(rightNow,'d/MM/y','it')}</div>
      </div>
       <div class="function">
        <h2>formatDateTimeWithLocale</h2>
        <p>Format a date and time with a specified language (lowercase two-letter ISO-639 code) based on a given
          pattern, or a builtin style (short, medium, long or full). Also supports different styles for date and time
          with the syntax "date_style;time_style" (e.g. "short;long")</p>
        <div class="sampletext">java.lang.String formatDateTimeWithLocale(java.util.Calendar, java.lang.String, java.lang.String)
        </div>
        <div class="sampleresult">formatDateTimeWithLocale(data,'it','d/MM/y-h:mm'): ${du:formatDateTimeWithLocale(data,'it','d/MM/y-h:mm')}</div>
      </div>
      <div class="function">
        <h2>formatDate</h2>
        <p>Format a date based on a given pattern, or a builtin style (short, medium, long or full)</p>
        <div class="sampletext">java.lang.String formatDate(java.util.Calendar, java.lang.String)
        </div>
        <div class="sampleresult">short: ${du:formatDate(data,"short","it")}<br/>long: ${du:formatDate(data,"long","it")}<br/>full: ${du:formatDate(data,"full","it")}<br/></div>
      </div>
       <div class="function">
        <h2>formatTime</h2>
        <p>Format a time based on a given pattern, or a builtin style (short, medium, long or full)</p>
        <div class="sampletext">java.lang.String formatTime(java.util.Calendar, java.lang.String)
        </div>
        <div class="sampleresult">hh:mm-ss: ${du:formatTime(data,'hh:mm-ss','it')}</div>
        <div class="sampleresult">Short: ${du:formatTime(data,'short','it')}</div>
         <div class="sampleresult">Long : ${du:formatTime(data,'long','it')}</div>
      </div>
      <div class="function">
        <h2>formatInterval</h2>
        <p>format a milliseconds interval as a string like 523h 22m 18s</p>
        <div class="sampletext">java.lang.String formatInterval(java.lang.Long)
        </div>
        <div class="sampleresult">formatInterval(63076234017000): ${du:formatInterval(63076234017000)}</div>
      </div>
       <div class="function">
        <h2>getMillisFromNow</h2>
        <p>retrieve the milliseconds in difference between now and the input date</p>
        <div class="sampletext">java.lang.Long getMillisFromNow(java.util.Calendar)
        </div>
        <div class="sampleresult">${du:getMillisFromNow(datadefault)}</div>
      </div>
       <div class="function">
        <h2>parseDate</h2>
        <p>parse a date with a given pattern and return the parsed date as a calendar object (null safe)</p>
        <div class="sampletext">java.util.Calendar parseDate(java.lang.String, java.lang.String)
        </div>
        <div class="sampleresult">parseDate("22/10/10","d/m/y"): ${du:parseDate("22/10/10","d/m/y")}
        <br/>du:formatDate(du:parseDate("22/10/10","d/m/y"),"short","it") : ${du:formatDate(du:parseDate("22/10/10","d/m/y"),"short","it")}
       </div> 
      </div> 
    </body>
  </html>
</jsp:root>