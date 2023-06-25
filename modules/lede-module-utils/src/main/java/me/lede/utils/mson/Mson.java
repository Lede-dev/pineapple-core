package me.lede.utils.mson;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class Mson<T> {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private String path;
    private T data;
    private Class<?> clazz;

    public Mson(String path, Class<T> clazz) {
        this.path = path;
        this.data = null;
        this.clazz = clazz;
    }

    public Mson(String path, T data) {
        this.path = path;
        this.data = data;
        this.clazz = data.getClass();
    }

    public Mson<T> updateData(T data) {
        setData(data);
        return this;
    }

    public String toJson() {
        return gson.toJson(data);
    }

    public String getPath() {
        return path;
    }

    public T getData() {
        return data;
    }

    public void write() {
        BufferedWriter bufferedWriter = null;
        try {
            initFile();
            bufferedWriter = new BufferedWriter(new FileWriter(path));
            writeToJsonFile(bufferedWriter);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeBufferedWriter(bufferedWriter);
        }
    }

    public Mson<T> read() {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            readFromJsonFile(bufferedReader);
        } catch (FileNotFoundException e) {
            write();
        } catch (JsonIOException | JsonSyntaxException e) {
            e.printStackTrace();
        } finally {
            closeBufferedReader(bufferedReader);
        }
        return this;
    }

    private void writeToJsonFile(BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.write(toJson());
    }

    private void readFromJsonFile(BufferedReader bufferedReader) {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(bufferedReader);
        T readData = gson.fromJson(jsonElement, (Type) clazz);
        setData(readData);
    }

    private void closeBufferedWriter(BufferedWriter bufferedWriter) {
        try {
            if (bufferedWriter != null) {
                bufferedWriter.flush();
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeBufferedReader(BufferedReader bufferedReader) {
        try {
            if (bufferedReader != null)
                bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initFile() {
        File file = new File(path);
        if (!isFileExist(file))
            file.getParentFile().mkdirs();
    }

    private void setPath(String path) {
        this.path = path;
    }

    private void setData(T data) {
        this.data = data;
    }

    private void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    private boolean isFileExist(File file) {
        return file.getParentFile().exists();
    }

}
