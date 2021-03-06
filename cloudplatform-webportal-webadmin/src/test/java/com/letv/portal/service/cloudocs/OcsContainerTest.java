package com.letv.portal.service.cloudocs;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.portal.controller.cloudocs.OcsContainerController;
import com.letv.portal.junitBase.AbstractTest;
 
public class OcsContainerTest extends AbstractTest{

	@Autowired
	private OcsContainerController ocsContainerController;
    
    @Test
    public void testGceContainerList() {
    	MockHttpServletRequest request =  new MockHttpServletRequest();
    	request.setAttribute("currentPage", 1);
    	request.setAttribute("recordsPerPage", 15);
    	ResultObject ro = ocsContainerController.list(new Page(), request, new ResultObject());
    	Assert.assertEquals(78, ((Page)ro.getData()).getTotalRecords());
    }
    
    @Test
    public void testGceContainerByClusterId() {
    	ResultObject ro = ocsContainerController.list(50l, new ResultObject());
    	Assert.assertEquals(3, ((List)ro.getData()).size());
    }
    
}
