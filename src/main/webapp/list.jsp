<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>显示</title>
    <style type="text/css">
        .sty{
            padding-left: 150px;
        }

        a:{font-size:16px}
        a:link {color: #8B8989; text-decoration:none;} /* 未访问：蓝色、无下划线 */
        a:active:{color: #8DB6CD; } /* 激活：红色 */
        a:visited {color:purple;text-decoration:none;} /* 已访问：紫色、无下划线 */
        a:hover {color: #8B8989; text-decoration:underline;} /* 鼠标移近：红色、下划线 */

        .dv{
            margin-left:150px;
        }
    </style>
</head>
<script src="jquery-3.6.0.min.js"></script>
<script type="text/javascript">
    function bb(t) {
        var href = $(t).attr("href");//获取当前对象的文件路径
        document.getElementById("loadFilePath").value = href;
        $("#download").submit();
    }

    //搜索查询
    function Search(){
        var pageSize = $("#pageSize").val();//每页显示几条
        var pageNo = 1;//当前页
        // var pageCount6 = 0;
        // var pageSelect6 = 1;
        // Data(pageNo, pageSize, pageCount6, pageSelect6);
        Data(pageNo, pageSize);
    }
    function Page(page3, pageSize3, pageCount3, pageSelect3){
        // if(0 == page3){
        //     page3 = 1;
        // }
        // if(0 == pageSelect3){
        //     pageSize3 = $("#pageSize").val();
        // }
        if(1 == pageSize3){
            pageSize3 = $("#pageSize").val();//获取每页显示的条数
        }
        Data(page3, pageSize3);
    }

    // 初始化(搜索)分页查询
    function Data(page4, pageSize4){
        var page2 = page4;
        if("" == page2){
            page2 = 1;
        }
        var pageSize2 = pageSize4;
        if("" == pageSize2 || null == pageSize2){
            pageSize2 = 10;
        }
        var select2 = $("#va").val();
        var b='\\\\';

        $.ajax({
            type:"get", //请求方式     对应form的  method请求
            url:"page", // 请求路径  对应 form的action路径
            cache: false,  //是否缓存，false代表拒绝缓存
            data:{"pageNo":page2,"pageSize":pageSize2, "select":select2},  //传参
            dataType: "json",   //返回值类型
            success:function(data){
                if("1" == data){
                    alert("有没有返回值");
                }else{
                    var list = data.files;
                    var html = "";
                    $("#t_body").empty();//移除t_body中的内容
                    for(var i in list){
                        html += "<a href='"+list[i].fileUrl+"' onclick='bb(this)'>" + list[i].fileName + "</a>&nbsp;&nbsp;&nbsp;&nbsp;" +
                            "<span>"+ list[i].fileSize +"</span><br />" +
                            "<span>"+ list[i].fileContent +"</span><br /><hr>"
                        ;
                    }
                    $("#t_body").append(html);//在结尾插入分页
                    var page1 = "";
                    page1 = "总记录数:<span>" + data.pageSizeSum + "</span>&nbsp;&nbsp;&nbsp;&nbsp;" +
                        "总页数:<span>" + data.pageCount + "</span>&nbsp;&nbsp;&nbsp;&nbsp;" +
                        "当前页:<span id='inPage'>" + data.pageNo + "</span>&nbsp;&nbsp;&nbsp;&nbsp;" +
                        "每页记录数:<select name='pageSize' id='pageSize' onchange='Page(" + data.pageNo + ", 1, " + data.pageCount + ",0, " + '' + ")'>" +
                        "<option value='5' >5</option>" +
                        "<option value='10' >10</option>" +
                        "<option value='20' >20</option>" +
                        "<option value='50' >50</option>" +
                        "</select><br />"
                    ;
                    if(data.pageNo > 1){
                        page1 = page1 + "<button onclick='Page(1," + pageSize2 + ", " + data.pageCount + ",1, " + '' + ")'>首页</button>&nbsp;&nbsp;";
                        page1 = page1 + "<button onclick='Page(" + (data.pageNo-1) + "," + pageSize2 + ", " + data.pageCount + ",1, " + '' + ")'>上一页</button>&nbsp;&nbsp;";
                    }
                    for (var c = 1; c < data.pageCount+1; c++) {
                        page1 = page1 + "<button onclick='Page(" + c + "," + pageSize2 + ", " + data.pageCount + ",1, " + '' + ")'>"+ c + "</button>&nbsp;&nbsp;";
                    }
                    if(data.pageNo < data.pageCount){
                        page1 = page1 + "<button onclick='Page(" + (data.pageNo+1) + "," + pageSize2 + ", " + data.pageCount + ",1, " + '' + ")'>下一页</button>&nbsp;&nbsp;";
                        page1 = page1 + "<button onclick='Page(" + data.pageCount + "," + pageSize2 + ", " + data.pageCount + ",1, " + '' + ")'>尾页</button>&nbsp;&nbsp;";
                    }

                    $("#divPage").html(page1);//改变当前标签的内容
                    $("#pageSize").val(pageSize2);// 设置下拉框默认显示值

                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert(XMLHttpRequest.status);
                alert(XMLHttpRequest.readyState);
                alert(textStatus);
            }
        });
    }

</script>
<body>
<form action="user" method="POST" id="user">
    <input type="hidden" name="filePath" id="filePath" />
</form>
<form action="download" method="POST" id="download">
    <input type="hidden" name="loadFilePath" id="loadFilePath" />
</form>

<c:if test="${!empty requestScope.error}">
    <font color="#8B8989" class="sty">${error }</font>
</c:if>

<c:if test="${empty requestScope.fileList1 && empty requestScope.fileList && empty requestScope.error }">
    <form action="<%=request.getContextPath()%>/query" name="myform" target="u">
        <font color="#8B8989">请输入要搜索的关键字:</font> <input type="text" class="sc" placeholder="关键词" onfocus="this.placeholder=''" onblur="this.placeholder='请输入关键词'" name="value" ></input>
        <input type="submit" value="搜索" class="btn bto" id="seach"></input>
    </form>
</c:if>

<input type="hidden" value="${value}" id="va">
<div id="t_body">
</div>
<div id="divPage">
</div>

<%--<c:if test="${!empty value}">--%>
    <script>
        Search();
    </script>
<%--</c:if>--%>
</body>
</html>
