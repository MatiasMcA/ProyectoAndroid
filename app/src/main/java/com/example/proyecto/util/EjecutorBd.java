package com.example.proyecto.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class EjecutorBd {

    private static final ExecutorService EJECUTOR = Executors.newSingleThreadExecutor();
    private static final Handler HANDLER_PRINCIPAL = new Handler(Looper.getMainLooper());

    private EjecutorBd() {
    }

    public static void enSegundoPlano(Runnable tarea) {
        EJECUTOR.execute(tarea);
    }

    public static void enPrincipal(Runnable tarea) {
        HANDLER_PRINCIPAL.post(tarea);
    }
}
