package ru.mobnius.simple_core.data.socket;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.EngineIOException;
import io.socket.engineio.client.transports.WebSocket;
import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.data.credentials.BasicCredentials;
import ru.mobnius.simple_core.utils.StringUtil;
import ru.mobnius.simple_core.utils.UrlUtil;

/**
 * Создание websocket подключения к серверу
 * Подробнее читать тут
 * https://socket.io/blog/native-socket-io-and-android/
 * https://github.com/socketio/socket.io-client-java
 */
public class SocketManager {

    @Nullable
    private static SocketManager socketManager;
    @Nullable
    public Socket socket;

    /**
     * Подключение к сокету
     *
     * @param url         адресная строка подключения
     * @param credentials безопасность
     */
    private SocketManager(final @NonNull String url,
                          final @NonNull BasicCredentials credentials) {
        final String[] transports = new String[1];
        transports[0] = WebSocket.NAME;

        try {
            final IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.path = UrlUtil.getPathUrl(url) + "/socket.io";
            opts.query = "token=" + credentials.getToken();
            opts.transports = transports;

            socket = IO.socket(UrlUtil.getDomainUrl(url), opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * создает и возвращается текущий экземпляр подключения
     *
     * @param url         адресная строка подключения
     * @param credentials безопасность
     * @return Объект socket-подключения
     */
    public static void createInstance(final @NonNull String url,
                                      final @NonNull BasicCredentials credentials,
                                      final @NonNull SocketEventListener listeners) {
        socketManager = new SocketManager(url, credentials);
        socketManager.open(listeners);
    }

    /**
     * возвращается текущий экземпляр подключения
     *
     * @return Объект socket-подключения
     */
    @Nullable
    public static SocketManager getInstance() {
        return socketManager;
    }

    /**
     * Открытие подключения к серверу
     *
     * @param listeners обработчик уведомлений
     */
    public void open(final @NonNull SocketEventListener listeners) {
        if (socket == null) {
            return;
        }
        socket.on(Socket.EVENT_CONNECT, args -> listeners.onConnect());

        socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
           if (args == null || args.length == 0){
               return;
           }
           if(args[0] instanceof EngineIOException){
               final EngineIOException engineIOException = (EngineIOException) args[0];
               if (engineIOException.getCause() == null){
                   listeners.onConnectError(StringUtil.defaultEmptyString(engineIOException.getMessage()));
                   return;
               }
               listeners.onConnectError(StringUtil.defaultEmptyString(engineIOException.getCause().getMessage()));
               return;
           }
           listeners.onConnectError(BaseApp.ERROR_ENG);
        });

        socket.on(Socket.EVENT_DISCONNECT, args -> listeners.onDisconnect());

        socket.connect();
    }

    /**
     * зарегистрирован ли пользователь на сервере
     *
     * @return true - пользователь был зарегистрирован ранее
     */
    public boolean isRegistered() {
        if (socket != null)
            return socket.connected();
        return false;
    }

    /**
     * Закрытие подключения
     */
    public void close() {
        if (socket != null) {
            socket.off();
            socket.close();
        }
    }

    public void destroy() {
        close();
        socket = null;
        socketManager = null;
    }
}
