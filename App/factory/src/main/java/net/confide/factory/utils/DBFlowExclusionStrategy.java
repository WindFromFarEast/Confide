package net.confide.factory.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

/**
 * DBFlow数据库过滤
 */
public class DBFlowExclusionStrategy implements ExclusionStrategy{

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        //被跳过的字段
        //只要是属于DBFlow数据
        return f.getDeclaredClass().equals(ModelAdapter.class);
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        //被跳过的Class
        return false;
    }
}
