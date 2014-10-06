package com.letv.portal.python.service;

import java.util.Map;

import com.letv.portal.model.MclusterModel;


/**Program Name: IBuildTaskService <br>
 * Description:  mcluster db等创建调度<br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年9月14日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public interface IBuildTaskService { 
	
	/**Methods Name: buildMcluster <br>
	 * Description: 创建mcluster<br>
	 * @author name: liuhao1
	 * @param mclusterModel
	 */
	public void buildMcluster(MclusterModel mclusterModel,Long dbId);

	/**Methods Name: buildUser <br>
	 * Description: 创建用户<br>
	 * @author name: liuhao1
	 * @param ids
	 */
	public void buildUser(String ids);

	/**Methods Name: buildDb <br>
	 * Description: 创建数据库<br>
	 * @author name: liuhao1
	 * @param dbId
	 */
	public void buildDb(Long dbId);
	
	/**Methods Name: createContainer <br>
	 * Description: 创建container<br>
	 * @author name: liuhao1
	 * @param mclusterModel
	 * @return
	 */
	public boolean createContainer(MclusterModel mclusterModel,Long dbId);
	
	/**Methods Name: initContainer <br>
	 * Description: 初始化contianer,组合各分步骤<br>
	 * @author name: liuhao1
	 * @param params
	 * @return
	 */
	public boolean initContainer(MclusterModel mclusterModel,Long dbId);
	
	/**Methods Name: buildResultToMgr <br>
	 * Description: <br>
	 * @author name: liuhao1
	 * @param buildType
	 * @param result
	 * @param detail
	 */
	public void buildResultToMgr(String buildType,String result,String detail,String to);
	
	/**Methods Name: buildResultToUser <br>
	 * Description: <br>
	 * @author name: liuhao1
	 * @param buildType
	 */
	public void buildResultToUser(String buildType,String to);
}