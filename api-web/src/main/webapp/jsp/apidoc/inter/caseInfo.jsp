<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="panel panel-default margin-top">
	<div class="panel-heading">
		<div class="text-muted bootstrap-admin-box-title">测试案例</div>
		<div class="btn-group pull-right">
			<a href="javascript:void(0);" class="text-muted" onclick="$('.oper-test').trigger('click')">测试</a>
            <input type="checkbox" id="mock" title="mock" checked="checked">
     		<a href="javascript:void(0);" data-toggle="popover" data-placement="top" data-content="排序权重值越小排的越前面。建议权重值间隔50，方便后续调整。" class="text-muted">
            	<i class="fa fa-info-circle"></i> 排序说明
            </a>
 		</div>
	</div>
								
	<div class="bootstrap-admin-panel-content">
		<!-- TABLE SECTION -->
		<div class="row">
			<div class="col-lg-12">
				<table id="caseInfoTable" class="table table-hover table-bordered">
					<thead>
                    	<tr>
                        	<th class="col-lg-1">#</th>
                            <th class="col-lg-2">名称</th>
                            <th class="col-lg-3">请求</th>
                            <th class="col-lg-4">响应</th>
                            <th class="col-lg-2">操作</th>
                        </tr>
                     </thead>
                     
                     <tbody>
              		</tbody>
              	</table>
           	</div>
       	</div>
                    
		<div class="row">
			<div class="col-md-6" style="margin-top: 20px;">
				<button id="addCaseBtn" type="button" class="btn btn-warning" data-toggle="modal" data-target="#caseSchemaFormModal" data-form="caseCustSchemaForm">
					<i class="fa fa-plus"></i> 新增
				</button>
			</div>
		</div>
	</div>
</div>

<div class="row">
	<div class="col-lg-12">
		<div class="modal fade" id="caseSchemaFormModal" tabindex="-1" role="dialog" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title">测试案例</h4>
					</div>
					<div class="modal-body">
						<div class="row">
							<div class="col-lg-12">
								<form id="caseCustSchemaForm" role="form" class="form-horizontal">
									<input id="operTypeId" type="hidden" value="">
									<input id="caseId" name="caseId" type="hidden" value="">
									<input name="docId" type="hidden" value="${param.docId}">
									<input name="interId" type="hidden" value="${param.interId}">
									
									<div class="form-group">
										<label class="control-label col-lg-3">名称</label> 
										<div class="col-lg-6">
											<input name="name" class="form-control" />
										</div>
									</div>
									
									<div class="form-group">
										<label class="control-label col-lg-3">排序权重</label> 
										<div class="col-lg-6">
											<input name="sortWeight" value="0" class="form-control" />
										</div>
									</div>
									
									<div class="form-group">
										<label class="control-label col-lg-3">请求</label> 
										<div class="col-lg-6">
											<textarea name="reqSchema" class="form-control" rows="3" style="height:auto"></textarea>
										</div>
									</div>
									
									<div class="form-group">
										<label class="control-label col-lg-3">响应</label> 
										<div class="col-lg-6">
											<textarea name="respSchema" class="form-control" rows="6" style="height:auto"></textarea>
										</div>
									</div>

								</form>								
							</div>
						</div>
						
					</div>
					
					<div class="modal-footer">
						<div class="row">
							<div class="col-xs-3 text-left">
							</div>
							<div class="col-xs-6 text-left" style="color:red">
								assert(!!res,'res不能为空');
								<br>expect(1,res.userId);
							</div>
							<div class="col-xs-3 text-right">
								<button id="saveCaseBtn" type="button" class="btn btn-success">保存</button>
								<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- END MODAL SECTION -->
<script id="caseInfoTmpl" type="text/html">  
	<tr>
    	<td></td>
		<td>
			<input name="name" type="text" value="" class="form-control" readonly>
		</td>
    	<td>
			<textarea rows="2" class="form-control" name="reqSchema" readonly></textarea>
		</td>
		<td>
			<textarea rows="2" class="form-control" name="respSchema" readonly></textarea>
		</td>	
    	<td class="actions">
			<button type="button" class="btn btn-sm btn-primary oper-update" data-toggle="modal" data-target="#caseSchemaFormModal">
            	<i class="fa fa-pencil"></i> 编辑
			</button>

			<button type="button" class="btn btn-sm btn-danger oper-del">
				<i class="fa fa-trash"></i>删除
			</button>

			<button type="button" class="btn btn-sm oper-test" style="margin-top:8px">
				<i class="fa"></i>测试
			</button>
		</td>
    </tr>
</script>