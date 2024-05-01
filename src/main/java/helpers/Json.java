package helpers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Json {
    private static Json instance = null;
    private final Gson gson = new GsonBuilder().create();
    private Json() {
    }

    public static Json getInstance() {
        if (instance == null) {
            instance = new Json();
        }
        return instance;
    }

    public String toJson(Object object) {
        return gson.toJson(object);
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

}
