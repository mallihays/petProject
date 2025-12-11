class PetBuilder {
    // Private fields for all required Pet parameters, with default values
    private String name = "Unnamed";
    private String type = "cat";
    private int health = 100;
    private int energy = 80;
    private int hunger = 30;
    private int happiness = 70;

    // Fluent Setters: Each setter modifies a parameter and returns 'this'
    // This allows method chaining.
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

    // Final build method: The "factory" part that creates the concrete Product (IPet)
    public IPet build() {
        if (type.equals("dragon")) {
            // Dragon-specific logic from your original snippet (Health + 50)
            return new Dragon(name, health + 50, energy, hunger, happiness);
        } else {
            // Cat-specific logic from your original snippet (Energy + 20)
            // This also acts as the default if a non-existent type is given
            return new Cat(name, health, energy + 20, hunger, happiness);
        }
    }
}
//✅ Single Responsibility
//Builder ONLY constructs pets
//Doesn't handle game logic or display