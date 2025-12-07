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