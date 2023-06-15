package dev.grcq.permiplus.utils;

import dev.grcq.permiplus.PermiPlus;

public class Credentials {

    public static class MySQL {

        public static final String ADDRESS = PermiPlus.getInstance().getConfig().getString("mysql.address");
        public static final int PORT = PermiPlus.getInstance().getConfig().getInt("mysql.port");
        public static final String USERNAME = PermiPlus.getInstance().getConfig().getString("mysql.username");
        public static final String PASSWORD = PermiPlus.getInstance().getConfig().getString("mysql.password");
        public static final String DATABASE = PermiPlus.getInstance().getConfig().getString("mysql.database");

    }

}
