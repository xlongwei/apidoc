$(function() {
	//初始化新增操作
	initAddMockOper();
	
	//初始化保存操作
	initSaveMockOper();
});

//初始化新增操作
function initAddMockOper(){
	$("#addMockBtn").click(function(){
		$("#operTypeId").val("add");
	});
}

//初始化编辑操作
function initUpdateMockOper(id){
	$("#operTypeId").val("update");
	
	$("#mockId").val(id);
	
	var param = new Object();
	param.mockId = id;
	param.docId = $("#docId").val();
	doGet("auth/doc/inter/mock/json/info.htm",param,function(interResp){
		//填充表单数据
		$("#mockCustSchemaForm").find("*").setFieldsValue(interResp);
	});
}

//组装相关的表单信息
function serializeMockSchemaForm(){
	var param = $("#mockCustSchemaForm").find("*").getFieldsValue();
	param.valid = true;
	
	if(!param.respSchema){
		param.valid = false;
	}
	return param;
}

//新增操作
function addMockOper(){
	var param = serializeMockSchemaForm();
	if(!param.valid){
		notice("响应不能为空");
		return;
	}
	
	doPost("auth/doc/inter/mock/json/add.htm",param,function(data){
		notice("保存成功",function(){
			$("#mockSchemaFormModal").modal("hide");
			$("#mockInfoTab").click();
		});
	});
}

//初始化删除操作
function initDelMockOper(id){
	bootbox.confirm("确定执行删除操作？",function(){
		var param = new Object();
		param.mockId = id;
		doGet("auth/doc/inter/mock/json/del.htm",param,function(){
			$("#mockInfoTab").click();
		});
	});
}

//编辑操作
function updateMockOper(){
	var param = serializeMockSchemaForm();
	if(!param.valid){
		notice("响应不能为空");
		return;
	}
	
	doPost("auth/doc/inter/mock/json/update.htm",param,function(data){
		notice("保存成功",function(){
			$('#mockSchemaFormModal').modal('hide');
			$("#mockInfoTab").click();
		});
	});
}

//初始化保存操作
function initSaveMockOper(){
	$("#mockCustSchemaForm").bootstrapValidator({
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
	
	$("#saveMockBtn").click(function(){
		if(isFormValid("mockCustSchemaForm")){
			var operType = $("#operTypeId").val();
			if(operType == 'add'){
				addMockOper();
			}
			else if(operType == 'update'){
				updateMockOper();
			}
		}
	});
}

//加载响应信息
function loadMockInfo(){
	var param = new Object();
	param.interId = $("#interId").val();
	param.docId = $("#docId").val();
	doGet("auth/doc/inter/mock/json/list.htm",param,function(paramList){
		var tableBody = $("#mockInfoTable tbody");
		//先清空再加载新数据
		tableBody.empty();
		
		var row;
		var $row;
		var rowData;
		for(var i=0;i < paramList.length; i++){
			rowData = paramList[i];
			row = getTmpl("#mockInfoTmpl")[0];
			$row = $(row);
			
			//设置序号
			$row.find("td:first-child").html(i + 1);
			tableBody.append(row);
			
			$row.find("*").setFieldsValue(rowData);
			
			//初始化更新操作
			$row.find(".oper-update").bind("click",rowData.id,function(e){
				initUpdateMockOper(e.data);
			});
			
			//初始化删除操作
			$row.find(".oper-del").bind("click",rowData.id,function(e){
				initDelMockOper(e.data);
			});
		}
	});
}