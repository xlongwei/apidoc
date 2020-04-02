package com.dev.doc.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dev.base.mybatis.service.impl.BaseMybatisServiceImpl;
import com.dev.doc.dao.InterCaseDao;
import com.dev.doc.entity.InterCase;
import com.dev.doc.service.InterCaseService;

@Service
public class InterCaseServiceImpl extends BaseMybatisServiceImpl<InterCase, Long, InterCaseDao>
		implements InterCaseService {

	@Override
	public List<InterCase> listAllByInterId(Long interId) {
		return getMybatisDao().listAllByInterId(interId);
	}

}
