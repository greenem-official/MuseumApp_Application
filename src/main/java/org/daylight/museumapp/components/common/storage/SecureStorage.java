package org.daylight.museumapp.components.common.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.daylight.museumapp.dto.UserData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Optional;

public class SecureStorage {
    public enum StorageErrorType {
        FILE_NOT_FOUND,
        CORRUPTED_DATA,
        INVALID_FORMAT,
        ENCRYPTION_ERROR,
        ACCESS_DENIED,
        UNKNOWN
    }

    public record StorageResult<T>(T data, StorageErrorType error, Optional<Throwable> throwable) {
        public boolean isSuccess() { return error == null; }
        public static <T> StorageResult<T> success(T data) { return new StorageResult<>(data, null, Optional.empty()); }
        public static <T> StorageResult<T> error(StorageErrorType error, Throwable th) { return new StorageResult<>(null, error, th == null ? Optional.empty() : Optional.of(th)); }
    }

    private static final Path FILE = Paths.get(System.getProperty("user.home"), ".myapp", "userdata.sec");
    private static final ObjectMapper mapper = new ObjectMapper();

    public static StorageResult<Void> save(UserData data) {
        try {
            Files.createDirectories(FILE.getParent());

            String json = mapper.writeValueAsString(data);
            byte[] encrypted = CryptoUtils.encrypt(json.getBytes(StandardCharsets.UTF_8));

            Files.write(FILE, encrypted,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            return StorageResult.success(null);

        } catch (AccessDeniedException e) {
            return StorageResult.error(StorageErrorType.ACCESS_DENIED, e);
        } catch (JsonProcessingException e) {
            return StorageResult.error(StorageErrorType.INVALID_FORMAT, e);
        } catch (IOException e) {
            return StorageResult.error(StorageErrorType.UNKNOWN, e);
        } catch (Exception e) {
            return StorageResult.error(StorageErrorType.ENCRYPTION_ERROR, e);
        }
    }

    public static StorageResult<UserData> load() {
        if (!Files.exists(FILE)) {
            return StorageResult.error(StorageErrorType.FILE_NOT_FOUND, null);
        }

        try {
            byte[] encrypted = Files.readAllBytes(FILE);
            byte[] decrypted = CryptoUtils.decrypt(encrypted);

            UserData data = mapper.readValue(decrypted, UserData.class);

            return StorageResult.success(data);

        } catch (com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException e) {
            return StorageResult.error(StorageErrorType.INVALID_FORMAT, e);
        } catch (javax.crypto.AEADBadTagException e) {
            return StorageResult.error(StorageErrorType.CORRUPTED_DATA, e);
        } catch (IOException e) {
            return StorageResult.error(StorageErrorType.UNKNOWN, e);
        } catch (Exception e) {
            return StorageResult.error(StorageErrorType.ENCRYPTION_ERROR, e);
        }
    }

    public static StorageResult<Void> clear() {
        try {
            if (Files.exists(FILE)) {
                Files.delete(FILE);
            }
            return StorageResult.success(null);
        } catch (AccessDeniedException e) {
            return StorageResult.error(StorageErrorType.ACCESS_DENIED, e);
        } catch (IOException e) {
            return StorageResult.error(StorageErrorType.UNKNOWN, e);
        }
    }
}
