var daysInMonth = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);    //每月天数
var today = new Today();    //今日对象
var year = today.year;      //当前显示的年份
var month = today.month;    //当前显示的月份

//页面加载完毕后执行fillBox函数
$(function() {
    fillBox();
});

//今日对象
function Today() {
    this.now = new Date();
    this.year = this.now.getFullYear();
    this.month =this.now.getMonth();
    this.day = parseInt(Appendzero(this.now.getDate()));
    
}

function Appendzero(obj){
	 if(obj<10)
		 return "0" +""+ obj;
	 else 
		 return obj;
}


//根据当前年月填充每日单元格
function fillBox() {
    updateDateInfo();                   //更新年月提示
    $("td.calBox").empty();             //清空每日单元格

    var dayCounter = 1;                 //设置天数计数器并初始化为1
    var cal = new Date(year, month, 1); //以当前年月第一天为参数创建日期对象
    var startDay = cal.getDay();        //计算填充开始位置
    //计算填充结束位置
    var endDay = startDay + getDays(cal.getMonth(), cal.getFullYear()) - 1;

    //如果显示的是今日所在月份的日程，设置day变量为今日日期
    var day = -1;
    if (today.year == year && today.month == month) {
        day = today.day;
    }

    //从startDay开始到endDay结束，在每日单元格内填入日期信息
    for (var i=startDay; i<=endDay; i++) {
        if (dayCounter==day) {
            $("#calBox" + i).html("<div class='date today' id='" + year + "-" +  Appendzero(month + 1) + "-" +  Appendzero(dayCounter) + "' onclick='openAddBox(this)'>" + dayCounter + "</div>");
        } else {
            $("#calBox" + i).html("<div class='date' id='" + year + "-" +  Appendzero(month + 1) + "-" +  Appendzero(dayCounter) + "' onclick='openAddBox(this)'>" + dayCounter + "</div>");
        }
        dayCounter++;
    }
    getTasks();                         //从服务器获取任务信息
}

//从服务器获取任务信息
function getTasks() {
	
    Ext.Ajax.request({
		method:'POST',
		url:MATECH_SYSTEM_WEB_ROOT+'info.do?method=getSchedule',
		success:function (response,options) {
			var json = response.responseText ;
			
			var jsonObj = eval(json);
			for(var i=0;i<jsonObj.length;i++) { 
				 buildTask(jsonObj[i].workdate,i,jsonObj[i].worktype,jsonObj[i].task);
			}
		},
		failure:function (response,options) {
			return false ;
		}
	});
    
    /*
	  buildTask('2012-3-3','10','项目工作','天津泵业集团有限公司2011年报审计');
	  buildTask('2012-3-15','11','市内外勤','上海宝丰');
	  buildTask('2012-3-21','11','市外出差','长春本钢钢铁销售有限公司');
	  */
}

//根据日期、任务编号、任务内容在页面上创建任务节点
function buildTask(buildDate, taskId, taskInfo,title) {
    $("#" + buildDate).parent().append("<div id='task" + taskId + "' class='task' title='"+title+"' onclick='editTask(this)'>" + taskInfo + "</div>");
}

//判断是否闰年返回每月天数
function getDays(month, year) {
    if (1 == month) {
        if (((0 == year % 4) && (0 != (year % 100))) || (0 == year % 400)) {
            return 29;
        } else {
            return 28;
        }
    } else {
        return daysInMonth[month];
    }
}

//显示上月日程
function prevMonth() {
    if ((month - 1) < 0) {
        month = 11;
        year--;
    } else {
        month--;
    }
    fillBox();              //填充每日单元格
}

//显示下月日程
function nextMonth() {
    if((month + 1) > 11) {
        month = 0;
        year++;
    } else {
        month++;
    }
    fillBox();              //填充每日单元格
}

//显示本月日程
function thisMonth() {
    year = today.year;
    month = today.month;
    fillBox();              //填充每日单元格
}

//更新年月提示
function updateDateInfo() {
    $("#dateInfo").html(year + "年" + (month + 1) + "月");
}

//打开新建任务box
function openAddBox(src) {
    $("#taskDate").html(src.id);                    //设置新建日期
    $("#taskInfo").val("");                         //初始化新建任务内容
    var left = getLeft(src) + 15;                   //设置左边距
    var top = getTop(src) + 15;                     //设置顶边距
    $("#addBox").left(left).top(top).slideDown();   //显示新建任务box
}

//向服务器提交新任务信息
function addTask() {
    var taskInfo = $("#taskInfo").val();                //获取任务信息
    //检查任务信息是否为空
    if ($.trim(taskInfo)=="") {
        alert("请输入信息");
    } else {
        var buildDate = $("#taskDate").html();          //获取任务日期
         alert(taskInfo+buildDate);
        $.post("calendar.jsp",                          //服务器页面地址
            {
                action: "addTask",                      //action参数
              //  taskInfo: taskInfo,                     //任务信息参数
               // buildDate: buildDate                    //任务日期参数
                taskInfo:"dddd",
                buildDate: "2011-6-9"
                
            },
            function(taskId) {                          //回调函数
                buildTask(buildDate, taskId, taskInfo); //建立任务节点
                closeAddBox();                          //关闭新建任务box
            }
        );
    }
}

//关闭新建任务box
function closeAddBox() {
    $("#addBox").slideUp();
}

//打开编辑任务box
function editTask(src) {
    $("#taskId").val(src.id.substr(4));             //对任务编号隐藏域赋值
    $("#editTaskInfo").val(src.innerHTML);          //设置编辑内容
    var left = getLeft(src) + 15;                   //设置左边距
    var top = getTop(src) + 15;                     //设置顶边距
    $("#editBox").left(left).top(top).slideDown();  //显示编辑任务box
}

//删除任务
function delTask() {
    var taskId = $("#taskId").val();                //获取任务编号
    $.post("calendar.jsp",                          //服务器页面地址
        {
            action: "delTask",                      //action参数
            taskId: taskId                          //任务编号参数
        },
        function() {                                //回调函数
            $("#task" + taskId).remove();           //在页面删除任务节点
            closeEditBox();                         //关闭编辑box
        }
    );
}

//关闭编辑box
function closeEditBox() {
    $("#editBox").slideUp();
}

//更新任务信息
function updateTask() {
    var taskId = $("#taskId").val();                //任务编号
    var taskInfo = $("#editTaskInfo").val();        //任务内容
    //检查任务信息是否为空
    if ($.trim(taskInfo)=="") {
        alert("请输入任务信息。");
    } else {
        $.post("calendar.jsp",                      //服务器页面地址
            {
                action: "updateTask",               //action参数
                taskId: taskId,                     //年月参数
                taskInfo: taskInfo                  //任务信息参数
            },
            function() {                            //回调函数
                $("#task" + taskId).html(taskInfo); //更新页面任务内容
                closeEditBox();                     //关闭编辑box
            }
        );
    }
}

//返回对象对页面左边距
function getLeft(src){
    var obj = src;
    var objLeft = obj.offsetLeft;
    while(obj != null && obj.offsetParent != null && obj.offsetParent.tagName != "BODY"){
        objLeft += obj.offsetParent.offsetLeft;
        obj = obj.offsetParent;
    }
    return objLeft;
}

//返回对象对页面上边距
function getTop(src){
    var obj = src;
    var objTop = obj.offsetTop;
    while(obj != null && obj.offsetParent != null && obj.offsetParent.tagName != "BODY"){
        objTop += obj.offsetParent.offsetTop;
        obj = obj.offsetParent;
    }
    return objTop;
}
