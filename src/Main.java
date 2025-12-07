// ============================================================================
// INTERFACES AND ABSTRACT CLASSES
// ============================================================================
import java.util.List;

/**
 * Core interface for all pets (base for Decorator pattern)
 * Dependency Inversion Principle - depend on abstraction
 */


interface IPet {
    String getName();
    int getHealth();
    int getEnergy();
    int getHunger();
    int getHappiness();
    String getType();
    void setState(IPetState state);
    IPetState getState();
    void feed();
    void play();
    void sleep();
    void tick();
    void displayStatus();
    int getMaxHealth();
    void setHealth(int health);
    void setEnergy(int energy);
    void setHunger(int hunger);
    void setHappiness(int happiness);
    int getPetId();
    void setPetId(int id);
    String getDecorators();
    void setDecorators(String decorators);
}

/**
 * State Pattern Interface
 * Open/Closed Principle - open for extension (new states)
 */
interface IPetState {
    void handleFeed(IPet pet);
    void handlePlay(IPet pet);
    void handleSleep(IPet pet);
    void handleTick(IPet pet);
    String getStateName();
}

// ============================================================================
// ABSTRACT BASE PET CLASS
// ============================================================================

abstract class Pet implements IPet {
    protected String name;
    protected int health;
    protected int energy;
    protected int hunger;
    protected int happiness;
    protected IPetState currentState;
    protected int maxHealth;
    protected int petId = -1;
    protected String decorators = "";

    protected Pet(String name, int health, int energy, int hunger, int happiness) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.energy = energy;
        this.hunger = hunger;
        this.happiness = happiness;
        this.currentState = new HappyState();
    }

    @Override
    public String getName() { return name; }

    @Override
    public int getHealth() { return health; }

    @Override
    public int getEnergy() { return energy; }

    @Override
    public int getHunger() { return hunger; }

    @Override
    public int getHappiness() { return happiness; }

    @Override
    public int getMaxHealth() { return maxHealth; }

    @Override
    public int getPetId() { return petId; }

    @Override
    public void setPetId(int id) { this.petId = id; }

    @Override
    public String getDecorators() { return decorators; }

    @Override
    public void setDecorators(String decorators) { this.decorators = decorators; }

    @Override
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(maxHealth, health));
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(100, energy));
    }

    @Override
    public void setHunger(int hunger) {
        this.hunger = Math.max(0, Math.min(100, hunger));
    }

    @Override
    public void setHappiness(int happiness) {
        this.happiness = Math.max(0, Math.min(100, happiness));
    }

    @Override
    public IPetState getState() { return currentState; }

    @Override
    public void setState(IPetState state) {
        this.currentState = state;
        System.out.println("🔄 " + name + " is now " + state.getStateName());
    }

    @Override
    public void feed() { currentState.handleFeed(this); }

    @Override
    public void play() { currentState.handlePlay(this); }

    @Override
    public void sleep() { currentState.handleSleep(this); }

    @Override
    public void tick() { currentState.handleTick(this); }

    @Override
    public void displayStatus() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🐾 " + getType() + " " + name);
        System.out.println("❤️  Health: " + health + "/" + maxHealth);
        System.out.println("⚡ Energy: " + energy + "/100");
        System.out.println("🍖 Hunger: " + hunger + "/100");
        System.out.println("😊 Happiness: " + happiness + "/100");
        System.out.println("📊 State: " + currentState.getStateName());
        System.out.println("=".repeat(50));
    }

    protected void updateState() {
        if (health <= 0) {
            setState(new DeadState());
        } else if (energy <= 20) {
            setState(new SleepingState());
        } else if (hunger >= 80) {
            setState(new HungryState());
        } else if (happiness >= 70 && hunger < 50 && energy > 50) {
            setState(new HappyState());
        } else {
            setState(new NormalState());
        }
    }
}

// ============================================================================
// CONCRETE PET CLASSES
// ============================================================================

class Cat extends Pet {
    public Cat(String name, int health, int energy, int hunger, int happiness) {
        super(name, health, energy, hunger, happiness);
    }

    @Override
    public String getType() { return "🐱 Cat"; }
}

class Dragon extends Pet {
    public Dragon(String name, int health, int energy, int hunger, int happiness) {
        super(name, health, energy, hunger, happiness);
    }

    @Override
    public String getType() { return "🐉 Dragon"; }
}

// ============================================================================
// BUILDER PATTERN
// ============================================================================

class PetBuilder {
    private String name = "Unnamed";
    private String type = "cat";
    private int health = 100;
    private int energy = 80;
    private int hunger = 30;
    private int happiness = 70;

    public PetBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PetBuilder setType(String type) {
        this.type = type.toLowerCase();
        return this;
    }

    public PetBuilder setHealth(int health) {
        this.health = health;
        return this;
    }

    public PetBuilder setEnergy(int energy) {
        this.energy = energy;
        return this;
    }

    public PetBuilder setHunger(int hunger) {
        this.hunger = hunger;
        return this;
    }

    public PetBuilder setHappiness(int happiness) {
        this.happiness = happiness;
        return this;
    }

    public IPet build() {
        if (type.equals("dragon")) {
            return new Dragon(name, health + 50, energy, hunger, happiness);
        } else {
            return new Cat(name, health, energy + 20, hunger, happiness);
        }
    }
}

// ============================================================================
// STATE PATTERN IMPLEMENTATIONS
// ============================================================================

class HappyState implements IPetState {
    @Override
    public String getStateName() { return "😊 Happy"; }

    @Override
    public void handleFeed(IPet pet) {
        System.out.println("✅ " + pet.getName() + " enjoys the meal!");
        pet.setHunger(Math.max(0, pet.getHunger() - 30));
        pet.setHappiness(Math.min(100, pet.getHappiness() + 10));
        ((Pet)pet).updateState();
    }

    @Override
    public void handlePlay(IPet pet) {
        System.out.println("✅ " + pet.getName() + " plays joyfully!");
        pet.setHappiness(Math.min(100, pet.getHappiness() + 20));
        pet.setEnergy(Math.max(0, pet.getEnergy() - 15));
        pet.setHunger(Math.min(100, pet.getHunger() + 10));
        ((Pet)pet).updateState();
    }

    @Override
    public void handleSleep(IPet pet) {
        System.out.println("✅ " + pet.getName() + " takes a nap...");
        pet.setState(new SleepingState());
    }

    @Override
    public void handleTick(IPet pet) {
        pet.setEnergy(Math.max(0, pet.getEnergy() - 5));
        pet.setHunger(Math.min(100, pet.getHunger() + 8));
        pet.setHappiness(Math.max(0, pet.getHappiness() - 2));
        ((Pet)pet).updateState();
    }
}

class NormalState implements IPetState {
    @Override
    public String getStateName() { return "😐 Normal"; }

    @Override
    public void handleFeed(IPet pet) {
        System.out.println("✅ " + pet.getName() + " eats the food.");
        pet.setHunger(Math.max(0, pet.getHunger() - 25));
        pet.setHappiness(Math.min(100, pet.getHappiness() + 5));
        ((Pet)pet).updateState();
    }

    @Override
    public void handlePlay(IPet pet) {
        System.out.println("✅ " + pet.getName() + " plays a bit.");
        pet.setHappiness(Math.min(100, pet.getHappiness() + 15));
        pet.setEnergy(Math.max(0, pet.getEnergy() - 20));
        pet.setHunger(Math.min(100, pet.getHunger() + 10));
        ((Pet)pet).updateState();
    }

    @Override
    public void handleSleep(IPet pet) {
        System.out.println("✅ " + pet.getName() + " goes to sleep...");
        pet.setState(new SleepingState());
    }

    @Override
    public void handleTick(IPet pet) {
        pet.setEnergy(Math.max(0, pet.getEnergy() - 5));
        pet.setHunger(Math.min(100, pet.getHunger() + 10));
        pet.setHappiness(Math.max(0, pet.getHappiness() - 3));
        ((Pet)pet).updateState();
    }
}

class HungryState implements IPetState {
    @Override
    public String getStateName() { return "😠 Hungry"; }

    @Override
    public void handleFeed(IPet pet) {
        System.out.println("✅ " + pet.getName() + " devours the food hungrily!");
        pet.setHunger(Math.max(0, pet.getHunger() - 40));
        pet.setHappiness(Math.min(100, pet.getHappiness() + 15));
        ((Pet)pet).updateState();
    }

    @Override
    public void handlePlay(IPet pet) {
        System.out.println("❌ " + pet.getName() + " is too hungry to play!");
    }

    @Override
    public void handleSleep(IPet pet) {
        System.out.println("❌ " + pet.getName() + " can't sleep when hungry!");
    }

    @Override
    public void handleTick(IPet pet) {
        pet.setEnergy(Math.max(0, pet.getEnergy() - 8));
        pet.setHunger(Math.min(100, pet.getHunger() + 12));
        pet.setHappiness(Math.max(0, pet.getHappiness() - 10));
        pet.setHealth(Math.max(0, pet.getHealth() - 5));
        ((Pet)pet).updateState();
    }
}

class SleepingState implements IPetState {
    private int sleepTurns = 0;

    @Override
    public String getStateName() { return "😴 Sleeping"; }

    @Override
    public void handleFeed(IPet pet) {
        System.out.println("❌ " + pet.getName() + " is sleeping! Wake them up first.");
    }

    @Override
    public void handlePlay(IPet pet) {
        System.out.println("💤 " + pet.getName() + " wakes up!");
        pet.setState(new NormalState());
    }

    @Override
    public void handleSleep(IPet pet) {
        System.out.println("💤 " + pet.getName() + " is already sleeping...");
    }

    @Override
    public void handleTick(IPet pet) {
        sleepTurns++;
        pet.setEnergy(Math.min(100, pet.getEnergy() + 20));
        pet.setHealth(Math.min(pet.getMaxHealth(), pet.getHealth() + 5));

        if (sleepTurns >= 2 || pet.getEnergy() >= 90) {
            System.out.println("⏰ " + pet.getName() + " wakes up refreshed!");
            pet.setState(new NormalState());
        }
    }
}

class DeadState implements IPetState {
    @Override
    public String getStateName() { return "💀 Dead"; }

    @Override
    public void handleFeed(IPet pet) {
        System.out.println("💀 " + pet.getName() + " has passed away...");
    }

    @Override
    public void handlePlay(IPet pet) {
        System.out.println("💀 " + pet.getName() + " has passed away...");
    }

    @Override
    public void handleSleep(IPet pet) {
        System.out.println("💀 " + pet.getName() + " has passed away...");
    }

    @Override
    public void handleTick(IPet pet) {
        // Dead pets don't tick
    }
}

// ============================================================================
// DECORATOR PATTERN
// ============================================================================

abstract class PetDecorator implements IPet {
    protected IPet wrappedPet;

    public PetDecorator(IPet pet) {
        this.wrappedPet = pet;
    }

    @Override
    public String getName() { return wrappedPet.getName(); }

    @Override
    public int getHealth() { return wrappedPet.getHealth(); }

    @Override
    public int getEnergy() { return wrappedPet.getEnergy(); }

    @Override
    public int getHunger() { return wrappedPet.getHunger(); }

    @Override
    public int getHappiness() { return wrappedPet.getHappiness(); }

    @Override
    public String getType() { return wrappedPet.getType(); }

    @Override
    public void setState(IPetState state) { wrappedPet.setState(state); }

    @Override
    public IPetState getState() { return wrappedPet.getState(); }

    @Override
    public void feed() { wrappedPet.feed(); }

    @Override
    public void play() { wrappedPet.play(); }

    @Override
    public void sleep() { wrappedPet.sleep(); }

    @Override
    public void tick() { wrappedPet.tick(); }

    @Override
    public int getMaxHealth() { return wrappedPet.getMaxHealth(); }

    @Override
    public void setHealth(int health) { wrappedPet.setHealth(health); }

    @Override
    public void setEnergy(int energy) { wrappedPet.setEnergy(energy); }

    @Override
    public void setHunger(int hunger) { wrappedPet.setHunger(hunger); }

    @Override
    public void setHappiness(int happiness) { wrappedPet.setHappiness(happiness); }

    @Override
    public int getPetId() { return wrappedPet.getPetId(); }

    @Override
    public void setPetId(int id) { wrappedPet.setPetId(id); }

    @Override
    public String getDecorators() { return wrappedPet.getDecorators(); }

    @Override
    public void setDecorators(String decorators) { wrappedPet.setDecorators(decorators); }
}

class ArmorDecorator extends PetDecorator {
    private int armorBonus = 30;

    public ArmorDecorator(IPet pet) {
        super(pet);
        String currentDec = pet.getDecorators();
        pet.setDecorators(currentDec.isEmpty() ? "Armor" : currentDec + ",Armor");
        System.out.println("⚔️  Equipped Golden Armor! +" + armorBonus + " HP");
    }

    @Override
    public int getMaxHealth() {
        return wrappedPet.getMaxHealth() + armorBonus;
    }

    @Override
    public String getType() {
        return wrappedPet.getType() + " ⚔️ [Armored]";
    }

    @Override
    public void displayStatus() {
        wrappedPet.displayStatus();
        System.out.println("🛡️  Armor Bonus: +" + armorBonus + " Max HP");
    }
}

class AmuletDecorator extends PetDecorator {
    private int happinessBonus = 15;

    public AmuletDecorator(IPet pet) {
        super(pet);
        String currentDec = pet.getDecorators();
        pet.setDecorators(currentDec.isEmpty() ? "Amulet" : currentDec + ",Amulet");
        System.out.println("✨ Equipped Magic Amulet! +" + happinessBonus + " Happiness/turn");
    }

    @Override
    public String getType() {
        return wrappedPet.getType() + " ✨ [Amulet]";
    }

    @Override
    public void tick() {
        wrappedPet.tick();
        setHappiness(Math.min(100, getHappiness() + happinessBonus));
    }

    @Override
    public void displayStatus() {
        wrappedPet.displayStatus();
        System.out.println("💎 Amulet Bonus: +" + happinessBonus + " Happiness/turn");
    }
}

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