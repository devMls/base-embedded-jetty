<#function tieneRol rol>
  <#if session.roles??>
     <#if session.roles?seq_contains(rol)>
        <#return true>
     <#else>
       <#return false>
    </#if>
  <#else>
    <#return false>
  </#if>
</#function>

<#function filter things name value>
    <#local result = []>
    <#list things as thing>
        <#if thing[name] == value>
            <#local result = result + [thing]>
        </#if>
    </#list>
    <#return result>
</#function>

<#function filterLast things name value>
    <#local result = []>
    <#list things as thing>
        <#if thing[name] == value>
            <#local result =  [thing]>
        </#if>
    </#list>
    <#return result>
</#function>