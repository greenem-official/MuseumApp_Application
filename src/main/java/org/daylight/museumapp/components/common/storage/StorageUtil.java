package org.daylight.museumapp.components.common.storage;

import org.daylight.museumapp.components.common.GlobalHooks;
import org.daylight.museumapp.dto.UserData;
import org.daylight.museumapp.services.AuthService;
import org.daylight.museumapp.services.NotificationService;

public class StorageUtil {
    public static void onStartup() {
        SecureStorage.StorageResult<UserData> result = SecureStorage.load();

        if (!result.isSuccess()) {
            handleResultErrors(result.error(), result.throwable().orElse(null));
        } else {
            UserData userData = result.data();
            AuthService.getInstance().setCurrentUser(userData);

            GlobalHooks.getInstance().sidebarAccountButtonChangeHook.run();
            GlobalHooks.getInstance().sidebarOnAuthStateChange.run();
        }
    }

    public static void onSave() {
        UserData userData = AuthService.getInstance().getCurrentUser();
        SecureStorage.StorageResult<Void> result = null;
        if (userData != null) result = SecureStorage.save(userData);
        else result = SecureStorage.clear();

        if (!result.isSuccess()) {
            handleResultErrors(result.error(), result.throwable().orElse(null));
        }
    }

    private static void handleResultErrors(SecureStorage.StorageErrorType error, Throwable throwable) {
        switch (error) {
//                case FILE_NOT_FOUND -> NotificationService.getInstance().warning("Пользователь не залогинен");
            case CORRUPTED_DATA -> NotificationService.getInstance().warning("Файл сессии повреждён");
            case INVALID_FORMAT -> NotificationService.getInstance().warning("Данные сессии устарели");
            case ACCESS_DENIED -> NotificationService.getInstance().warning("Нет доступа к предыдущей сессии");
            case ENCRYPTION_ERROR -> NotificationService.getInstance().warning("Ошибка шифрования");
            case UNKNOWN -> NotificationService.getInstance().warning("Неизвестная ошибка восстановления сессии");
        }
        if(throwable != null) throwable.printStackTrace();
    }
}
