package br.net.rankup.mining.utils;

import java.util.*;
import com.google.gson.*;
import java.io.*;

public class JsonDocument
{
    private final File file;
    private final Gson gson;
    private final Map<String, Object> defaultValues;
    private JsonObject json;
    
    public static JsonDocument of(final File parent, final String name, final Gson gson) throws IOException {
        return new JsonDocument(parent, name, gson);
    }
    
    public static JsonDocument of(final File parent, final String name) throws IOException {
        return of(parent, name, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create());
    }
    
    public static JsonDocument safeOf(final File parent, final String name, final Gson gson) {
        try {
            return of(parent, name, gson);
        }
        catch (IOException e) {
            return null;
        }
    }
    
    public static JsonDocument safeOf(final File parent, final String name) {
        try {
            return of(parent, name);
        }
        catch (IOException e) {
            return null;
        }
    }
    
    private JsonDocument(final File parent, final String name, final Gson gson) throws IOException {
        this.defaultValues = new WeakHashMap<String, Object>();
        if (!parent.exists() && !parent.mkdir()) {
            throw new RuntimeException("Could not create parent directory");
        }
        this.file = new File(parent.getAbsolutePath() + File.separator + name);
        if (!this.file.exists() && !this.file.createNewFile()) {
            throw new RuntimeException("Could not create file");
        }
        this.gson = gson;
        final FileReader fileReader = new FileReader(this.file);
        final JsonElement jsonElement = new JsonParser().parse((Reader)fileReader);
        fileReader.close();
        if (jsonElement instanceof JsonNull) {
            this.json = new JsonObject();
        }
        else {
            this.json = jsonElement.getAsJsonObject();
        }
    }
    
    public void reload() throws IOException {
        if (!this.file.exists() && !this.file.createNewFile()) {
            throw new RuntimeException("Could not create file");
        }
        final FileReader fileReader = new FileReader(this.file);
        final JsonElement jsonElement = new JsonParser().parse((Reader)fileReader);
        fileReader.close();
        if (jsonElement instanceof JsonNull) {
            this.json = new JsonObject();
        }
        else {
            this.json = jsonElement.getAsJsonObject();
        }
    }
    
    public void loadDefaultValues() throws IOException {
        this.defaultValues.forEach((key, value) -> {
            if (!this.contains(key)) {
                this.set(key, value);
            }
            return;
        });
        this.save();
    }
    
    public void loadDefaultValue(final String name) throws IOException {
        final Object value = this.defaultValues.get(name);
        if (value == null) {
            return;
        }
        if (this.contains(name)) {
            return;
        }
        this.set(name, value);
        this.save();
    }
    
    public JsonDocument addDefaultValue(final String key, final Object value) {
        this.defaultValues.put(key, value);
        return this;
    }
    
    public boolean contains(final String key) {
        if (!key.contains(".")) {
            return this.json.has(key);
        }
        final String[] path = key.split("\\.");
        JsonElement currentElement = (JsonElement)this.json;
        for (final String subPath : path) {
            if (!currentElement.isJsonObject()) {
                return false;
            }
            if (!currentElement.getAsJsonObject().has(subPath)) {
                return false;
            }
            currentElement = currentElement.getAsJsonObject().get(subPath);
        }
        return true;
    }
    
    public JsonElement get(final String key) {
        if (!this.contains(key)) {
            return (JsonElement)JsonNull.INSTANCE;
        }
        if (!key.contains(".")) {
            return this.json.get(key);
        }
        final String[] path = key.split("\\.");
        JsonElement currentElement = (JsonElement)this.json;
        for (final String subPath : path) {
            if (!currentElement.isJsonObject()) {
                return (JsonElement)JsonNull.INSTANCE;
            }
            if (!currentElement.getAsJsonObject().has(subPath)) {
                return (JsonElement)JsonNull.INSTANCE;
            }
            currentElement = currentElement.getAsJsonObject().get(subPath);
        }
        return currentElement;
    }
    
    public JsonDocument set(final String key, final Object value) {
        if (!key.contains(".")) {
            this.set(this.json, key, value);
            return this;
        }
        final String[] path = key.split("\\.");
        JsonObject currentElement = this.json;
        for (int i = 0; i < path.length; ++i) {
            final String subPath = path[i];
            if (i == path.length - 1) {
                this.set(currentElement, subPath, value);
                break;
            }
            if (!currentElement.has(subPath)) {
                currentElement.add(subPath, (JsonElement)new JsonObject());
                currentElement = currentElement.get(subPath).getAsJsonObject();
            }
            else {
                currentElement = currentElement.get(subPath).getAsJsonObject();
            }
        }
        return this;
    }
    
    private boolean set(final JsonObject jsonObject, final String key, final Object value) {
        if (value == null) {
            jsonObject.remove(key);
        }
        else {
            jsonObject.add(key, this.gson.toJsonTree(value));
        }
        return true;
    }
    
    public void save() throws IOException {
        final FileWriter fileWriter = new FileWriter(this.file);
        this.gson.toJson((JsonElement)this.json, (Appendable)fileWriter);
        fileWriter.flush();
        fileWriter.close();
    }
    
    public <T> T from(final String path, final Class<T> type) {
        return (T)this.gson.fromJson(this.get(path), (Class)type);
    }
    
    public File getFile() {
        return this.file;
    }
    
    public JsonObject getJson() {
        return this.json;
    }
    
    public Gson getGson() {
        return this.gson;
    }
    
    public Map<String, Object> getDefaultValues() {
        return this.defaultValues;
    }
}
