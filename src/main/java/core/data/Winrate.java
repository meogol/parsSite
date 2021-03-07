package core.data;

public class Winrate {
    int win =0;
    int louse = 0;

    public int getLouse() {
        return louse;
    }

    public int getWin() {
        return win;
    }

    public void setLouse(int louse) {
        this.louse = louse;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public void addLouse(int louse) {
        this.louse += louse;
    }

    public void addWin(int win) {
        this.win += win;
    }
}
