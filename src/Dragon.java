class Dragon extends Pet {
    public Dragon(String name, int health, int energy, int hunger, int happiness) {
        super(name, health, energy, hunger, happiness);
    }
    @Override
    public String getType() {
        return "🐉 Dragon";
    }
}