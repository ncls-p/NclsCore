package fr.nclsp.database;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class DBModeration extends DatabaseConnect {
    private final String tablePrefix;

    public DBModeration(String host, String port, String username, String password, String databaseName,
            String tablePrefix) {
        super(host, port, username, password, databaseName);
        this.tablePrefix = tablePrefix;
    }

    public boolean isMuted(Statement statementMod, UUID targetUUID) throws SQLException {
        ResultSet rs = statementMod
                .executeQuery("SELECT * FROM " + this.tablePrefix + "MUTE WHERE playerUUID = '" + targetUUID
                        + "' and dateTimeEnd > NOW() or playerUUID ='" + targetUUID + "' and dateTimeEnd is null;");
        int i = 0;
        while (rs.next()) {
            i++;
        }
        if (i >= 1)
            return true;
        else
            return false;
    }

    public boolean isBanned(Statement statementMod, UUID targetUUID) throws SQLException {
        ResultSet rs = statementMod
                .executeQuery("SELECT * FROM " + this.tablePrefix + "BAN WHERE playerUUID = '" + targetUUID
                        + "' and dateTimeEnd > NOW() or playerUUID ='" + targetUUID + "' and dateTimeEnd is null;");
        int i = 0;
        while (rs.next()) {
            i++;
        }
        if (i >= 1)
            return true;
        else
            return false;
    }

    public void unbanActual(Statement statementMod, UUID targetUUID) throws SQLException {
        statementMod.executeUpdate("DELETE from " + this.tablePrefix + "BAN WHERE playerUUID ='" + targetUUID
                + "' and dateTimeEnd > NOW() or playerUUID ='" + targetUUID + "' and dateTimeEnd is null;");
    }

    public void mutedActual(Statement statementMod, UUID targetUUID) throws SQLException {
        statementMod.executeUpdate("DELETE from " + this.tablePrefix + "MUTE WHERE playerUUID ='" + targetUUID
                + "' and dateTimeEnd > NOW() or playerUUID ='" + targetUUID + "' and dateTimeEnd is null;");
    }

    public String getReasonBan(Player p, Statement statement) throws SQLException {
        String reason = null;
        ResultSet resultBanPerm = statement
                .executeQuery("SELECT reason FROM " + this.tablePrefix + "BAN WHERE playerUUID='"
                        + p.getUniqueId().toString() + "' AND dateTimeEnd is null ORDER BY dateTimeStart;");
        while (resultBanPerm.next()) {
            reason = resultBanPerm.getString("reason");
            if (reason == null)
                return reason;
        }
        ResultSet reasonBanNotPerm = statement.executeQuery("SELECT reason FROM " + this.tablePrefix
                + "BAN WHERE playerUUID='" + p.getUniqueId().toString() + "' ORDER BY dateTimeEnd asc;");
        while (reasonBanNotPerm.next()) {
            reason = reasonBanNotPerm.getString("reason");
            return reason;
        }
        return null;
    }

    public String getReasonMute(Player p, Statement statement) throws SQLException {
        String reason = null;
        ResultSet resultMutePerm = statement
                .executeQuery("SELECT reason FROM " + this.tablePrefix + "MUTE WHERE playerUUID='"
                        + p.getUniqueId().toString() + "' AND dateTimeEnd is null ORDER BY dateTimeStart;");
        while (resultMutePerm.next()) {
            reason = resultMutePerm.getString("reason");
            if (reason == null)
                return reason;
        }
        ResultSet reasonMuteNotPerm = statement.executeQuery("SELECT reason FROM " + this.tablePrefix
                + "MUTE WHERE playerUUID='" + p.getUniqueId().toString() + "' ORDER BY dateTimeEnd asc;");
        while (reasonMuteNotPerm.next()) {
            reason = reasonMuteNotPerm.getString("reason");
            return reason;
        }
        return null;
    }

    public void insertKickDatabase(Statement statement, String playerName, String playerUUID, String reason,
            String staffName, String staffUUID) throws SQLException {
        LocalDateTime now = LocalDateTime.now(); // Insert kick into the database
        statement.executeUpdate("INSERT INTO " + this.tablePrefix
                + "KICK (playerUUID, playerName, reason, dateTime, staffUUID, staffName)" +
                "VALUES ('" + playerUUID + "', '" + playerName + "', '" + reason + "', '" + now + "', '" + staffUUID
                + "', '" + staffName + "');");
    }

    public void insertBanDatabase(Statement statement, String playerName, String playerUUID, String reason,
            String staffName, String staffUUID, int time) throws SQLException {
        LocalDateTime now = LocalDateTime.now(); // Insert kick into the database
        if (time != 0) { // If time has been specified we ban normally
            LocalDateTime end = now.plusSeconds(time);
            statement.executeUpdate("INSERT INTO " + this.tablePrefix
                    + "BAN (playerUUID, playerName, reason, dateTimeStart, dateTimeEnd, staffUUID, staffName, time)" +
                    "VALUES ('" + playerUUID + "', '" + playerName + "', '" + reason.replace("'", " ") + "', '" + now
                    + "', '" + end + "', '" + staffUUID + "', '" + staffName + "', " + time + ");");
        } else { // if time wasn't specified we ban permanent, DateTimeEnd will be null in the
                 // database
            statement.executeUpdate("INSERT INTO " + this.tablePrefix
                    + "BAN (playerUUID, playerName, reason, dateTimeStart, dateTimeEnd, staffUUID, staffName, time)" +
                    "VALUES ('" + playerUUID + "', '" + playerName + "', '" + reason.replace("'", " ") + "', '" + now
                    + "', " + null + ", '" + staffUUID + "', '" + staffName + "', " + time + ");");
        }
    }

    public void insertMuteDatabase(Statement statement, String targetName, String playerUUID, String reason,
            String staffName, String staffUUID, int time) throws SQLException {
        LocalDateTime now = LocalDateTime.now(); // Insert kick into the database
        if (time != 0) { // If time has been specified we ban normally
            LocalDateTime end = now.plusSeconds(time);
            statement.executeUpdate("INSERT INTO " + this.tablePrefix
                    + "MUTE (playerUUID, playerName, reason, dateTimeStart, dateTimeEnd, staffUUID, staffName, time)" +
                    "VALUES ('" + playerUUID + "', '" + targetName + "', '" + reason.replace("'", " ") + "', '" + now
                    + "', '" + end + "', '" + staffUUID + "', '" + staffName + "', " + time + ");");
        } else { // if time wasn't specified we ban permanent, DateTimeEnd will be null in the
                 // database
            statement.executeUpdate("INSERT INTO " + this.tablePrefix
                    + "MUTE (playerUUID, playerName, reason, dateTimeStart, dateTimeEnd, staffUUID, staffName, time)" +
                    "VALUES ('" + playerUUID + "', '" + targetName + "', '" + reason.replace("'", " ") + "', '" + now
                    + "', " + null + ", '" + staffUUID + "', '" + staffName + "', " + time + ");");
        }
    }

    public ArrayList<UUID> getAllUUIDBan(Statement statementMod) throws SQLException {
        ArrayList<UUID> uuidList = new ArrayList<UUID>();
        ResultSet result = statementMod.executeQuery("SELECT playerUUID FROM " + this.tablePrefix + "BAN");
        if (result.next()) {
            while (result.next()) {
                String uuidAsString = result.getString("playerUUID");
                uuidList.add(UUID.fromString(uuidAsString));
            }
            return uuidList;
        }
        return uuidList;
    }

    public ArrayList<UUID> getAllUUIDMute(Statement statementMod) throws SQLException {
        ArrayList<UUID> uuidList = new ArrayList<UUID>();
        ResultSet result = statementMod.executeQuery("SELECT playerUUID FROM " + this.tablePrefix + "MUTE");
        if (result.next()) {
            while (result.next()) {
                String uuidAsString = result.getString("playerUUID");
                uuidList.add(UUID.fromString(uuidAsString));
            }
            return uuidList;
        }
        return uuidList;
    }

    public LocalDateTime getEndBan(Statement statementMod, String playerUUID) throws SQLException {
        ResultSet resultBanPerm = statementMod.executeQuery("SELECT dateTimeEnd FROM " + this.tablePrefix
                + "BAN WHERE playerUUID='" + playerUUID + "' AND dateTimeEnd is null ORDER BY dateTimeStart asc;");
        if (resultBanPerm.next()) {
            do {
                return null;
            } while (resultBanPerm.next());
        }
        ResultSet resultBanNotPerm = statementMod.executeQuery("SELECT MAX(dateTimeEnd) as dateTime FROM "
                + this.tablePrefix + "BAN WHERE playerUUID='" + playerUUID + "';");
        if (resultBanNotPerm.next()) {
            do {
                Timestamp ts1 = resultBanNotPerm.getTimestamp("dateTime");
                return ts1.toLocalDateTime();
            } while (resultBanNotPerm.next());
        }
        return null;
    }

    public LocalDateTime getEndMute(Statement statementMod, String playerUUID) throws SQLException {
        ResultSet resultBanPerm = statementMod.executeQuery("SELECT dateTimeEnd FROM " + this.tablePrefix
                + "MUTE WHERE playerUUID='" + playerUUID + "' AND dateTimeEnd is null ORDER BY dateTimeStart asc;");
        if (resultBanPerm.next()) {
            do {
                return null;
            } while (resultBanPerm.next());
        }
        ResultSet resultBanNotPerm = statementMod.executeQuery("SELECT MAX(dateTimeEnd) as dateTime FROM "
                + this.tablePrefix + "MUTE WHERE playerUUID='" + playerUUID + "';");
        if (resultBanNotPerm.next()) {
            do {
                Timestamp ts1 = resultBanNotPerm.getTimestamp("dateTime");
                return ts1.toLocalDateTime();
            } while (resultBanNotPerm.next());
        }
        return null;
    }

    public void defaultBan(Statement statementMod) throws SQLException {
        ArrayList<String> defaultName = new ArrayList<String>();
        ResultSet resultSet = statementMod
                .executeQuery("SELECT playerName FROM " + tablePrefix + "BAN WHERE playerName='§'");
        if (resultSet.next()) {
            do {
                defaultName.add(resultSet.getString("playerName"));
            } while (resultSet.next());
        }
        if (defaultName.isEmpty()) {
            String sql = "INSERT INTO " + this.tablePrefix
                    + "BAN (playerUUID, playerName, reason, dateTimeStart, dateTimeEnd, " +
                    "staffUUID, staffName, time) VALUES ('xxxxxxxxxxxxxxxxx', '§', 'This row will always come back to prevents bugs, no need to delete it', "
                    +
                    "'2017-01-01 00:00:00', NULL, 'xxxxxxxxxxxxxxxx', 'Fazdix', '0')";
            statementMod.executeUpdate(sql);
        }
    }

    public void defaulMute(Statement statementMod) throws SQLException {
        ArrayList<String> defaultName = new ArrayList<String>();
        ResultSet resultSet = statementMod
                .executeQuery("SELECT playerName FROM " + tablePrefix + "MUTE WHERE playerName='§'");
        if (resultSet.next()) {
            do {
                defaultName.add(resultSet.getString("playerName"));
            } while (resultSet.next());
        }
        if (defaultName.isEmpty()) {
            String sql = "INSERT INTO " + this.tablePrefix
                    + "MUTE (playerUUID, playerName, reason, dateTimeStart, dateTimeEnd, " +
                    "staffUUID, staffName, time) VALUES ('xxxxxxxxxxxxxxxxx', '§', 'This row will always come back to prevents bugs, no need to delete it', "
                    +
                    "'2017-01-01 00:00:00', NULL, 'xxxxxxxxxxxxxxxx', 'Fazdix', '0')";
            statementMod.executeUpdate(sql);
        }
    }

    public void defaulKick(Statement statementMod) throws SQLException {
        ArrayList<String> defaultName = new ArrayList<String>();
        ResultSet resultSet = statementMod
                .executeQuery("SELECT playerName FROM " + tablePrefix + "KICK WHERE playerName='§'");
        if (resultSet.next()) {
            do {
                defaultName.add(resultSet.getString("playerName"));
            } while (resultSet.next());
        }
        if (defaultName.isEmpty()) {
            String sql = "INSERT INTO " + this.tablePrefix
                    + "KICK (playerUUID, playerName, reason, dateTime, staffUUID, staffName) VALUES ('xxxxxxxxxxxxxxxxx', "
                    +
                    "'§', 'This row will always come back to prevents bugs, no need to delete it', '2017-01-01 00:00:00', 'xxxxxxxxxxxxxxxx', 'Fazdix')";
            statementMod.executeUpdate(sql);
        }
    }

    public void createDatabase(Statement statement) throws SQLException {
        String sql;

        sql = "CREATE TABLE IF NOT EXISTS " + this.tablePrefix + "MUTE(" +
                "        playerUUID    Varchar (50) NOT NULL ," +
                "        playerName    Varchar (50) NOT NULL ," +
                "        reason        Varchar (10000) NOT NULL ," +
                "        dateTimeStart Datetime NOT NULL ," +
                "        dateTimeEnd   Datetime NULL ," +
                "        staffUUID     Varchar (50) NOT NULL ," +
                "        staffName     Varchar (50) NOT NULL ," +
                "        time          Int NOT NULL" +
                ")ENGINE=InnoDB;";

        statement.executeUpdate(sql); // TABLE MUTE
        Bukkit.getConsoleSender()
                .sendMessage("§aTable §6" + this.tablePrefix + "MUTE §acreated or initialized: §6success");

        sql = "CREATE TABLE IF NOT EXISTS " + this.tablePrefix + "BAN(" +
                "        playerUUID    Varchar (50) NOT NULL ," +
                "        playerName    Varchar (50) NOT NULL ," +
                "        reason        Varchar (10000) NOT NULL ," +
                "        dateTimeStart Datetime NOT NULL ," +
                "        dateTimeEnd   Datetime NULL ," +
                "        staffUUID     Varchar (50) NOT NULL ," +
                "        staffName     Varchar (50) NOT NULL ," +
                "        time          Int NOT NULL" +
                ")ENGINE=InnoDB;";
        statement.executeUpdate(sql); // TABLE BAN
        Bukkit.getConsoleSender()
                .sendMessage("§aTable §6" + this.tablePrefix + "BAN §acreated or initialized: §6success");

        sql = "CREATE TABLE IF NOT EXISTS " + this.tablePrefix + "KICK(" +
                "        playerUUID Varchar (50) NOT NULL ," +
                "        playerName Varchar (50) NOT NULL ," +
                "        reason     Varchar (10000) NOT NULL ," +
                "        dateTime   Datetime NOT NULL ," +
                "        staffUUID  Varchar (50) NOT NULL ," +
                "        staffName  Varchar (50) NOT NULL" +
                ")ENGINE=InnoDB;";
        statement.executeUpdate(sql); // TABLE KICK
        Bukkit.getConsoleSender()
                .sendMessage("§aTable §6" + this.tablePrefix + "KICK §acreated or initialized: §6success");
    }
}