package main.splitwise.server;

import main.splitwise.server.BasicUserInfo;

public class ClientContextHolder {
    public static ThreadLocal<BasicUserInfo> userInformation = new ThreadLocal();
}
