package mpms.service.dblog;

import cn.hutool.db.Entity;
import mpms.model.log.MonitorNotifyLog;
import mpms.service.h2db.BaseDbCommonService;
import mpms.system.db.DbConfig;
import org.springframework.stereotype.Service;

/**
 * 监控消息
 *
 *
 
 */
@Service
public class DbMonitorNotifyLogService extends BaseDbCommonService<MonitorNotifyLog> {

	public DbMonitorNotifyLogService() {
		super(MonitorNotifyLog.TABLE_NAME, "logId", MonitorNotifyLog.class);
	}

	@Override
	public void insert(MonitorNotifyLog monitorNotifyLog) {
		super.insert(monitorNotifyLog);
		//
		DbConfig.autoClear(getTableName(), "createTime");
	}


	/**
	 * 修改执行结果
	 *
	 * @param logId    通知id
	 * @param status   状态
	 * @param errorMsg 错误消息
	 */
	public void updateStatus(String logId, boolean status, String errorMsg) {
		Entity entity = new Entity();
		entity.set("notifyStatus", status);
		if (errorMsg != null) {
			entity.set("notifyError", errorMsg);
		}

		Entity where = new Entity();
		where.set("logId", logId);
		super.update(entity, where);
	}
}
