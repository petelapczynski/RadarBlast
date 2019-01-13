package petelap.radarblast;

import java.util.List;

public class Levels {
    private List<Level> levelList;

    public List<Level> getLevels() {
        return levelList;
    }

    public void setLevels(List<Level> Levels) {
        levelList = Levels;
    }

    public Level getLevel(int level) {
        for (Level lvl: levelList) {
            if (lvl.number == level) {
                return lvl;
            }
        }
        return levelList.get(0);
    }

    public class Level {
        private int number;
        private String name;
        private String desc;
        private String gameObjects;
        private List<LevelObjects> levelObjects;

        public int getNumber() {
            return number;
        }
        public String getName() {
            return name;
        }
        public String getDesc() { return desc; }
        public String getGameObjects() {
            return gameObjects;
        }
        public List<LevelObjects> getLevelObjects() {
            return levelObjects;
        }
        public void setNumber(int Number) {
            number = Number;
        }
        public void setName(String Name) { name = Name; }
        public void setDesc(String Desc) { desc = Desc; }
        public void setGameObjects(String GameObjects) {
            gameObjects = GameObjects;
        }
        public void setLevelObjects(List<LevelObjects> LevelObjects) {
            levelObjects = LevelObjects;
        }

        public class LevelObjects {
            private int height;
            private int width;
            private int posX;
            private int posY;

            public int getHeight() {
                return height;
            }
            public int getWidth() {
                return width;
            }
            public int getPosX() {
                return posX;
            }
            public int getPosY() {
                return posY;
            }
            public void setHeight(int Height) {
                height = Height;
            }
            public void setWidth(int Width) {
                width = Width;
            }
            public void setPosX(int PosX) {
                posX = PosX;
            }
            public void setPosY(int PosY) {
                posY = PosY;
            }
        }
    }
}