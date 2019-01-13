package petelap.radarblast;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonFileHelper {

    public Highscores getHighScores(String levelPrefix) {
        Highscores highscores = new Highscores();
        List<Highscores.Highscore> hsList = new ArrayList<>();
        JSONArray jsonArr = getJSON("highscore_" + levelPrefix + ".json", "internal");
        boolean saveFile = false;

        if (jsonArr == null){
            jsonArr = getJSON("highscore.json","assets");
            saveFile = true;
        }

        if (jsonArr != null) {
            for(int i=0; i < jsonArr.length(); i++){
                Highscores.Highscore hs = new Highscores().new Highscore();
                try {
                    JSONObject obj = jsonArr.getJSONObject(i);
                    hs.setNumber((int)obj.get("Number"));
                    hs.setName((String)obj.get("Name"));
                    hs.setScore((int)obj.get("Score"));
                    hsList.add(hs);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        highscores.setHighscores(hsList);
        if (saveFile) {
            saveHighscores(highscores, levelPrefix);
        }
        return highscores;
    }

    public void saveHighscores(Highscores highscores, String levelPrefix) {
        writeJSON("highscore_" + levelPrefix + ".json", HighScoresToJSON(highscores));
    }

    private String HighScoresToJSON(Highscores highscores){
        try {
            JSONArray jsonArr = new JSONArray();
            for (Highscores.Highscore hs : highscores.getHighscores()) {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("Number", hs.getNumber());
                jsonObj.put("Name", hs.getName());
                jsonObj.put("Score", hs.getScore());
                jsonArr.put(jsonObj);
            }
            return jsonArr.toString();
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Levels getLevels(String fileName) {
        Levels levels = new Levels();
        ArrayList<Levels.Level> lvlList = new ArrayList<>();
        JSONArray jsonArr = getJSON(fileName, "assets");
        if (jsonArr != null) {
            for(int i=0; i < jsonArr.length(); i++){
                Levels.Level lvl = new Levels().new Level();
                ArrayList<Levels.Level.LevelObjects> lvlObj = new ArrayList<>();
                try {
                    JSONObject obj = jsonArr.getJSONObject(i);
                    lvl.setNumber((int)obj.get("Number"));
                    lvl.setName((String)obj.get("Name"));
                    lvl.setDesc((String)obj.get("Desc"));
                    lvl.setGameObjects((String)obj.get("GameObjects"));
                    JSONArray jArr = obj.getJSONArray("LevelObjects");
                    for (int j=0; j < jArr.length(); j++) {
                        Levels.Level.LevelObjects lob = new Levels().new Level().new LevelObjects();
                        JSONObject jObj= jArr.getJSONObject(j);
                        lob.setPosX((int)jObj.get("PosX"));
                        lob.setPosY((int)jObj.get("PosY"));
                        lob.setHeight((int)jObj.get("Height"));
                        lob.setWidth((int)jObj.get("Width"));
                        lvlObj.add(lob);
                    }
                    lvl.setLevelObjects(lvlObj);

                    lvlList.add(lvl);
                } catch (JSONException e) {
                    e.printStackTrace();
                    lvl.setNumber(0);
                    lvl.setGameObjects("");
                    lvl.setLevelObjects(lvlObj);
                    lvlList.add(lvl);
                }
            }
        }
        levels.setLevels(lvlList);
        return levels;
    }

    public void saveLevels(String fileName, Levels levels) {
        writeJSON(fileName, LevelsToJSON(levels));
    }

    private String LevelsToJSON(Levels levels) {
        try {
            JSONArray jsonArr = new JSONArray();
            for (Levels.Level lvl : levels.getLevels()) {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("Number", lvl.getNumber());
                jsonObj.put("Name", lvl.getName());
                jsonObj.put("Desc", lvl.getDesc());
                jsonObj.put("GameObjects", lvl.getGameObjects());
                JSONArray jsonAdd = new JSONArray();
                for (Levels.Level.LevelObjects lo : lvl.getLevelObjects() ) {
                    JSONObject lObj = new JSONObject();
                    lObj.put("Height", lo.getHeight());
                    lObj.put("Width", lo.getWidth());
                    lObj.put("PosX", lo.getPosX());
                    lObj.put("PosY", lo.getPosY());
                    jsonAdd.put(lObj);
                }
                jsonObj.put("LevelObjects", jsonAdd);
                jsonArr.put(jsonObj);
            }
            return jsonArr.toString();
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private JSONArray getJSON(String fileName,String folderType) {
        String json;
        int size;

        if(folderType.equals("assets")){
            try {
                InputStream is = Constants.CONTEXT.getAssets().open(fileName);
                size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
                return new JSONArray(json);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        } else if (folderType.equals("internal")) {
            try {
                FileInputStream fis = Constants.CONTEXT.openFileInput(fileName);
                StringBuilder jsonBuilder = new StringBuilder();
                while( (size = fis.read()) != -1){
                    jsonBuilder.append(Character.toString((char) size));
                }
                json = jsonBuilder.toString();
                fis.close();
                return new JSONArray(json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void writeJSON(String fileName, String json) {
        FileOutputStream outputStream;
        try {
            outputStream = Constants.CONTEXT.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}