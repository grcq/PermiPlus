package dev.grcq.permiplus.database;

import com.google.gson.*;
import dev.grcq.permiplus.utils.Credentials;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MySQL {

    private Connection connection;

    public MySQL() {

    }

    private boolean connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + Credentials.MySQL.ADDRESS + ":" + Credentials.MySQL.PORT + "/" + Credentials.MySQL.DATABASE, Credentials.MySQL.USERNAME, Credentials.MySQL.PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @SneakyThrows
    public void disconnect() {
        this.connection.close();
        this.connection = null;
    }

    @SneakyThrows
    public void update(@NotNull String query, @Nullable Map<Integer, Object> values) {
        if (!this.connect()) throw new RuntimeException("An error occurred attempting to execute query '" + query + "'");

        PreparedStatement ps = this.connection.prepareStatement(query);
        if (values != null) {
            for (Map.Entry<Integer, Object> entry : values.entrySet()) {
                int i = entry.getKey();
                Object o = entry.getValue();

                switch (o.getClass().getSimpleName()) {
                    case "String" -> ps.setString(i, (String) o);
                    case "Integer" -> ps.setInt(i, (int) o);
                    case "Float" -> ps.setFloat(i, (float) o);
                    case "Double" -> ps.setDouble(i, (double) o);
                    case "Long" -> ps.setLong(i, (long) o);
                    case "Date" -> ps.setDate(i, (Date) o);
                    case "Array" -> ps.setArray(i, (Array) o);
                    case "Boolean" -> ps.setBoolean(i, (boolean) o);
                }
            }
        }

        ps.executeUpdate();
        this.disconnect();
    }

    public void update(String query) {
        update(query, null);
    }

    @SneakyThrows
    public ResultSet execute(@NotNull String query, @NotNull Map<Integer, Object> values) {
        if (!this.connect()) throw new RuntimeException("An error occurred attempting to execute query '" + query + "'");

        PreparedStatement ps = this.connection.prepareStatement(query);
        for (Map.Entry<Integer, Object> entry : values.entrySet()) {
            int i = entry.getKey();
            Object o = entry.getValue();

            switch (o.getClass().getSimpleName()) {
                case "String" -> ps.setString(i, (String) o);
                case "Integer" -> ps.setInt(i, (int) o);
                case "Float" -> ps.setFloat(i, (float) o);
                case "Double" -> ps.setDouble(i, (double) o);
                case "Long" -> ps.setLong(i, (long) o);
                case "Date" -> ps.setDate(i, (Date) o);
                case "Array" -> ps.setArray(i, (Array) o);
                case "Boolean" -> ps.setBoolean(i, (boolean) o);
            }
        }

        return ps.executeQuery();
    }

    @SneakyThrows
    public JsonArray toJson(ResultSet rs) {
        JsonArray array = new JsonArray();

        while (rs.next()) {
            //array.add(el(rs, rs.getMetaData()));
            int col = rs.getMetaData().getColumnCount();
            JsonObject object = new JsonObject();
            for (int i = 1; i <= col; i++) {
                object.add(rs.getMetaData().getColumnLabel(i), fieldToJsonElement(rs, rs.getMetaData(), i));
            }

            array.add(object);
        }

        rs.close();

        return array;
    }

    private JsonElement fieldToJsonElement(final ResultSet resultSet, final ResultSetMetaData metaData, final int column)
            throws SQLException {
        final int columnType = metaData.getColumnType(column);
        final Optional<JsonElement> jsonElement = switch (columnType) {
            case Types.BIT, Types.SMALLINT, Types.NUMERIC, Types.DECIMAL, Types.CHAR, Types.OTHER, Types.JAVA_OBJECT, Types.DISTINCT, Types.STRUCT, Types.ARRAY, Types.BLOB, Types.CLOB, Types.REF, Types.DATALINK, Types.ROWID, Types.NCHAR, Types.NVARCHAR, Types.LONGNVARCHAR, Types.NCLOB, Types.SQLXML, Types.REF_CURSOR, Types.TIME_WITH_TIMEZONE, Types.TIMESTAMP_WITH_TIMEZONE ->
                    throw new UnsupportedOperationException("TODO: " + JDBCType.valueOf(columnType));
            case Types.TINYINT -> Optional.of(resultSet.getBoolean(column)).map(JsonPrimitive::new);
            case Types.INTEGER ->
                // resultSet.getInt() returns 0 in case of null, so it must be extracted with getObject and cast, then converted to a JsonPrimitive
                    Optional.of(resultSet.getInt(column)).map(JsonPrimitive::new);
            case Types.BIGINT -> Optional.of(resultSet.getLong(column)).map(JsonPrimitive::new);
            case Types.FLOAT -> Optional.of(resultSet.getFloat(column)).map(JsonPrimitive::new);
            case Types.DOUBLE -> Optional.of(resultSet.getDouble(column)).map(JsonPrimitive::new);
            case Types.VARCHAR -> Optional.ofNullable(resultSet.getString(column)).map(JsonPrimitive::new);
            case Types.LONGVARCHAR, Types.DATE ->
                    Optional.of(resultSet.getDate(column)).map(Date::toString).map(JsonPrimitive::new);
            case Types.TIME, Types.TIMESTAMP, Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY, Types.NULL ->
                    Optional.empty();
            case Types.BOOLEAN ->
                    Optional.of(resultSet.getBoolean(column)).map(JsonPrimitive::new);
            default -> throw new UnsupportedOperationException("Unknown type: " + columnType);
        };
        // Process each SQL type mapping a value to a JSON tree equivalent
        // If the optional value is missing, assume it's a null
        return jsonElement.orElse(JsonNull.INSTANCE);
    }

}
