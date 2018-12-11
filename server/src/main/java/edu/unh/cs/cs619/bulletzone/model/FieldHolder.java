package edu.unh.cs.cs619.bulletzone.model;

import com.google.common.base.Optional;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class FieldHolder {

    private final Map<Direction, FieldHolder> neighbors = new HashMap<Direction, FieldHolder>();
    private Optional<FieldEntity> entityHolder = Optional.absent();
    protected int position;

    public FieldHolder(int pos){
        position = pos;
    }

    public void addNeighbor(Direction direction, FieldHolder fieldHolder) {
        neighbors.put(checkNotNull(direction), checkNotNull(fieldHolder));
    }

    public FieldHolder getNeighbor(Direction direction) {
        return neighbors.get(checkNotNull(direction,
                "Direction cannot be null."));
    }

    public boolean isPresent() {
        return entityHolder.isPresent();
    }

    public FieldEntity getEntity() {
        return entityHolder.get();
    }

    public void setFieldEntity(FieldEntity entity) {
        entityHolder = Optional.of(checkNotNull(entity,
                "FieldEntity cannot be null."));
        //this is so that we don't have to manually add parent
        entity.setParent(this);
    }

    public void clearField() {
        if (entityHolder.isPresent()) {
            entityHolder = Optional.absent();
        }
    }

}
