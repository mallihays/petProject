public class Main {
    public static void main(String[] args) {
        GameEngine game = new GameEngine();
        game.start();
    }
}
//✅ Single Responsibility - Main only starts the application
//✅ Dependency Inversion - Depends on GameEngine abstraction1