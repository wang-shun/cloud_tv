package com.letv.portal.proxy;

import com.letv.portal.model.BackupResultModel;
import com.letv.portal.model.MclusterModel;

/**Program Name: IBackupProxy <br>
 * Description:  数据库db备份<br>
 * @author name: liuhao1 <br>
 * Written Date: 2015年1月4日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public interface IBackupProxy extends IBaseProxy<BackupResultModel> {

	/**Methods Name: backupTask <br>
	 * Description: 备份任务<br>
	 * @author name: liuhao1
	 */
	public void backupTask();
	
	/**Methods Name: wholeBackup4Db <br>
	 * Description: 数据库全量备份 <br>
	 * @author name: liuhao1
	 */
	public void wholeBackup4Db(MclusterModel mcluster);

	/**Methods Name: checkBackupStatus <br>
	 * Description: 定期检查备份结果 任务<br>
	 * @author name: liuhao1
	 */
	public void checkBackupStatusTask();
	
	/**Methods Name: checkBackupStatus <br>
	 * Description: 检查某备份结果<br>
	 * @author name: liuhao1
	 * @param result
	 */
	public void checkBackupStatus(BackupResultModel result);
	
}