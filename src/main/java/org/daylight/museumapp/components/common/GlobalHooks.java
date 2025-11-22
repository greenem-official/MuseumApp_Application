package org.daylight.museumapp.components.common;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
public class GlobalHooks {
    private static GlobalHooks instance;

    public static GlobalHooks getInstance() {
        if (instance == null) {
            instance = new GlobalHooks();
        }
        return instance;
    }

    public Runnable sidebarAccountButtonChangeHook;
    public Runnable sidebarOnAuthStateChange;
}
