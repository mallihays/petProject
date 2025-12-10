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

//    public PetBuilder setHealth(int health) {
//        this.health = health;
//        return this;
//    }
//
//    public PetBuilder setEnergy(int energy) {
//        this.energy = energy;
//        return this;
//    }
//
//    public PetBuilder setHunger(int hunger) {
//        this.hunger = hunger;
//        return this;
//    }
//
//    public PetBuilder setHappiness(int happiness) {
//        this.happiness = happiness;
//        return this;
//    }

    public IPet build() {
        if (type.equals("dragon")) {
            return new Dragon(name, health + 50, energy, hunger, happiness);
        } else {
            return new Cat(name, health, energy + 20, hunger, happiness);
        }
    }
}
//SOLID Principles:
//✅ Single Responsibility
//
//Builder ONLY constructs pets
//Doesn't handle game logic or display
//
//✅ Open/Closed
//
//Can add new pet types by modifying build()
//Could refactor to make truly extensible with registry
//
//✅ Dependency Inversion
//
//Returns IPet interface, not concrete class