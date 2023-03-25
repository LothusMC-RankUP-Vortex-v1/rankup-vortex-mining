package br.net.rankup.mining.utils;

import java.io.*;
import com.google.gson.*;
import java.util.*;
import java.lang.reflect.*;

public abstract class AbstractHandler<ID, T>
{
    private JsonDocument document;
    
    public AbstractHandler(final File parent, final String path, final Map<Class<?>, Object> adapters) {
        final GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        for (final Map.Entry<Class<?>, Object> entry : adapters.entrySet()) {
            builder.registerTypeAdapter((Type)entry.getKey(), entry.getValue());
        }
        builder.disableHtmlEscaping();
        try {
            this.document = JsonDocument.of(parent, path, builder.create());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public abstract Class<T> getType();
    
    public abstract Class<T[]> getArrayType();
    
    public T find(final ID id) {
        final JsonElement element = this.document.get(id.toString());
        if (element == null) {
            return null;
        }
        return (T)this.document.getGson().fromJson(element, (Class)this.getType());
    }
    
    public void set(final ID id, final T item) throws IOException {
        this.document.set(id.toString(), item);
        this.document.save();
    }
    
    public void delete(final ID id) throws IOException {
        this.document.set(id.toString(), null);
        this.document.save();
    }
    
    public T[] getAll() {
        final List<T> items = new ArrayList<T>();
        for (final Map.Entry<String, JsonElement> entry : this.document.getJson().entrySet()) {
            items.add(this.parseElement(entry.getValue()));
        }
        return items.toArray((T[])newArray(this.getArrayType()));
    }
    
    public T parseElement(final JsonElement data) {
        return (T)this.document.getGson().fromJson(data, (Class)this.getType());
    }
    
    public T parseString(final String data) {
        return (T)this.document.getGson().fromJson(data, (Class)this.getType());
    }
    
    public String parseItem(final T item) {
        return this.document.getGson().toJson((Object)item);
    }
    
    private static <T> T[] newArray(final Class<T[]> type) {
        return type.cast(Array.newInstance(type.getComponentType(), 0));
    }
    
    public JsonDocument getDocument() {
        return this.document;
    }
}
