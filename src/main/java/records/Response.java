package records;

import com.google.gson.internal.LinkedTreeMap;
import enums.Operations;
import enums.Statuses;
import helpers.singletons.Json;

public record Response<T>(Operations operation, Statuses response, T data) {
    public T data(Class<T> classOfT) {
        Json jsonParser = Json.getInstance();
        String toJson = jsonParser.toJson(data);
        return jsonParser.fromJson(toJson, classOfT);
    }
}