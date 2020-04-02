$(function() {
	//初始化新增操作
	initAddCaseOper();
	
	//初始化保存操作
	initSaveCaseOper();
});

//初始化新增操作
function initAddCaseOper(){
	$("#addCaseBtn").click(function(){
		$("#operTypeId").val("add");
	});
}

//初始化编辑操作
function initUpdateCaseOper(id){
	$("#operTypeId").val("update");
	
	$("#caseId").val(id);
	
	var param = new Object();
	param.caseId = id;
	param.docId = $("#docId").val();
	doGet("auth/doc/inter/case/json/info.htm",param,function(interResp){
		//填充表单数据
		$("#caseCustSchemaForm").find("*").setFieldsValue(interResp);
	});
}

//组装相关的表单信息
function serializeCaseSchemaForm(){
	var param = $("#caseCustSchemaForm").find("*").getFieldsValue();
	param.valid = true;
	
	if(!param.respSchema){
		param.valid = false;
	}
	return param;
}

//新增操作
function addCaseOper(){
	var param = serializeCaseSchemaForm();
	if(!param.valid){
		notice("响应不能为空");
		return;
	}
	
	doPost("auth/doc/inter/case/json/add.htm",param,function(data){
		notice("保存成功",function(){
			$("#caseSchemaFormModal").modal("hide");
			$("#caseInfoTab").click();
		});
	});
}

//初始化删除操作
function initDelCaseOper(id){
	bootbox.confirm("确定执行删除操作？",function(){
		var param = new Object();
		param.caseId = id;
		doGet("auth/doc/inter/case/json/del.htm",param,function(){
			$("#caseInfoTab").click();
		});
	});
}

//初始化测试操作
function initTestCaseOper(e){
	var param = new Object();
	param.caseId = e.data;
	param.mock = $('#mock').is(":checked");
	param.docId = $("#docId").val();
	doGet("auth/doc/inter/case/json/test.htm",param,function(data){
		try{
			res = JSON.parse(data.reqSchema);
			console.log(res);
			eval(data.respSchema);
			$(e.target).addClass("btn-info").find("i").removeClass("fa-close").addClass("fa-check");
			return;
		}catch(ex){
			$(e.target).removeClass("btn-info").find("i").removeClass("fa-check").addClass("fa-close");
		}
	});
}
function assert(b,msg){
	if(!b){
		if(!!msg) {
			console.error(msg);
		}
		throw new Error(!msg ? b : msg);
	}
}
function expect(expect, value, msg){
	if(expect!=value){
		msg = !msg ? '期望值: '+expect+', 实际值: '+value : msg;
		console.error(msg);
		throw new Error(msg);
	}
}

//编辑操作
function updateCaseOper(){
	var param = serializeCaseSchemaForm();
	if(!param.valid){
		notice("响应不能为空");
		return;
	}
	
	doPost("auth/doc/inter/case/json/update.htm",param,function(data){
		notice("保存成功",function(){
			$('#caseSchemaFormModal').modal('hide');
			$("#caseInfoTab").click();
		});
	});
}

//初始化保存操作
function initSaveCaseOper(){
	$("#caseCustSchemaForm").bootstrapValidator({
		fields:{
			respSchema:{
                validators: {
                    notEmpty: {
                        message: '响应不能为空'
                    }
                }
			},
			sortWeight:{
                validators: {
                	integer: {
                        message: '权重值只能为整数'
                    },
                    notEmpty: {
                        message: '权重值不能为空'
                    }
                }
			}
		}
	});
	
	$("#saveCaseBtn").click(function(){
		if(isFormValid("caseCustSchemaForm")){
			var operType = $("#operTypeId").val();
			if(operType == 'add'){
				addCaseOper();
			}
			else if(operType == 'update'){
				updateCaseOper();
			}
		}
	});
}

//加载响应信息
function loadCaseInfo(){
	var param = new Object();
	param.interId = $("#interId").val();
	param.docId = $("#docId").val();
	doGet("auth/doc/inter/case/json/list.htm",param,function(paramList){
		var tableBody = $("#caseInfoTable tbody");
		//先清空再加载新数据
		tableBody.empty();
		
		var row;
		var $row;
		var rowData;
		for(var i=0;i < paramList.length; i++){
			rowData = paramList[i];
			row = getTmpl("#caseInfoTmpl")[0];
			$row = $(row);
			
			//设置序号
			$row.find("td:first-child").html(i + 1);
			tableBody.append(row);
			
			$row.find("*").setFieldsValue(rowData);
			
			//初始化更新操作
			$row.find(".oper-update").bind("click",rowData.id,function(e){
				initUpdateCaseOper(e.data);
			});
			
			//初始化删除操作
			$row.find(".oper-del").bind("click",rowData.id,function(e){
				initDelCaseOper(e.data);
			});
			
			//初始化测试操作
			$row.find(".oper-test").bind("click",rowData.id,function(e){
				initTestCaseOper(e);
			});
		}
	});
}