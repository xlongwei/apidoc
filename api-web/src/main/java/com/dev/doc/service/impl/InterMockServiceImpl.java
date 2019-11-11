package com.dev.doc.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dev.base.mybatis.service.impl.BaseMybatisServiceImpl;
import com.dev.doc.dao.InterMockDao;
import com.dev.doc.entity.InterMock;
import com.dev.doc.service.InterMockService;

@Service
public class InterMockServiceImpl extends BaseMybatisServiceImpl<InterMock, Long, InterMockDao>
		implements InterMockService {

	@Override
	public List<InterMock> listAllByInterId(Long interId) {
		return getMybatisDao().listAllByInterId(interId);
	}

}
