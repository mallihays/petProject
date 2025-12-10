abstract class Pet implements IPet {
    protected String name,decorators = "";
    protected int health,energy,hunger,happiness,maxHealth, petId=-1;
    protected IPetState currentState;
    //State Pattern - current behavior state.Polymorphism - holds any IPetState implementation

    protected Pet(String name, int health, int energy, int hunger, int happiness) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.energy = energy;
        this.hunger = hunger;
        this.happiness = happiness;
        this.currentState = new HappyState(); //Default state - all pets start happy .State Pattern initialization
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
    public void setDecorators(String decorators) {
        this.decorators = decorators;
    }

    @Override
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(maxHealth, health)); //health stays in range [0, maxHealth]
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
        if (this.currentState == null || !this.currentState.getClass().equals(state.getClass())) {
            this.currentState = state;
            System.out.println("🔄 " + name + " is now " + state.getStateName());
        } else {
            this.currentState = state;
        }
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

//✅ Single Responsibility
//
//Pet manages its own data and basic operations
//Doesn't handle UI, database, or game loop
//
//✅ Open/Closed
//
//Open for extension (subclass for new pet types)
//Closed for modification (core logic stable)
//
//✅ Liskov Substitution
//
//Cat and Dragon can replace Pet anywhere
//No unexpected behavior differences
//
//✅ Dependency Inversion
//
//Depends on IPetState interface, not concrete states
//Can add new states without changing Pet class