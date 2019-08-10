<html>
<#--我只是一个注释，我不会有任何输出  -->

<head>
    <meta charset="UTF-8">
<#include "header.ftl" />
</head>
<body>
<#--插值表达式-->
hello ${name}<br>

<#--定义一个变量并赋值-->
<#assign myvar="zhangsan" />

<#--取出变量的值-->
名称为:${myvar}

<br>
<#--条件判断-->
<#if list?size<4 >
如果是true 我就显示出来
<#else>
如果是false你就是显示出来
</#if>

<#--循环遍历-->
<table>
    <tr>
        <td>id</td>
        <td>name</td>
        <td>index</td>
    </tr>
  <#list list as item>
    <tr>
        <td>${item.id?c}</td>
        <td>${item.name}</td>
        <td>${item_index+1}</td>
    </tr>
  </#list>





</table>


集合的大小:
${list?size}




<#assign myjson="{'id':1,'name':'张三'}" />

<#--?eval 转换josn对象-->
<#assign myobject=myjson?eval />

输出值:${myjson}

输出:${myobject.id}
输出:${myobject.name}


<br>
当前的时间的日期为:${date?date}<br>
当前的时间的时间为:${date?time}<br>
当前的时间的日期和时间为:${date?datetime}<br>
自定义日期的格式:${date?string("yyyy/MM/dd HH:mm:ss")}

<br>
名言:
默认值:
${keynull!}

判断显示

<#if keynull??>
    显示有值
<#else >
    显示没有值

</#if>


比较运算符
<#if 1 lt 2 >
    显示正常
<#else>
    不正常
</#if>






</body>

</html>

