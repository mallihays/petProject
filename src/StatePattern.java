interface IPetState {
    void handleFeed(IPet pet);
    void handlePlay(IPet pet);
    void handleSleep(IPet pet);
    void handleTick(IPet pet);
    String getStateName();
}
class HappyState implements IPetState {
    public String getStateName() { return "◝(ᵔᗜᵔ)◜Happy"; }

    public void handleFeed(IPet pet) {
        System.out.println("✅ " + pet.getName() + " enjoys the meal!");
        pet.setHunger(Math.max(0, pet.getHunger() - 30));
        pet.setHappiness(Math.min(100, pet.getHappiness() + 10));
        ((Pet)pet).updateState();
    }
    public void handlePlay(IPet pet) {
        System.out.println("✅ " + pet.getName() + " plays joyfully!");
        pet.setHappiness(Math.min(100, pet.getHappiness() + 20));
        pet.setEnergy(Math.max(0, pet.getEnergy() - 15));
        pet.setHunger(Math.min(100, pet.getHunger() + 10));
        ((Pet)pet).updateState();
    }
    public void handleSleep(IPet pet) {
        System.out.println("✅ " + pet.getName() + " takes a nap...");
        pet.setState(new SleepingState());
    }
    public void handleTick(IPet pet) {
        pet.setEnergy(Math.max(0, pet.getEnergy() - 5));
        pet.setHunger(Math.min(100, pet.getHunger() + 8));
        pet.setHappiness(Math.max(0, pet.getHappiness() - 2));
        ((Pet)pet).updateState();
    }
}

class NormalState implements IPetState {
    public String getStateName() { return "(ㆆ_ㆆ)Normal"; }

    public void handleFeed(IPet pet) {
        System.out.println("✅ " + pet.getName() + " eats the food.");
        pet.setHunger(Math.max(0, pet.getHunger() - 25));
        pet.setHappiness(Math.min(100, pet.getHappiness() + 5));
        ((Pet)pet).updateState();
    }
    public void handlePlay(IPet pet) {
        System.out.println("✅ " + pet.getName() + " plays a bit.");
        pet.setHappiness(Math.min(100, pet.getHappiness() + 15));
        pet.setEnergy(Math.max(0, pet.getEnergy() - 20));
        pet.setHunger(Math.min(100, pet.getHunger() + 10));
        ((Pet)pet).updateState();
    }
    public void handleSleep(IPet pet) {
        System.out.println("✅ " + pet.getName() + " goes to sleep...");
        pet.setState(new SleepingState());
    }
    public void handleTick(IPet pet) {
        pet.setEnergy(Math.max(0, pet.getEnergy() - 5));
        pet.setHunger(Math.min(100, pet.getHunger() + 10));
        pet.setHappiness(Math.max(0, pet.getHappiness() - 3));
        ((Pet)pet).updateState();
    }
}

class HungryState implements IPetState {
    public String getStateName() { return "( •̀ ᴖ •́ )Hungry"; }

    public void handleFeed(IPet pet) {
        System.out.println("✅ " + pet.getName() + " devours the food hungrily!");
        pet.setHunger(Math.max(0, pet.getHunger() - 40));
        pet.setHappiness(Math.min(100, pet.getHappiness() + 15));
        ((Pet)pet).updateState();
    }
    public void handlePlay(IPet pet) {
        System.out.println("❌ " + pet.getName() + " is too hungry to play!");
    }
    public void handleSleep(IPet pet) {
        System.out.println("❌ " + pet.getName() + " can't sleep when hungry!");
    }
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
    public String getStateName() { return "(ᴗ_ ᴗ。)Sleeping"; }

    public void handleFeed(IPet pet) {
        System.out.println("❌ " + pet.getName() + " is sleeping! Wake them up first.");
    }

    public void handlePlay(IPet pet) {
        System.out.println("💤 " + pet.getName() + " wakes up!");
        pet.setState(new NormalState());
    }

    public void handleSleep(IPet pet) {
        System.out.println("💤 " + pet.getName() + " is already sleeping...");
    }

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
    public String getStateName() { return "💀 Dead"; }

    public void handleFeed(IPet pet) {
        System.out.println("💀 " + pet.getName() + " has passed away...");
    }
    public void handlePlay(IPet pet) {
        System.out.println("💀 " + pet.getName() + " has passed away...");
    }
    public void handleSleep(IPet pet) {
        System.out.println("💀 " + pet.getName() + " has passed away...");
    }
    public void handleTick(IPet pet) {
        // Dead pets don't tick
    }
}