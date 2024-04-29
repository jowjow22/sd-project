package records;

import enums.Operations;
import helpers.singletons.Json;
@SuppressWarnings("unchecked")
public record Request<T>(Operations operation, String token, T data) {
    public Request(Operations operation, T data) {
        this(operation, null, data);
    }
    public Request(Operations operation, String token) {
        this(operation, token, (T) new Object());
    }
    public <DT> Request<DT> withDataClass(Class<DT> dataClass) {
        String toJson = Json.getInstance().toJson(data);
        DT data = Json.getInstance().fromJson(toJson, dataClass);
        return new Request<DT>(operation, token, data);
    }
}
