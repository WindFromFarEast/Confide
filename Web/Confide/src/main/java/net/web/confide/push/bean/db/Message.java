package net.web.confide.push.bean.db;

import net.web.confide.push.bean.api.message.MessageCreateModel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息的MODEL
 */
@Entity
@Table(name = "TB_MESSAGE")
public class Message {
    //发送给人的消息标志
    public static final int RECEIVER_TYPE_NONE = 1;
    //发送给群的消息标志
    public static final int RECEIVER_TYPE_GROUP = 2;

    //消息类型
    public static final int TYPE_STR = 1;//字符串类型
    public static final int TYPE_PIC = 2;//图片类型
    public static final int TYPE_FILE = 3;//文件类型
    public static final int TYPE_AUDIO = 4;//语音类型

    //主键
    @Id
    @PrimaryKeyJoinColumn
    //主键生成存储的类型为UUID
    //@GeneratedValue(generator = "uuid"),不自动生成UUID，由客户端负责生成
    //把uuid的生成器定义为uuid2, uuid2在hibernate中是常规的UUID toString
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    //id为列,不允许更改,不允许为空
    @Column(updatable = false, nullable = false)
    private String id;

    //消息内容
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    //附件
    @Column
    private String attach;

    //消息类型
    @Column(nullable = false)
    private int type;

    //创建时间,在创建时自动写入数据库
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    //修改时间,在修改时自动写入数据库
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    //消息发送者
    @JoinColumn(name = "senderId")
    @ManyToOne(optional = false)
    private User sender;

    @Column(updatable = false, insertable = false, nullable = false)
    private String senderId;

    //消息接收者
    @JoinColumn(name = "receiverId")
    @ManyToOne
    private User receiver;

    @Column(updatable = false, insertable = false)
    private String receiverId;

    //一个群可接收多个消息
    @JoinColumn(name = "groupId")
    @ManyToOne
    private Group group;

    @Column(updatable = false, insertable = false)
    private String groupId;

    public Message() {

    }

    /**
     * 向好友发送的消息构造方法
     * @param sender
     * @param receiver
     * @param model
     */
    public Message(User sender, User receiver, MessageCreateModel model) {
        this.id = model.getId();
        this.content = model.getContent();
        this.attach = model.getAttach();
        this.type = model.getType();

        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * 向群发送的消息构造方法
     * @param sender
     * @param group
     * @param model
     */
    public Message(User sender, Group group, MessageCreateModel model) {
        this.id = model.getId();
        this.content = model.getContent();
        this.attach = model.getAttach();
        this.type = model.getType();

        this.sender = sender;
        this.group = group;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
