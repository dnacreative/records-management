<@markup id="rm-css" target="css" action="after" scope="global">
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/rm/components/document-details/node-header.css" group="document-details"/>
</@>

<@markup id="rm-widgets" target="html" action="after" scope="global">
   <#if isClassified>
      <div class="status-banner theme-bg-color-2 theme-border-4 classified-info classified-banner">
         <span>${node.properties["clf:currentClassification"].label?upper_case}</span>
      </div>
   </#if>
</@markup>