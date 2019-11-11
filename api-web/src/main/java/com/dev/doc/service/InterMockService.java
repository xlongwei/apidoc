package com.dev.doc.service;

import java.util.List;

import com.dev.base.mybatis.service.BaseMybatisService;
import com.dev.doc.entity.InterMock;

public interface InterMockService extends BaseMybatisService<InterMock, Long> {

	List<InterMock> listAllByInterId(Long interId);
}
