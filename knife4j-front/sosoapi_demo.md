
**sosoapi_demo**


**简介**：该demo主要用于汇集常见的接口编辑事例。
<br/>
接口测试过程中如果出现ajax跨域请求问题可参考"常见问题->线下部署"中的跨域请求相关资料。
<br/>
<br/>
技术交流群:957806743


**HOST**:



**联系人**:


**Version**:1.0.0

**接口路径**：


# 入门
## 新增用户

**接口描述**:新增用户信息

**接口地址**:`/apidoc/mock/user/simple/add.htm`


**请求方式**：`POST`


**consumes**:`["application/json"]`


**produces**:`["application/json"]`



**请求参数**：

| 参数名称         | 参数说明     |     in |  是否必须      |  数据类型  |  schema  |
| ------------ | -------------------------------- |-----------|--------|----|--- |
|email| 邮箱  | formData | true |string  |    |
|nickName| 昵称  | formData | true |string  |    |
|age| 年龄  | formData | false |integer  |    |

**响应示例**:

```json
{
	"userId": 0
}
```

**响应参数**:


| 参数名称         | 参数说明                             |    类型 |  schema |
| ------------ | -------------------|-------|----------- |
|userId| 用户id  |integer(int64)  | integer(int64)   |





**响应状态**:


| 状态码         | 说明                            |    schema                         |
| ------------ | -------------------------------- |---------------------- |
| 404 | 无法找到对应服务  ||
| default | 默认响应  |object|
## 查询用户列表

**接口描述**:获取用户列表

**接口地址**:`/apidoc/mock/user/simple/list.htm`


**请求方式**：`GET`


**consumes**:`["application/json"]`


**produces**:`["application/json"]`



**请求参数**：
暂无



**响应示例**:

```json

```

**响应参数**:


暂无





**响应状态**:


| 状态码         | 说明                            |    schema                         |
| ------------ | -------------------------------- |---------------------- |
| default | 默认响应  ||
## 查询用户分页

**接口描述**:获取用户分页

**接口地址**:`/apidoc/mock/user/simple/pager.htm`


**请求方式**：`GET`


**consumes**:`["application/json"]`


**produces**:`["application/json"]`



**请求参数**：

| 参数名称         | 参数说明     |     in |  是否必须      |  数据类型  |  schema  |
| ------------ | -------------------------------- |-----------|--------|----|--- |
|pageNumber|   | query | false |integer  |    |
|pageSize|   | query | false |integer  |    |

**响应示例**:

```json
{
	"totalCount": 0,
	"list": ""
}
```

**响应参数**:


| 参数名称         | 参数说明                             |    类型 |  schema |
| ------------ | -------------------|-------|----------- |
|totalCount| 总条数  |integer(int32)  | integer(int32)   |
|list| 分页数据  |array  | array   |





**响应状态**:


| 状态码         | 说明                            |    schema                         |
| ------------ | -------------------------------- |---------------------- |
| default | 默认响应  |object|
## 删除用户

**接口描述**:删除指定用户

**接口地址**:`/apidoc/mock/user/simple/{userId}/del.htm`


**请求方式**：`DELETE`


**consumes**:`["application/json"]`


**produces**:`["application/json"]`



**请求参数**：

| 参数名称         | 参数说明     |     in |  是否必须      |  数据类型  |  schema  |
| ------------ | -------------------------------- |-----------|--------|----|--- |
|userId| 用户id  | path | true |integer  |    |

**响应示例**:

```json
{
	"errorCode": "",
	"errorMsg": ""
}
```

**响应参数**:


| 参数名称         | 参数说明                             |    类型 |  schema |
| ------------ | -------------------|-------|----------- |
|errorCode| 错误编码  |string  |    |
|errorMsg| 错误提示信息  |string  |    |





**响应状态**:


| 状态码         | 说明                            |    schema                         |
| ------------ | -------------------------------- |---------------------- |
| default | 默认响应  |ErrorCode|
## 查询用户

**接口描述**:查询用户信息

**接口地址**:`/apidoc/mock/user/simple/{userId}/info.htm`


**请求方式**：`GET`


**consumes**:`["application/json"]`


**produces**:`["application/json"]`



**请求参数**：

| 参数名称         | 参数说明     |     in |  是否必须      |  数据类型  |  schema  |
| ------------ | -------------------------------- |-----------|--------|----|--- |
|userId| 用户id  | path | true |integer  |    |

**响应示例**:

```json
{
	"nickName": "",
	"email": "",
	"age": 0
}
```

**响应参数**:


| 参数名称         | 参数说明                             |    类型 |  schema |
| ------------ | -------------------|-------|----------- |
|nickName| 昵称  |string  |    |
|email| 邮箱  |string  |    |
|age| 年龄  |integer(int32)  | integer(int32)   |





**响应状态**:


| 状态码         | 说明                            |    schema                         |
| ------------ | -------------------------------- |---------------------- |
| default | 默认响应  |SimpleUserInfo|
## 更新用户

**接口描述**:更新用户信息

**接口地址**:`/apidoc/mock/user/simple/{userId}/update.htm`


**请求方式**：`POST`


**consumes**:`["application/json"]`


**produces**:`["application/json"]`



**请求参数**：

| 参数名称         | 参数说明     |     in |  是否必须      |  数据类型  |  schema  |
| ------------ | -------------------------------- |-----------|--------|----|--- |
|userId| 用户id  | path | true |integer  |    |
|nickName| 昵称  | query | true |string  |    |

**响应示例**:

```json
{
	"errorCode": "",
	"errorMsg": ""
}
```

**响应参数**:


| 参数名称         | 参数说明                             |    类型 |  schema |
| ------------ | -------------------|-------|----------- |
|errorCode| 错误编码  |string  |    |
|errorMsg| 错误提示信息  |string  |    |





**响应状态**:


| 状态码         | 说明                            |    schema                         |
| ------------ | -------------------------------- |---------------------- |
| default | 默认响应  |ErrorCode|
## 上传图片

**接口描述**:上传

**接口地址**:`/apidoc/mock/user/simple/{userId}/upload.htm`


**请求方式**：`POST`


**consumes**:`["application/json"]`


**produces**:`["application/json"]`



**请求参数**：

| 参数名称         | 参数说明     |     in |  是否必须      |  数据类型  |  schema  |
| ------------ | -------------------------------- |-----------|--------|----|--- |
|userId|   | path | true |integer  |    |
|img|   | formData | true |file  |    |

**响应示例**:

```json

```

**响应参数**:


暂无





**响应状态**:


暂无


# 进阶

## 新增用户(自定义参数)

**接口描述**:新增用户(自定义参数)

**接口地址**:`/apidoc/mock/user/complex/add.htm`


**请求方式**：`POST`


**consumes**:`["application/json"]`


**produces**:`["application/json"]`


**请求示例**：
```json
""
```


**请求参数**：

| 参数名称         | 参数说明     |     in |  是否必须      |  数据类型  |  schema  |
| ------------ | -------------------------------- |-----------|--------|----|--- |
|userInfo| 用户信息  | body | true |cust  |    |

**响应示例**:

```json
{
	"userId": 0
}
```

**响应参数**:


| 参数名称         | 参数说明                             |    类型 |  schema |
| ------------ | -------------------|-------|----------- |
|userId| 用户id  |integer(int64)  | integer(int64)   |





**响应状态**:


| 状态码         | 说明                            |    schema                         |
| ------------ | -------------------------------- |---------------------- |
| default | 默认响应  |object|
## 查询用户列表(多层嵌套)

**接口描述**:查询用户列表(多层嵌套)

**接口地址**:`/apidoc/mock/user/complex/list.htm`


**请求方式**：`GET`


**consumes**:`["application/json"]`


**produces**:`["application/json"]`



**请求参数**：
暂无



**响应示例**:

```json
{
	"data": {},
	"errorCode": ""
}
```

**响应参数**:


| 参数名称         | 参数说明                             |    类型 |  schema |
| ------------ | -------------------|-------|----------- |
|data| 响应信息  |object  |    |
|errorCode| 错误码  |string  |    |





**响应状态**:


| 状态码         | 说明                            |    schema                         |
| ------------ | -------------------------------- |---------------------- |
| default | 默认响应  |object|
## 获取用户详情(多层嵌套)

**接口描述**:获取用户详情(多层嵌套)

**接口地址**:`/apidoc/mock/user/complex/{userId}/info.htm`


**请求方式**：`GET`


**consumes**:`["application/json"]`


**produces**:`["application/json"]`



**请求参数**：

| 参数名称         | 参数说明     |     in |  是否必须      |  数据类型  |  schema  |
| ------------ | -------------------------------- |-----------|--------|----|--- |
|userId| 用户id  | path | true |integer  |    |

**001响应示例**:

```json
{
	"admin": ""
}
```

**001响应参数**:


| 参数名称         | 参数说明                             |    类型 |  schema |
| ------------ | -------------------|-------|----------- |
|admin| 管理员信息  |string  |    |







**default响应示例**:

```json
{
	"addressList": "",
	"nickName": "",
	"userId": 0,
	"age": 0
}
```

**default响应参数**:


| 参数名称         | 参数说明                             |    类型 |  schema |
| ------------ | -------------------|-------|----------- |
|addressList| 地址列表  |array  | array   |
|nickName| 昵称  |string  |    |
|userId| 用户id  |integer(int64)  | integer(int64)   |
|age| 年龄  |integer(int32)  | integer(int32)   |







**error响应示例**:

```json
{
	"errorCode": "",
	"errorMsg": ""
}
```

**error响应参数**:


| 参数名称         | 参数说明                             |    类型 |  schema |
| ------------ | -------------------|-------|----------- |
|errorCode| 错误编码  |string  |    |
|errorMsg| 错误提示信息  |string  |    |








**响应状态**:


| 状态码         | 说明                            |    schema                         |
| ------------ | -------------------------------- |---------------------- |
| 001 | 管理员  |object|
| default | 默认响应  |object|
| error | 错误信息  |ErrorCode|
