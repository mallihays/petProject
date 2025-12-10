import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:virtual_pet.db";
    private Connection connection;

    public DatabaseManager() {
        initializeDatabase();
    }
    private void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            System.out.println("✅ Database initialized successfully!");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("❌ Database initialization error: " + e.getMessage());
        }
    }

    private void createTables() {
        String createPetsTable = """
            CREATE TABLE IF NOT EXISTS pets (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                health INTEGER NOT NULL,
                max_health INTEGER NOT NULL,
                energy INTEGER NOT NULL,
                hunger INTEGER NOT NULL,
                happiness INTEGER NOT NULL,
                state TEXT NOT NULL,
                decorators TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_played TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        String createGameHistoryTable = """
            CREATE TABLE IF NOT EXISTS game_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                pet_id INTEGER NOT NULL,
                action TEXT NOT NULL,
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                health_after INTEGER,
                energy_after INTEGER,
                hunger_after INTEGER,
                happiness_after INTEGER,
                FOREIGN KEY (pet_id) REFERENCES pets(id)
            )
        """;

        String createStatsTable = """
            CREATE TABLE IF NOT EXISTS pet_statistics (
                pet_id INTEGER PRIMARY KEY,
                total_feeds INTEGER DEFAULT 0,
                total_plays INTEGER DEFAULT 0,
                total_sleeps INTEGER DEFAULT 0,
                total_turns INTEGER DEFAULT 0,
                FOREIGN KEY (pet_id) REFERENCES pets(id)
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPetsTable);
            stmt.execute(createGameHistoryTable);
            stmt.execute(createStatsTable);
        } catch (SQLException e) {
            System.err.println("❌ Error creating tables: " + e.getMessage());
        }
    }

    public int savePet(IPet pet) {
        String sql = """
            INSERT INTO pets (name, type, health, max_health, energy, hunger, happiness, state, decorators)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, pet.getName());
            pstmt.setString(2, pet.getType());
            pstmt.setInt(3, pet.getHealth());
            pstmt.setInt(4, pet.getMaxHealth());
            pstmt.setInt(5, pet.getEnergy());
            pstmt.setInt(6, pet.getHunger());
            pstmt.setInt(7, pet.getHappiness());
            pstmt.setString(8, pet.getState().getStateName());
            pstmt.setString(9, pet.getDecorators());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int petId = rs.getInt(1);
                pet.setPetId(petId);
                initializeStatistics(petId);
                System.out.println("💾 Pet saved to database with ID: " + petId);
                return petId;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error saving pet: " + e.getMessage());
        }
        return -1;
    }

    public void updatePet(IPet pet) {
        String sql = """
            UPDATE pets 
            SET health = ?, energy = ?, hunger = ?, happiness = ?, state = ?, 
                decorators = ?, last_played = CURRENT_TIMESTAMP
            WHERE id = ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, pet.getHealth());
            pstmt.setInt(2, pet.getEnergy());
            pstmt.setInt(3, pet.getHunger());
            pstmt.setInt(4, pet.getHappiness());
            pstmt.setString(5, pet.getState().getStateName());
            pstmt.setString(6, pet.getDecorators());
            pstmt.setInt(7, pet.getPetId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error updating pet: " + e.getMessage());
        }
    }

    public void logAction(int petId, String action, IPet pet) {
        String sql = """
            INSERT INTO game_history (pet_id, action, health_after, energy_after, hunger_after, happiness_after)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, petId);
            pstmt.setString(2, action);
            pstmt.setInt(3, pet.getHealth());
            pstmt.setInt(4, pet.getEnergy());
            pstmt.setInt(5, pet.getHunger());
            pstmt.setInt(6, pet.getHappiness());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error logging action: " + e.getMessage());
        }
    }

    public List<String> getSavedPets() {
        List<String> pets = new ArrayList<>();
        String sql = "SELECT id, name, type, health, last_played FROM pets ORDER BY last_played DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String petInfo = String.format("ID: %d | %s | Type: %s | HP: %d | Last Played: %s",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getInt("health"),
                        rs.getString("last_played"));
                pets.add(petInfo);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving pets: " + e.getMessage());
        }

        return pets;
    }

    public IPet loadPet(int petId) {
        String sql = "SELECT * FROM pets WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, petId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String type = rs.getString("type");
                Pet pet;

                if (type.contains("Dragon")) {
                    pet = new Dragon(rs.getString("name"),
                            rs.getInt("health"),
                            rs.getInt("energy"),
                            rs.getInt("hunger"),
                            rs.getInt("happiness"));
                } else {
                    pet = new Cat(rs.getString("name"),
                            rs.getInt("health"),
                            rs.getInt("energy"),
                            rs.getInt("hunger"),
                            rs.getInt("happiness"));
                }

                pet.setPetId(petId);
                pet.maxHealth = rs.getInt("max_health");

                // Restore state
                String stateName = rs.getString("state");
                pet.setState(getStateFromName(stateName));

                // Apply decorators if any
                String decorators = rs.getString("decorators");
                pet.setDecorators(decorators);

                IPet finalPet = pet;
                if (decorators != null && !decorators.isEmpty()) {
                    if (decorators.contains("Armor")) {
                        finalPet = new ArmorDecorator(finalPet);
                    }
                    if (decorators.contains("Amulet")) {
                        finalPet = new AmuletDecorator(finalPet);
                    }
                }

                System.out.println("📂 Loaded pet: " + pet.getName());
                return finalPet;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading pet: " + e.getMessage());
        }

        return null;
    }

    private IPetState getStateFromName(String stateName) {
        return switch (stateName) {
            case "◝(ᵔᗜᵔ)◜Happy" -> new HappyState();
            case "( •̀ ᴖ •́ )Hungry" -> new HungryState();
            case "(ᴗ_ ᴗ。)Sleeping" -> new SleepingState();
            case "💀 Dead" -> new DeadState();
            default -> new NormalState();
        };
    }

    public void deletePet(int petId) {
        try {
            connection.createStatement().execute("DELETE FROM game_history WHERE pet_id = " + petId);
            connection.createStatement().execute("DELETE FROM pet_statistics WHERE pet_id = " + petId);
            connection.createStatement().execute("DELETE FROM pets WHERE id = " + petId);
            System.out.println("🗑️  Pet deleted from database");
        } catch (SQLException e) {
            System.err.println("❌ Error deleting pet: " + e.getMessage());
        }
    }

    private void initializeStatistics(int petId) {
        String sql = "INSERT INTO pet_statistics (pet_id) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, petId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error initializing stats: " + e.getMessage());
        }
    }

    public void updateStatistics(int petId, String action) {
        String column = switch (action) {
            case "FEED" -> "total_feeds";
            case "PLAY" -> "total_plays";
            case "SLEEP" -> "total_sleeps";
            case "TICK" -> "total_turns";
            default -> null;
        };

        if (column != null) {
            String sql = "UPDATE pet_statistics SET " + column + " = " + column + " + 1 WHERE pet_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, petId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("❌ Error updating stats: " + e.getMessage());
            }
        }
    }

    public void displayStatistics(int petId) {
        String sql = "SELECT * FROM pet_statistics WHERE pet_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, petId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("\n📊 Pet Statistics:");
                System.out.println("   🍖 Total Feeds: " + rs.getInt("total_feeds"));
                System.out.println("   🎾 Total Plays: " + rs.getInt("total_plays"));
                System.out.println("   😴 Total Sleeps: " + rs.getInt("total_sleeps"));
                System.out.println("   🔄 Total Turns: " + rs.getInt("total_turns"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error displaying stats: " + e.getMessage());
        }
    }

    public void displayRecentHistory(int petId, int limit) {
        String sql = "SELECT * FROM game_history WHERE pet_id = ? ORDER BY timestamp DESC LIMIT ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, petId);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n📜 Recent Activity:");
            while (rs.next()) {
                System.out.printf("   %s - %s (HP:%d E:%d H:%d Happiness:%d)%n",
                        rs.getString("timestamp"),
                        rs.getString("action"),
                        rs.getInt("health_after"),
                        rs.getInt("energy_after"),
                        rs.getInt("hunger_after"),
                        rs.getInt("happiness_after"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error displaying history: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing database: " + e.getMessage());
        }
    }
}