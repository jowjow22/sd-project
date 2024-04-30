package records;

import com.google.gson.internal.LinkedTreeMap;
import enums.Operations;
import enums.Statuses;
import helpers.singletons.Json;

public record Response<T>(Operations operation, Statuses status, T data) {
    public Response(Operations operation, Statuses status) {
        this(operation, status, (T) new Object());
    }

    public <DT> Response<DT> withDataClass(Class<DT> dataClass) {
        String toJson = Json.getInstance().toJson(data);
        DT data = Json.getInstance().fromJson(toJson, dataClass);
        return new Response<>(operation, status, data);
    }
}