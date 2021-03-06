/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.portal.dao.elasticcalc.gce;

import java.util.Map;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.model.elasticcalc.gce.EcGce;

/**
 * IEcGceDao
 * @author linzhanbo .
 * @since 2016年6月28日, 下午2:24:30 .
 * @version 1.0 .
 */
public interface IEcGceDao extends IBaseDao<EcGce> {
	public Integer selectBySelectiveCount(Map<String,Object> exParams);
}
