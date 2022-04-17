package com.example.musicdatabaseui;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataSource {
                                //      CONSTANTS
    public static final String DB_NAME = "music.db";
    public static final String CONNECTION_STRING = "jdbc:sqlite:D:\\JAVA PROGRAMS\\MusicDatabaseUI\\" +DB_NAME;

    public static final String TABLE_ALBUMS = "albums";
    public static final String COLUMN_ALBUMS_ID = "_id";
    public static final String COLUMN_ALBUMS_NAME = "name";
    public static final String COLUMN_ALBUMS_ARTIST = "artist";
    public static final int INDEX_ALBUM_ID = 1;
    public static final int INDEX_ALBUM_NAME = 2;
    public static final int INDEX_ALBUM_ARTIST = 3;

    public static final String TABLE_ARTISTS = "artists";
    public static final String COLUMN_ARTIST_ID = "_id";
    public static final String COLUMN_ARTIST_NAME = "name";
    public static final int INDEX_ARTIST_ID = 1;
    public static final int INDEX_ARTIST_NAME = 2;

    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_SONGS_ID = "_id";
    public static final String COLUMN_SONGS_TRACK = "track";
    public static final String COLUMN_SONGS_TITLE = "title";
    public static final String COLUMN_SONGS_ALBUM = "album";
    public static final int INDEX_SONG_ID = 1;
    public static final int INDEX_SONG_TRACK = 2;
    public static final int INDEX_SONG_TITLE = 3;
    public static final int INDEX_SONG_ALBUM = 4;

    public static final int ORDER_BY_NONE = 1;
    public static final int ORDER_BY_ASC = 2; // ASCENDING ORDER
    public static final int ORDER_BY_DESC = 3; // DESCENDING ORDER

    public static final String QUERY_ALBUMS_BY_ARTISTS_START = "SELECT "+TABLE_ALBUMS+'.'+COLUMN_ALBUMS_NAME+" FROM "
            +TABLE_ALBUMS+" INNER JOIN "+TABLE_ARTISTS+" ON "+TABLE_ALBUMS+"."+COLUMN_ALBUMS_ARTIST+" = "+TABLE_ARTISTS+"."+COLUMN_ARTIST_ID+
            " WHERE "+TABLE_ARTISTS+"."+COLUMN_ARTIST_NAME+" =\"";

    public static final String QUERY_ALBUMS_BY_ARTIST_SORT = " ORDER BY "+TABLE_ALBUMS+"."+COLUMN_ALBUMS_NAME+" COLLATE NOCASE ";

    public static final String QUERY_ARTIST_FOR_SONG_START = "SELECT "+TABLE_ARTISTS+"."+COLUMN_ARTIST_NAME+" , "
            +TABLE_ALBUMS+"."+COLUMN_ALBUMS_NAME+" , "+TABLE_SONGS+"."+COLUMN_SONGS_TRACK+" FROM "+TABLE_SONGS+
            " INNER JOIN "+TABLE_ALBUMS+ " ON "+TABLE_SONGS+"."+COLUMN_SONGS_ALBUM+" = "+TABLE_ALBUMS+"."+COLUMN_ALBUMS_ID+
            " INNER JOIN "+TABLE_ARTISTS+" ON "+TABLE_ALBUMS+"."+COLUMN_ALBUMS_ARTIST+" = "+TABLE_ARTISTS+"."+COLUMN_ARTIST_ID+
            " WHERE "+TABLE_SONGS+"."+COLUMN_SONGS_TITLE+" = \"";

    public static final String QUERY_ARTIST_FOR_SONG_SORT = "ORDER BY "+TABLE_ARTISTS+"."+COLUMN_ARTIST_NAME+" , "+TABLE_ALBUMS+
            "."+COLUMN_ALBUMS_NAME+" COLLATE NOCASE ";

    public static final String TABLE_ARTIST_SONG_VIEW = "artists_list";
    public static final String CREATE_ARTIST_SONG_VIEW = "CREATE VIEW IF NOT EXISTS "+TABLE_ARTIST_SONG_VIEW+" AS SELECT "+TABLE_ARTISTS+
            "."+COLUMN_ARTIST_NAME+" , "+TABLE_ALBUMS+"."+COLUMN_ALBUMS_NAME+" AS "+COLUMN_SONGS_ALBUM+" , "+TABLE_SONGS+"."+COLUMN_SONGS_TRACK+
            " , "+TABLE_SONGS+"."+COLUMN_SONGS_TITLE+" FROM "+TABLE_SONGS+" INNER JOIN "+TABLE_ALBUMS+" ON "+TABLE_SONGS+"."+COLUMN_SONGS_ALBUM+
            " = "+TABLE_ALBUMS+"."+COLUMN_ALBUMS_ID+" INNER JOIN "+TABLE_ARTISTS+" ON "+TABLE_ALBUMS+"."+COLUMN_ALBUMS_ARTIST+" = "+TABLE_ARTISTS+
            "."+COLUMN_ARTIST_ID+" ORDER BY "+TABLE_ARTISTS+"."+COLUMN_ARTIST_NAME+" , "+TABLE_ALBUMS+"."+COLUMN_ALBUMS_NAME+" , " + TABLE_SONGS+
            "."+COLUMN_SONGS_TRACK;

    public static final String QUERY_VIEW_SONG_INFO = "SELECT "+COLUMN_ARTIST_NAME+" , "+COLUMN_SONGS_ALBUM+ " , "+COLUMN_SONGS_TRACK+" FROM "+
            TABLE_ARTIST_SONG_VIEW+ " WHERE "+COLUMN_SONGS_TITLE+" = \"";

    public static final String QUERY_VIEW_SONG_INFO_PREP = "SELECT "+COLUMN_ARTIST_NAME+" , "+COLUMN_SONGS_ALBUM+" , "+COLUMN_SONGS_TRACK+" FROM "+
            TABLE_ARTIST_SONG_VIEW+" WHERE "+COLUMN_SONGS_TITLE+" = ?";

    // INSERT CONSTANTS
    public static final String INSERT_ARTIST =  "INSERT INTO " +TABLE_ARTISTS+'('+COLUMN_ARTIST_NAME +") VALUES (?)";
    public static final String INSERT_ALBUM = "INSERT INTO "+TABLE_ALBUMS+'('+COLUMN_ALBUMS_NAME+", "+COLUMN_ALBUMS_ARTIST+") VALUES(?, ?)";
    public static final String INSERT_SONG = "INSERT INTO "+TABLE_SONGS+'('+COLUMN_SONGS_TRACK+", "+COLUMN_SONGS_TITLE+","+COLUMN_SONGS_ALBUM+
            ") VALUES(?, ?, ?)";

    public static final String QUERY_ARTISTS = "SELECT "+COLUMN_ARTIST_ID+" FROM "+TABLE_ARTISTS+" WHERE "+COLUMN_ARTIST_NAME+" = ?"; // Gets the ID while the WHERE clause looks for the artist with the same name
    public static final String QUERY_ALBUM = "SELECT "+COLUMN_ALBUMS_ID+" FROM "+TABLE_ALBUMS+" WHERE "+COLUMN_ALBUMS_NAME+" = ?";

    public static final String QUERY_ALBUMS_BY_ARTIST_ID = "SELECT * FROM "+TABLE_ALBUMS+" WHERE "+COLUMN_ALBUMS_ARTIST+" = ? ORDER BY "+
            COLUMN_ALBUMS_NAME+" COLLATE NOCASE";

    public static final String UPDATE_ARTIST_NAME = "UPDATE "+TABLE_ARTISTS+" SET "+COLUMN_ARTIST_NAME+" = ? WHERE "+COLUMN_ARTIST_ID+" = ?";


    private Connection conn;

    // PREPARED STATEMENTS AGAINST SQL INJECTIONS
    private PreparedStatement querySongInfoView;
    private PreparedStatement insertIntoArtists;
    private PreparedStatement insertIntoAlbums;
    private PreparedStatement insertIntoSongs;

    private PreparedStatement queryArtist;
    private PreparedStatement queryAlbum;

    private PreparedStatement queryAlbumsByArtistId;
    private PreparedStatement updateArtistName;

    private static DataSource instance = new DataSource();

    private DataSource(){
    }

    public static DataSource getInstance(){
        return instance;
    }

    public boolean open(){
        try{
            conn = DriverManager.getConnection(CONNECTION_STRING);

            querySongInfoView = conn.prepareStatement(QUERY_VIEW_SONG_INFO_PREP);
            insertIntoArtists = conn.prepareStatement(INSERT_ARTIST, Statement.RETURN_GENERATED_KEYS); // return_generated_ey provides the ey from the prepared statement
            insertIntoAlbums = conn.prepareStatement(INSERT_ALBUM, Statement.RETURN_GENERATED_KEYS);
            insertIntoSongs = conn.prepareStatement(INSERT_SONG); // we do not need return_generated_keys for this because this is the last insertion when referred to relationships of the sql tables
            queryArtist = conn.prepareStatement(QUERY_ARTISTS);
            queryAlbum = conn.prepareStatement(QUERY_ALBUM);
            queryAlbumsByArtistId = conn.prepareStatement(QUERY_ALBUMS_BY_ARTIST_ID);
            updateArtistName = conn.prepareStatement(UPDATE_ARTIST_NAME);

            return true;
        } catch (SQLException err) {
            System.out.println("Cannot connect to the database: "+ err.getMessage());
            return false;
        }
    }
    public void close(){ // THE CLOSE METHOD
        try {
            // ORDER IS IMPORTANT, CLOSE THE PREPARED STATEMENT AND THEN CLOSE THE DB CONNECTION
            if(querySongInfoView != null){
                querySongInfoView.close();
            }
            if(insertIntoArtists != null){
                insertIntoArtists.close();
            }
            if(insertIntoAlbums!=null){
                insertIntoAlbums.close();
            }
            if(insertIntoSongs!=null){
                insertIntoSongs.close();
            }
            if(queryArtist != null){
                queryArtist.close();
            }
            if(queryAlbum != null){
                queryAlbum.close();
            }
            if(queryAlbumsByArtistId != null){
                queryAlbumsByArtistId.close();
            }
            if(updateArtistName !=null){
                updateArtistName.close();
            }
            if(conn!=null){
                conn.close();}
        }catch(SQLException err){
            System.out.println("Could not close connection");
        }
    }

    public List<Artist> queryArtist(int sortOrder){
        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(TABLE_ARTISTS);
        if(sortOrder != ORDER_BY_NONE){
            sb.append(" ORDER BY ");
            sb.append(COLUMN_ARTIST_NAME);
            sb.append(" COLLATE NOCASE ");
            if(sortOrder ==  ORDER_BY_DESC){
                sb.append("DESC");
            } else{
                sb.append("ASC");
            }
        }
        try(Statement statement = conn.createStatement(); ResultSet results = statement.executeQuery(sb.toString())){
            List<Artist> artists = new ArrayList<>();
            while (results.next()){
                Artist artist = new Artist();
                artist.setId(results.getInt(INDEX_ARTIST_ID));
                artist.setName(results.getString(INDEX_ARTIST_NAME));
                artists.add(artist);
            }
            return artists;
        } catch (SQLException err){
            System.out.println("Query Failed "+err.getMessage());
            return null;
        }
    }

    public List<Albums> queryAlbumsForArtistId (int id){
        try{
            queryAlbumsByArtistId.setInt(1, id);
            ResultSet resultSet = queryAlbumsByArtistId.executeQuery();

            List<Albums> albums = new ArrayList<>();
            while (resultSet.next()){
                Albums album = new Albums();
                album.setArtist_id(resultSet.getInt(1));
                album.setName(resultSet.getString(2));
                album.setArtist_id(id);
                albums.add(album);
            }
            return albums;
        } catch (SQLException err){
            System.out.println("QUERY FAILED : "+err.getMessage());
            return null;
        }
    }

    public List<String> queryAlbumsForArtists(String artistName, int sortOrder){
        StringBuilder sb = new StringBuilder(QUERY_ALBUMS_BY_ARTISTS_START);
        sb.append(artistName);
        sb.append("\"");

        if(sortOrder != ORDER_BY_NONE){
            sb.append(QUERY_ALBUMS_BY_ARTIST_SORT);
            if(sortOrder == ORDER_BY_DESC){
                sb.append("DESC");
            } else{
                sb.append("ASC");
            }
        }
        System.out.println("SQL statement = "+sb.toString());
        try(Statement statement = conn.createStatement(); ResultSet results = statement.executeQuery(sb.toString())){
            List<String> albums = new ArrayList<>();
            while(results.next()){
                albums.add(results.getString(1));
            } return albums;
        } catch(SQLException err){
            System.out.println("QUERY FAILED : "+err.getMessage());
            return null;
        }
    }

    public void querySongMetaData(){
        String sql = "SELECT * FROM "+TABLE_SONGS;
        try(Statement statement = conn.createStatement(); ResultSet results = statement.executeQuery(sql)){
            ResultSetMetaData meta = results.getMetaData();
            int numColumns = meta.getColumnCount();
            for(int i =1; i<=numColumns; i++){ // Prints each column name
                System.out.format("Column %d in the songs table is named %s\n", i, meta.getColumnName(i));
            }
        }catch(SQLException err){
            System.out.println("QUERY FAILED "+err.getMessage());
        }
    }

    public int getCount(String table){
        String sql = "SELECT COUNT(*) AS count FROM "+table;
        try(Statement statement = conn.createStatement(); ResultSet results = statement.executeQuery(sql)){
            int count = results.getInt("count");
            System.out.format("Count = %d\n", count);
            return count;
        } catch (SQLException err){
            System.out.println("QUERY FAILED : "+err.getMessage());
            return -1;
        }
    }

    public boolean createViewSongArtist(){
        try(Statement statement = conn.createStatement()){
            statement.execute(CREATE_ARTIST_SONG_VIEW);
            return true;
        } catch (SQLException err){
            System.out.println("CREATE VIEW FAILED : "+err.getMessage());
            return false;
        }
    }

    private int insertArtist(String name) throws SQLException{
        queryArtist.setString(1, name);
        ResultSet results = queryArtist.executeQuery(); // querying the artist table to check if artist already exists
        if(results.next()){
            return results.getInt(1); // if exists, returning the id and data from results set
        } else{ // else is done if artist queried for is not found
            // Insert the artist
            insertIntoArtists.setString(1, name);
            int affectedRows = insertIntoArtists.executeUpdate(); // execute update returns the number of rows affected when the sql code ran
            if(affectedRows != 1){
                throw new SQLException("Could not insert artist");
            }
            ResultSet generatedKeys = insertIntoArtists.getGeneratedKeys();
            if(generatedKeys.next()){
                return generatedKeys.getInt(1); // gets the _id needed
            } else{
                throw new SQLException("Could not get _id for artist");
            }
        }
    }

    private int insertAlbum(String name, int artistId) throws SQLException{
        queryAlbum.setString(1, name);
        ResultSet results = queryAlbum.executeQuery(); // querying the artist table to check if album already exists
        if(results.next()){
            return results.getInt(1); // if exists, returning the id and data from results set
        } else{ // else is done if album queried for is not found
            // Insert the album
            insertIntoAlbums.setString(1, name);
            insertIntoAlbums.setInt(2, artistId);
            int affectedRows = insertIntoAlbums.executeUpdate(); // execute update returns the number of rows affected when the sql code ran
            if(affectedRows != 1){
                throw new SQLException("Could not insert album");
            }
            ResultSet generatedKeys = insertIntoAlbums.getGeneratedKeys();
            if(generatedKeys.next()){
                return generatedKeys.getInt(1); // gets the _id needed
            } else{
                throw new SQLException("Could not get _id for album");
            }
        }
    }

    public boolean updateArtistName(int id, String newName){
        try{
            updateArtistName.setString(1, newName);
            updateArtistName.setInt(2, id);
            int effectiveRecords = updateArtistName.executeUpdate();
            return effectiveRecords == 1;
        }catch(SQLException err){
            System.out.println("UPDATE FAILED: "+err.getMessage());
            return false;
        }
    }

    public void insertSong(String title,String artist, String album, int track){
        try{
            conn.setAutoCommit(false);

            int artistId = insertArtist(artist); // passing artist name into the artist method to check availability
            int albumId = insertAlbum(album, artistId);
            insertIntoSongs.setInt(1, track); // setting values in the prepared statements
            insertIntoSongs.setString(2, title);
            insertIntoSongs.setInt(3, albumId);

            int affectedRows = insertIntoSongs.executeUpdate(); // execute update returns the number of rows affected when the sql code ran
            if(affectedRows == 1){
                conn.commit();
            } else{
                throw new SQLException("Song insert failed");
            }
        } catch(Exception err){ // Catching all Exceptions and not just SQL Exceptions
            System.out.println("Insert song exception "+err.getMessage());
            try{
                System.out.println("Performing rollback");
                conn.rollback(); // if an error occurs this stops previous changes from being made since starting the transactions and ends the transaction
            } catch (SQLException err2){
                System.out.println("Things are really bad "+err2.getMessage());
            }
            finally {
                try{
                    System.out.println("Resetting default commit behaviour");
                    conn.setAutoCommit(true);
                }catch(SQLException err3){
                    System.out.println("Could not reset auto commit "+err3.getMessage());
                }
            }
        }
    }
}
