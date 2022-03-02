<#include "../commons/commons-variables.ftl">
<#include "../commons/commons-macros.ftl">
<#include "macros/attributes.ftl">
<#include "macros/log.ftl">
<#include "macros/recurse_nodes.ftl">
<#include "macros/attr-common.ftl">
<#include "macros/rainfo.ftl">

<#assign 
  isbdd=true 
  pageClass="bdd">

<!DOCTYPE html>
<html>
<#include "partials/head.ftl">
<body class="spa ${reportType}-report ${theme}">
  <div class="app">
    <div class="layout">
      <#include "partials/navbar.ftl">
      <#include "partials/sidenav.ftl">
      <div class="vcontainer">
        <div class="main-content">
          <#list viewOrder as view><#include "partials/${view.toString()}.ftl"></#list>
        </div>
      </div>
    </div>
  </div>
  <#include "partials/scripts.ftl">
</body>
</html>