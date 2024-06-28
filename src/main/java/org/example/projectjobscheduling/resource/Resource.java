package org.example.projectjobscheduling.resource;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import org.example.projectjobscheduling.AbstractPersistable;
import org.example.projectjobscheduling.JacksonUniqueIdGenerator;

@Getter
@Setter
@AllArgsConstructor
@JsonSubTypes({
        @JsonSubTypes.Type(value = GlobalResource.class, name = "global"),
        @JsonSubTypes.Type(value = LocalResource.class, name = "local"),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public abstract class Resource extends AbstractPersistable {

    /**
     * 容量
     */
    private int capacity;

    public Resource() {
    }

    public Resource(long id, int capacity) {
        super(id);
        this.capacity = capacity;
    }


    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public abstract boolean isRenewable();

}
