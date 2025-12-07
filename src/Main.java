import java.util.List;


// ============================================================================
// GAME ENGINE WITH DATABASE INTEGRATION
// ============================================================================

class GameEngine {
    private IPet currentPet;
    private java.util.Scanner scanner;
    private boolean running;
    private DatabaseManager dbManager;

    public GameEngine() {
        this.scanner = new java.util.Scanner(System.in);
        this.running = true;
        this.dbManager = new DatabaseManager();
    }

    public void start() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎮 WELCOME TO VIRTUAL PET SIMULATOR 🎮");
        System.out.println("=".repeat(60));

        mainMenu();

        if (currentPet != null && running) {
            gameLoop();
        }

        dbManager.close();
        scanner.close();
    }

    private void mainMenu() {
        while (true) {
            System.out.println("\n🏠 MAIN MENU:");
            System.out.println("1. 🆕 Create New Pet");
            System.out.println("2. 📂 Load Saved Pet");
            System.out.println("3. 📋 View All Saved Pets");
            System.out.println("4. 🗑️  Delete Pet");
            System.out.println("5. 🚪 Exit");
            System.out.print("Choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createPet();
                    return;
                case "2":
                    loadPet();
                    if (currentPet != null) return;
                    break;
                case "3":
                    viewSavedPets();
                    break;
                case "4":
                    deletePet();
                    break;
                case "5":
                    running = false;
                    return;
                default:
                    System.out.println("❌ Invalid choice!");
            }
        }
    }

    private void createPet() {
        PetBuilder builder = new PetBuilder();

        System.out.println("\n🐾 Choose your pet:");
        System.out.println("1. 🐱 Cat (Higher energy, agile)");
        System.out.println("2. 🐉 Dragon (Higher health, powerful)");
        System.out.print("Choice: ");

        String choice = scanner.nextLine();
        if (choice.equals("2")) {
            builder.setType("dragon");
        } else {
            builder.setType("cat");
        }

        System.out.print("\n📝 Enter pet name: ");
        String name = scanner.nextLine();
        builder.setName(name.isEmpty() ? "Buddy" : name);

        currentPet = builder.build();
        System.out.println("\n✅ " + currentPet.getType() + " " + currentPet.getName() + " has been created!");

        // Save to database
        dbManager.savePet(currentPet);
    }

    private void loadPet() {
        List<String> pets = dbManager.getSavedPets();

        if (pets.isEmpty()) {
            System.out.println("❌ No saved pets found!");
            return;
        }

        System.out.println("\n📂 SAVED PETS:");
        for (int i = 0; i < pets.size(); i++) {
            System.out.println((i + 1) + ". " + pets.get(i));
        }

        System.out.print("\nEnter pet number to load (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice > 0 && choice <= pets.size()) {
                String petInfo = pets.get(choice - 1);
                int petId = Integer.parseInt(petInfo.split("ID: ")[1].split(" \\|")[0]);
                currentPet = dbManager.loadPet(petId);

                if (currentPet != null) {
                    System.out.println("✅ Pet loaded successfully!");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input!");
        }
    }

    private void viewSavedPets() {
        List<String> pets = dbManager.getSavedPets();

        if (pets.isEmpty()) {
            System.out.println("\n❌ No saved pets found!");
        } else {
            System.out.println("\n📂 ALL SAVED PETS:");
            for (int i = 0; i < pets.size(); i++) {
                System.out.println((i + 1) + ". " + pets.get(i));
            }
        }
    }

    private void deletePet() {
        List<String> pets = dbManager.getSavedPets();

        if (pets.isEmpty()) {
            System.out.println("❌ No saved pets found!");
            return;
        }

        System.out.println("\n🗑️  DELETE PET:");
        for (int i = 0; i < pets.size(); i++) {
            System.out.println((i + 1) + ". " + pets.get(i));
        }

        System.out.print("\nEnter pet number to delete (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice > 0 && choice <= pets.size()) {
                String petInfo = pets.get(choice - 1);
                int petId = Integer.parseInt(petInfo.split("ID: ")[1].split(" \\|")[0]);

                System.out.print("Are you sure? (yes/no): ");
                String confirm = scanner.nextLine();
                if (confirm.equalsIgnoreCase("yes")) {
                    dbManager.deletePet(petId);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input!");
        }
    }

    private void gameLoop() {
        while (running && !(currentPet.getState() instanceof DeadState)) {
            currentPet.displayStatus();
            displayMenu();
            handleInput();
            currentPet.tick();

            // Update database after each action
            dbManager.updatePet(currentPet);
            dbManager.updateStatistics(currentPet.getPetId(), "TICK");

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (currentPet.getState() instanceof DeadState) {
            System.out.println("\n💀 Game Over! " + currentPet.getName() + " has died.");
            dbManager.updatePet(currentPet);
        }

        System.out.println("\n👋 Thanks for playing!");
    }

    private void displayMenu() {
        System.out.println("\n📋 Actions:");
        System.out.println("1. 🍖 Feed");
        System.out.println("2. 🎾 Play");
        System.out.println("3. 😴 Sleep");
        System.out.println("4. 🛡️  Equip Armor (+30 HP)");
        System.out.println("5. ✨ Equip Amulet (+15 Happiness/turn)");
        System.out.println("6. 📊 View Statistics");
        System.out.println("7. 📜 View History");
        System.out.println("8. 💾 Save & Exit");
        System.out.println("9. 🚪 Exit Without Saving");
        System.out.print("Choice: ");
    }

    private void handleInput() {
        String input = scanner.nextLine();

        switch (input) {
            case "1":
                currentPet.feed();
                dbManager.logAction(currentPet.getPetId(), "FEED", currentPet);
                dbManager.updateStatistics(currentPet.getPetId(), "FEED");
                break;
            case "2":
                currentPet.play();
                dbManager.logAction(currentPet.getPetId(), "PLAY", currentPet);
                dbManager.updateStatistics(currentPet.getPetId(), "PLAY");
                break;
            case "3":
                currentPet.sleep();
                dbManager.logAction(currentPet.getPetId(), "SLEEP", currentPet);
                dbManager.updateStatistics(currentPet.getPetId(), "SLEEP");
                break;
            case "4":
                if (!currentPet.getDecorators().contains("Armor")) {
                    currentPet = new ArmorDecorator(currentPet);
                    dbManager.logAction(currentPet.getPetId(), "EQUIPPED_ARMOR", currentPet);
                } else {
                    System.out.println("❌ Armor already equipped!");
                }
                break;
            case "5":
                if (!currentPet.getDecorators().contains("Amulet")) {
                    currentPet = new AmuletDecorator(currentPet);
                    dbManager.logAction(currentPet.getPetId(), "EQUIPPED_AMULET", currentPet);
                } else {
                    System.out.println("❌ Amulet already equipped!");
                }
                break;
            case "6":
                dbManager.displayStatistics(currentPet.getPetId());
                break;
            case "7":
                dbManager.displayRecentHistory(currentPet.getPetId(), 10);
                break;
            case "8":
                dbManager.updatePet(currentPet);
                System.out.println("💾 Game saved!");
                running = false;
                break;
            case "9":
                running = false;
                break;
            default:
                System.out.println("❌ Invalid choice!");
        }
    }
}

// ============================================================================
// MAIN CLASS
// ============================================================================

public class Main {
    public static void main(String[] args) {
        GameEngine game = new GameEngine();
        game.start();
    }
}