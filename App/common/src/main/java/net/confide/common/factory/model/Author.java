package net.confide.common.factory.model;


/**
 * 基础用户接口
 */
public interface Author {

    String getId();

    void setId(String id);

    String getName();

    void setName(String name);

    String getPhone();

    void setPhone(String phone);

    String getPortrait();

    void setPortrait(String portrait);
}
