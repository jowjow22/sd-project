package records;


import com.google.gson.internal.LinkedTreeMap;
import enums.Operations;
import helpers.singletons.Json;
import lombok.Getter;

public record Request<T>(Operations operation, String token, T data) {
    public Request(Operations operation, T data) {
        this(operation, null, data);
    }
    public Request(Operations operation, String token) {
        this(operation, token, (T) new Object());
    }
    public T data(Class<T> classOfT) {
        Json jsonParser = Json.getInstance();
        String toJson = jsonParser.toJson(data);
        return jsonParser.fromJson(toJson, classOfT);
    }
}
