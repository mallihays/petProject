class Cat extends Pet {
    public Cat(String name, int health, int energy, int hunger, int happiness) {
        super(name, health, energy, hunger, happiness);
    }

    @Override
    public String getType() { return "🐱 Cat"; }
}