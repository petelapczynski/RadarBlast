package petelap.radarblast;

import java.util.ArrayList;
import java.util.List;

public class Highscores {
    private List<Highscore> scoresList;

    public List<Highscore> getHighscores() {
        return scoresList;
    }

    public void setHighscores(List<Highscore> HighScores) {
        scoresList = HighScores;
    }

    public int getMinHighscore() {
        return scoresList.get(4).getScore();
    }

    public int getMaxHighscore() {
        return scoresList.get(0).getScore();
    }

    public void updateHighscores(int score, String name) {
        List<Highscore> newScores = new ArrayList<>();
        boolean bFound = false;
        for (int i = 0; i < scoresList.size(); i++) {
            Highscore hs = new Highscore();
            hs.setNumber(i+1);

            if (!bFound && score >= scoresList.get(i).getScore()) {
                hs.setScore(score);
                hs.setName(name);
                bFound = true;
            } else {
                hs.setScore(scoresList.get(i).getScore());
                hs.setName(scoresList.get(i).getName());
            }
            newScores.add(hs);
        }
        setHighscores(newScores);
    }

    public class Highscore {
        private int number;
        private String name;
        private int score;

        public int getNumber() {
            return number;
        }
        public String getName() {
            return name;
        }
        public int getScore() {
            return score;
        }
        public void setNumber(int Number) {
            number = Number;
        }
        public void setName(String Name) {
            name = Name;
        }
        public void setScore(int Score) {
            score = Score;
        }
    }
}