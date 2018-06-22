package net.confide.factory.model.db;

import com.raizlabs.android.dbflow.structure.BaseModel;

import net.confide.factory.utils.DiffUiDataCallback;

/**
 * App中的基础BaseDbModel,继承自DbFlow中的BaseModel
 */
public abstract class BaseDbModel<Model> extends BaseModel implements DiffUiDataCallback.UiDataDiffer<Model>{


}
