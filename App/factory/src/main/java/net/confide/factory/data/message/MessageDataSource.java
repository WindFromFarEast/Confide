package net.confide.factory.data.message;

import net.confide.common.factory.data.DbDataSource;
import net.confide.factory.model.db.Message;

/**
 * 消息的数据源接口定义,其实现是MessageRepository
 * 关注的对象是Message表
 */
public interface MessageDataSource extends DbDataSource<Message> {


}
