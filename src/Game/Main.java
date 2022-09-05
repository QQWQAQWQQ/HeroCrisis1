package Game;

public class Main {
    public static void main(String[] args) {
        new Start();
        String filepath = "Music\\bgm.wav";
        music musicObject = new music();
        musicObject.playMusic(filepath);
    }
}
