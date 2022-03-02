<#macro rainfo test>

<!-- Hack to include styles as do not have access to online css and js locations -->
<style>
.italic10 {width:10%;font-weight: bold;font-style: italic;}
.bold10 {width:10%;font-weight: bold;}
.width40 {width:40%;}
</style>

<#assign rakey = "Rest Assured">
<#assign items = ["Method", "Status Code", "Endpoint", "Request", "Response"]>

 <#if test.infoMap[rakey]??>
 	<#assign ralist = test.infoMap[rakey]>
		    		
	<#list ralist as ramap>	 		
 		<#assign values=[]>
 		
 		<#list items as item>
			 <#if ramap[item]??>
				<#assign values += [ramap[item]]>			 
			 <#else>
			 	<#assign values += [""]>
			 </#if>
 		</#list>
 		
		<table class="table table-sm">
			<tbody> 				
 				<tr class="event-row">
 					<td class="italic10">${values[0]}</td>
 					<td class="italic10">${values[1]}</td>
 					<td colspan=3>${values[2]}</td>
 				</tr>
 				<tr class="event-row">
 					<td class="bold10">Request</td>
 					<td class="width40" colspan=2>${values[3]}</td>
 					<td class="bold10">Response</td>
 					<td class="width40">${values[4]}</td>
 				</tr>	  				
  			</tbody>
		</table>
	</#list>
</#if>
	
</#macro>