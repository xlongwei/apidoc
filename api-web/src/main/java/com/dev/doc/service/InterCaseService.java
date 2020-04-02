package com.dev.doc.service;

import java.util.List;

import com.dev.base.mybatis.service.BaseMybatisService;
import com.dev.doc.entity.InterCase;

public interface InterCaseService extends BaseMybatisService<InterCase, Long> {

	List<InterCase> listAllByInterId(Long interId);
}
