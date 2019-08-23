package org.meveo.apiv2.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@JsonSerialize
public interface ApiException extends Resource{
    @Nullable
    Long getId();
    @Nullable
    String getCode();
    @Nullable
    String getStatus();
    @Nullable
    String getDetails();
    @Nullable
    List<Cause> getCauses();
}

