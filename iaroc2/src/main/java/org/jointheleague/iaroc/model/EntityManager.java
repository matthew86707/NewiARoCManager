package org.jointheleague.iaroc.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jointheleague.iaroc.db.DBUtils;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class EntityManager {

    private static final String[] relationshipTableNames = {"MATCH_RESULTS"};

    private static final String DELETE_MATCH_RESULT_BY_MATCH = "DELETE FROM MATCH_RESULTS WHERE matchId = ?";

    private static final String DROP_MATCH_RESULT_TABLE = "DROP TABLE MATCH_RESULTS";

    private static final String SELECT_MATCH_RESULTS = "SELECT * FROM MATCH_RESULTS";

    private static final String SELECT_MATCH_RESULT_BY_MATCH = "SELECT * FROM MATCH_RESULTS WHERE matchId = ?";

    private static final String SELECT_MATCH_RESULT_BY_TEAM = "SELECT * FROM MATCH_RESULTS WHERE teamId = ?";

    private static final String SELECT_MATCH_RESULT_BY_TEAM_AND_MATCH = "SELECT * FROM MATCH_RESULTS WHERE teamId = ? AND matchId = ?";

    private static final String SELECT_MATCH_RESULTS_EXTENDED = "SELECT mr.matchId as matchId, mr.teamId as teamId, " +
            "mr.time as scoreTime, " +
            "mr.bonusPoints as bonusPoints, " +
            "mr.completedObjective as completedObjective, " +
            "mr.isFinalResult as isFinalResult, " +
            "m.type as matchType, " +
            "m.unixTime as matchTime, " +
            "t.name as teamName, " +
            "t.iconUrl as iconUrl " +
            "FROM MATCH_RESULTS as mr " +
            "INNER JOIN MATCHES as m on mr.matchId = m.id " +
            "INNER JOIN TEAMS as t on mr.teamId = t.id " +
            "ORDER BY isFinalResult, unixTime asc, matchId asc";

    private static final String INSERT_MATCH_RESULT = "INSERT INTO MATCH_RESULTS (matchId, teamId, time, bonusPoints," +
            "completedObjective, isFinalResult)" +
            " values (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_MATCH_RESULTS = "UPDATE MATCH_RESULTS (matchId, teamId, time, bonusPoints," +
            "completedObjective, isFinalResult) values (?, ?, ?, ?, ?, ?)" +
            " WHERE matchId = ? AND teamId = ?";

    private static final String DELETE_MATCH_RESULT_BY_MATCH_AND_TEAM = "DELETE * FROM MATCH_RESULTS WHERE matchId = ? AND teamId = ?";

    private static final String CREATE_MATCH_RESULTS_TABLE = "CREATE TABLE MATCH_RESULTS"
            + "(matchId INTEGER, "
            + "teamId INTEGER,"
            + "time INTEGER,"
            + "bonusPoints INTEGER,"
            + "completedObjective BOOLEAN,"
            + "isFinalResult BOOLEAN)";

    //For each match of a certain event, produce results. They are sorted by time and whether finished or not
    //Such that this also forms a ranking of performance.
    private static final String SELECT_MATCH_RESULTS_BY_TYPE = "SELECT m.id as matchId, m.type as type," +
            " mr.teamId as teamId, mr.time as time, mr.bonusPoints as bonusPoints," +
            " mr.completedObjective as completedObjective, mr.isFinalResult as isFinalResult " +
            " FROM MATCHES as m " +
            " INNER JOIN MATCH_RESULTS as mr " +
            " ON m.id = mr.matchId  " +
            " WHERE m.type = ?" +
            " ORDER BY completedObjective desc, time asc";


    public static final int MATCH_STATUS_PENDING = 0;
    public static final int MATCH_STATUS_COMPLETE = 1;
    public static final int MATCH_STATUS_CANCELLED = 2;

    public static final ZoneOffset PST_TIME_OFFSET = ZoneOffset.ofHours(-7);

    public static void addDummyData(Connection con) {

        createTables(con);

        TeamDAO t1 = new TeamDAO(con, "Red Team", "http://images.clipartpanda.com/hawk-clipart-KTjky9GTq.gif");
        TeamDAO t2 = new TeamDAO(con, "Blue Team", "http://www.clipartlord.com/wp-content/uploads/2014/03/dolphin8.png");
        TeamDAO t3 = new TeamDAO(con, "Purple Team", "http://www.clipartpal.com/_thumbs/pd/holiday/christmas/Snowman_12.png");
        t1.insert();
        t2.insert();
        t3.insert();

//        MemberDAO roger = new MemberDAO(con, "Roger", "Rabbit", "r.rabbit@reddit.com", t1.getId());
//        MemberDAO bob = new MemberDAO(con, "Bob", "Barn", "b.barn@reddit.com", t1.getId());
//
//        MemberDAO johnathan = new MemberDAO(con, "johnathan", "jackrabbit", "j.rabbit@reddit.com", t2.getId());
//        MemberDAO robert = new MemberDAO(con, "Robert", "Barner", "r.barner@reddit.com", t2.getId());
//
//        MemberDAO ronathan = new MemberDAO(con, "Ronathan", "Babbit", "r.b@whitehouse.gov", t3.getId());
//
//        roger.insert();
//        bob.insert();
//        johnathan.insert();
//        robert.insert();
//        ronathan.insert();



        //TODO: Add relationships to dummy data using new system

        LocalDateTime m1dt = LocalDateTime.of(2017, 06, 25, 12, 25);
        MatchDAO m1 = new MatchDAO(con, 0, m1dt.toEpochSecond(PST_TIME_OFFSET), MatchDAO.TYPES.DRAG_RACE);
        m1.insert();


        LocalDateTime m2dt = LocalDateTime.of(2017, 06, 25, 12, 45);
        MatchDAO m2 = new MatchDAO(con, 0, m2dt.toEpochSecond(PST_TIME_OFFSET), MatchDAO.TYPES.DRAG_RACE);
        m2.insert();

        LocalDateTime m3dt = LocalDateTime.of(2017, 06, 25, 15, 0);
        MatchDAO m3 = new MatchDAO(con, 0, m3dt.toEpochSecond(PST_TIME_OFFSET), MatchDAO.TYPES.MAZE);
        m3.insert();

        LocalDateTime m4dt = LocalDateTime.of(2017, 06, 26, 10, 25);
        MatchDAO m4 = new MatchDAO(con, 0, m4dt.toEpochSecond(PST_TIME_OFFSET), MatchDAO.TYPES.MAZE);
        m4.insert();

        LocalDateTime m5dt = LocalDateTime.of(2017, 06, 26, 12, 30);
        MatchDAO m5 = new MatchDAO(con, 0, m5dt.toEpochSecond(PST_TIME_OFFSET), MatchDAO.TYPES.GOLD_RUSH);
        m5.insert();

        MatchResultData mr11 = new MatchResultData();
        mr11.time = 110000;
        mr11.completedObjective = true;
        mr11.isFinalResult = true;
        mr11.teamId = t1.getId();
        mr11.matchId = m1.getId();
        mr11.bonusPoints = 5;
        EntityManager.insertOrUpdateMatchResult(con, mr11);


        MatchResultData mr22 = new MatchResultData();
        mr22.time = 100000;
        mr22.completedObjective = true;
        mr22.isFinalResult = true;
        mr22.teamId = t2.getId();
        mr22.matchId = m2.getId();
        mr22.bonusPoints = 0;

        EntityManager.insertOrUpdateMatchResult(con, mr22);

        MatchResultData mr33 = new MatchResultData();
        mr33.time = 50000;
        mr33.completedObjective = true;
        mr33.isFinalResult = true;
        mr33.teamId = t3.getId();
        mr33.matchId = m3.getId();
        mr33.bonusPoints = 5;

        EntityManager.insertOrUpdateMatchResult(con, mr33);

        MatchResultData mr14 = new MatchResultData();
        mr14.time = 60000;
        mr14.completedObjective = true;
        mr14.isFinalResult = true;
        mr14.teamId = t1.getId();
        mr14.matchId = m4.getId();
        mr14.bonusPoints = 5;

        EntityManager.insertOrUpdateMatchResult(con, mr14);

        MatchResultData mr15 = new MatchResultData();
        mr15.time = -1;
        mr15.completedObjective = false;
        mr15.isFinalResult = true;
        mr15.teamId = t1.getId();
        mr15.matchId = m5.getId();
        mr15.bonusPoints = 0;

        EntityManager.insertOrUpdateMatchResult(con, mr15);

        MatchResultData mr25 = new MatchResultData();
        mr25.time = -1;
        mr25.completedObjective = false;
        mr25.isFinalResult = false;
        mr25.teamId = t2.getId();
        mr25.matchId = m5.getId();
        mr25.bonusPoints = 0;

        EntityManager.insertOrUpdateMatchResult(con, mr25);

        MatchResultData mr35 = new MatchResultData();
        mr35.time = 30000;
        mr35.completedObjective = true;
        mr35.isFinalResult = true;
        mr35.teamId = t3.getId();
        mr35.matchId = m5.getId();
        mr35.bonusPoints = 0;

        EntityManager.insertOrUpdateMatchResult(con, mr35);
    }

    public static List<MatchResultData> getAllMatchResults(Connection con) {
        List<MatchResultData> results = new ArrayList<>();
        try {
            PreparedStatement stmt = con.prepareStatement(SELECT_MATCH_RESULTS);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(extractMatchResultFromSQL(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }


    public static List<Integer> getTeamsByMatch(Connection con, int matchId) {
        List<Integer> teamIds = new ArrayList<>();
        try {
            PreparedStatement stmt = con.prepareStatement(SELECT_MATCH_RESULT_BY_MATCH);
            stmt.setInt(1, matchId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                teamIds.add(new Integer(rs.getInt("teamId")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teamIds;
    }

    public static void clearResultsForMatch(Connection con, int id) {
        try {
            PreparedStatement stmt = con.prepareStatement(DELETE_MATCH_RESULT_BY_MATCH);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Simple data class that encapsulates information on the result of a match for a certain team.
     */
    public static class MatchResultData {
        public int teamId;
        public int matchId;
        public long time;
        public int bonusPoints;
        public boolean completedObjective;
        //Whether or not this is a finalized result or if it's merely a placeholder to let us show upcoming match participation.
        public boolean isFinalResult;
        public int placementPoints;
        public int totalPoints;

        public static MatchResultData fromJson(String matchResultsStr) throws IOException {
            JsonNode node = new ObjectMapper().readTree(matchResultsStr);
            return fromJsonObject(node);
        }

        public static MatchResultData fromJsonObject(JsonNode node) throws IOException {
            MatchResultData resultData = new MatchResultData();
            resultData.matchId = node.get("matchId").asInt();
            resultData.teamId = node.get("teamId").asInt();
            if (node.has("isFinalResult")) {
                resultData.isFinalResult = node.get("isFinalResult").asBoolean();
            }
            if (node.has("completedObjective")) {
                resultData.completedObjective = node.get("completedObjective").asBoolean();
            }
            if (node.has("time")) {
                resultData.time = node.get("time").asLong();
            }
            if (node.has("bonusPoints")) {
                resultData.bonusPoints = node.get("bonusPoints").asInt();
            }
            return resultData;
        }

        public ObjectNode toJSON() {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode jsonRoot = mapper.createObjectNode();
            jsonRoot.put("matchId", this.matchId).
                    put("teamId", this.teamId).
                    put("isFinalResult", this.isFinalResult).
                    put("completedObjective", this.completedObjective).
                    put("time", time).
                    put("bonusPoints", bonusPoints).
                    put("totalPoints", totalPoints).
                    put("placementPoints", placementPoints);
            return jsonRoot;
        }

        public String toJSONString() {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(toJSON());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    /**
     * @param con
     * @param matchType
     * @param includeNonFinalResults If true, will include results that have not completed (Or even started) yet.
     *                               Note that this could mess up placement points if not used carefully.
     *                               Calculate all results for the provided match type, ranked from the leader down.
     * @return
     */
    public static List<MatchResultData> calculateEventResults(Connection con, MatchDAO.TYPES matchType,
                                                              boolean includeNonFinalResults) {
        List<MatchResultData> matchResults = new ArrayList<>();
        try {
            PreparedStatement stmt = con.prepareStatement(SELECT_MATCH_RESULTS_BY_TYPE);
            stmt.setString(1, matchType.toString());

            ResultSet rs = stmt.executeQuery();
            //This should be pre-sorted with the smallest time first.
            //So, all we need to do is count up the number of entries remaining to see how many placement points were earned.
            //There are two assumptions here;
            //First, even teams that did not finish count towards the scores of those that did finish.
            //Second, the time system is sufficiently precise that ties will not occur. If they do, one team should have
            //its time reduced by a ms to resolve.

            //Just incase someone decides to log multiple results for the same team for an event (e.g. logging
            //all three attempts for drag race), go ahead and only accept the first result for each team.
            Set<Integer> teamsAlreadyCovered = new HashSet<>();

            //Going to take the easy way and make this a two-step process so that we will form the results first and
            //get a count. Then, go back over the results an calculate points.
            while (rs.next()) {
                MatchResultData resultData = extractMatchResultFromSQL(rs);
                //If this result isn't in yet, we don't want to report it, since it will throw off other results
                //for it to be included as if it were a team that had failed to finish.
                if (!includeNonFinalResults && !resultData.isFinalResult) {
                    continue;
                }

                if (teamsAlreadyCovered.contains(resultData.teamId)) {
                    continue;
                }
                teamsAlreadyCovered.add(resultData.teamId);

                matchResults.add(resultData);
            }

            for (int i = 0; i < matchResults.size(); i++) {
                //By the rules as written, you get one point for each team ranked lower than you.
                int numberOfResultsRemaining = matchResults.size() - i - 1;
                MatchResultData result = matchResults.get(i);
                if (!result.completedObjective) {
                    //We have presorted such that non-finishers are on the bottom. So, we know everyone beyond
                    //this point did not earn placement points.
                    break;
                }
                result.placementPoints = numberOfResultsRemaining;
                result.totalPoints = result.placementPoints + result.bonusPoints;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matchResults;
    }

    public static MatchResultData getMatchResult(Connection con, int matchId, int teamId) {
        MatchResultData result = null;
        try {
            PreparedStatement stmt = con.prepareStatement(SELECT_MATCH_RESULT_BY_TEAM_AND_MATCH);
            stmt.setInt(1, teamId);
            stmt.setInt(2, matchId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = new MatchResultData();
                result.bonusPoints = rs.getInt("bonusPoints");
                result.completedObjective = rs.getBoolean("completedObjective");
                result.time = rs.getLong("time");
                result.matchId = matchId;
                result.teamId = teamId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * Get info on every match result, including info on the associated team and match.
     * (Note that if we really wanted this to not waste bandwidth, the return would not duplicate info but
     * would probably return match results, matchces, and teams separately and let the
     * client combine them. But, for a quick and dirty approach, this works)
     * The sorting is optimized for allowing entry of new match result data.
     * e.g. match results without data are first, then sorted with oldest matches first, and then just by match ID.
     * @param con
     * @return
     */
    public static ObjectNode getMatchResultsExtended(Connection con) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        ArrayNode innerResults = result.putArray("matchResults");
        try {
            PreparedStatement stmt = con.prepareStatement(SELECT_MATCH_RESULTS_EXTENDED);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ObjectNode curResult = mapper.createObjectNode();
                ResultSetMetaData curMetaData = rs.getMetaData();
                for(int i = 1; i <= curMetaData.getColumnCount(); i++) {
                    String label = curMetaData.getColumnLabel(i);
                    Object curVal = rs.getObject(i);
                    curResult.putPOJO(label, curVal);
                }
                innerResults.add(curResult);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void removeTeamFromRelationships(Connection con, int teamId) {
        try {
            for (String tName : relationshipTableNames) {
                PreparedStatement stmt = con.prepareStatement("DELETE FROM " + tName + " WHERE teamId = ?");
                stmt.setInt(1, teamId);
                stmt.executeUpdate();
            }
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert a match result. Note that this can be for a match that has not occurred yet, in which case it will be
     * mostly empty.
     *
     * @param con
     * @param result
     */
    public static void insertOrUpdateMatchResult(Connection con, MatchResultData result) {
        try {
            PreparedStatement stmt = con.prepareStatement(SELECT_MATCH_RESULT_BY_TEAM_AND_MATCH);
            stmt.setInt(1, result.teamId);
            stmt.setInt(2, result.matchId);
            ResultSet rs = stmt.executeQuery();
            //If the match result already exists, have this update instead.
            if (rs.next()) {
                PreparedStatement stmt2 = con.prepareStatement(UPDATE_MATCH_RESULTS);
                stmt2.setInt(1, result.matchId);
                stmt2.setInt(2, result.teamId);
                stmt2.setLong(3, result.time);
                stmt2.setInt(4, result.bonusPoints);
                stmt2.setBoolean(5, result.completedObjective);
                stmt2.setBoolean(6, result.isFinalResult);
                stmt2.setInt(7, result.matchId);
                stmt2.setInt(8, result.teamId);
                stmt2.executeUpdate();
            }
            else {
                PreparedStatement stmt2 = con.prepareStatement(INSERT_MATCH_RESULT);
                stmt2.setInt(1, result.matchId);
                stmt2.setInt(2, result.teamId);
                stmt2.setLong(3, result.time);
                stmt2.setInt(4, result.bonusPoints);
                stmt2.setBoolean(5, result.completedObjective);
                stmt2.setBoolean(6, result.isFinalResult);
                stmt2.executeUpdate();
            }

            //While we are at it, go ahead and set the associated match to complete.
            if(result.isFinalResult) {
                MatchDAO associatedMatch = MatchDAO.loadById(result.matchId, con);

                if(associatedMatch != null  && associatedMatch.getStatus() == MATCH_STATUS_PENDING) {
                    associatedMatch.setStatus(MATCH_STATUS_COMPLETE);
                }

                con.commit();
            }
        } catch (SQLException e) {
            return;
        }
    }

    public static void deleteMatchResults(Connection con, int teamId, int matchId) {
        try {
            PreparedStatement stmt = con.prepareStatement(DELETE_MATCH_RESULT_BY_MATCH_AND_TEAM);
            stmt.setInt(1, matchId);
            stmt.setInt(2, teamId);
            stmt.executeUpdate();
            con.commit();
        } catch (SQLException e) {

        }
    }

    public static void createRelationshipTables(Connection con) {
        try {
            //If table already exists, drop and recreate.
            for (String tName : relationshipTableNames) {
                if (DBUtils.doesTableExist(con, tName)) {
                    con.prepareStatement("DROP TABLE " + tName).executeUpdate();
                    con.commit();
                }
            }
            con.prepareStatement(CREATE_MATCH_RESULTS_TABLE).executeUpdate();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTables(Connection con) {
        //Get Instances
        TeamDAO teams = new TeamDAO(con);
//        MemberDAO members = new MemberDAO(con);
        MatchDAO matches = new MatchDAO(con);
        //Create
        EntityManager.createRelationshipTables(con);
        teams.createTable();
//        members.createTable();
        matches.createTable();
    }

    public static MatchResultData extractMatchResultFromSQL(ResultSet rs) {
        try {
            int matchId = rs.getInt("matchId");
            boolean isFinalResult = rs.getBoolean("isFinalResult");
            int teamId = rs.getInt("teamId");
            long time = rs.getLong("time");
            int bonusPoints = rs.getInt("bonusPoints");
            boolean completedObjective = rs.getBoolean("completedObjective");
            MatchResultData resultData = new MatchResultData();
            resultData.matchId = matchId;
            resultData.isFinalResult = isFinalResult;
            resultData.teamId = teamId;
            resultData.time = time;
            resultData.bonusPoints = bonusPoints;
            resultData.completedObjective = completedObjective;
            return resultData;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }



}
