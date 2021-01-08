### knife4j-front 配置说明

获取 knife4j-front 前端代码，使用 knife4j-front 目录即可，并用此目录的 doc.html、cdao/swaggerbootstrapui.js 覆盖 knife4j-front 目录下的同名文件

	git clone https://gitee.com/xiaoym/knife4j.git
	git checkout 24b2ce6a6bc16162836820597369838a28aa65dc #apidoc使用的是2019-11-01时的版本，git log --before="2019-12-01" knife4j-front/
	location /swagger/ {  #xlongwei.conf，https://api.xlongwei.com/swagger/，静态资源
		alias D:/OpenSource/xiaoym/knife4j.git/knife4j-front/;
	}

示例1，[swagger](https://api.xlongwei.com/swagger/)，从 [light4j](https://gitee.com/xlongwei/light4j/) 的 knife4j 目录读取文件，doc.html 默认读取[group.json](https://api.xlongwei.com/swagger/json/group.json)，[swagger.json](https://api.xlongwei.com/swagger/json/swagger.json)

	https://api.xlongwei.com/swagger/ #swagger
	var url = 'json/group.json'  #doc.html，相对路径
	https://api.xlongwei.com/swagger/json/group.json #group.json
	https://api.xlongwei.com/swagger/json/swagger.json #swagger.json
	location /swagger/json/ {  #xlongwei.conf，从文件目录获取group.json、swagger.json，以后维护这两个静态文件即可
		alias H:/works/itecheast/light4j/knife4j/;
	}

示例2，[demo](https://api.xlongwei.com/swagger/doc.html?doc=2)，[mock](https://api.xlongwei.com/swagger/doc.html?doc=1&mock=true)，从 [apidoc](https://api.xlongwei.com/apidoc/) 获取动态配置，knife4j-front可以动态获取[group.json](https://api.xlongwei.com/apidoc/pass/knife4j/group.htm?docId=2)，[swagger.json](https://api.xlongwei.com/swagger/apidoc/pass/knife4j/swagger.htm?docId=2)，doc.html 会处理参数 doc=2&mock=true

	/apidoc/pass/knife4j/group.htm?docId=2&mock=true  #doc.html，绝对路径
	https://api.xlongwei.com/apidoc/pass/knife4j/group.htm?docId=2 #group.json
	https://api.xlongwei.com/swagger/apidoc/pass/knife4j/swagger.htm?docId=2 #swagger.json
	location /apidoc/ {  #xlongwei.com，转发请求至apidoc应用，以后从apidoc维护接口文档即可
		proxy_pass http://localhost:8080;
	}

钉钉交流群：[21975907](https://s.xlongwei.com/uploads/img/avatar/dingding-group.jpg)，[接口服务](https://api.xlongwei.com/)
