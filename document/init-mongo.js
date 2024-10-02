print("Starting MongoDB initialization...");

// Get the database name from the environment variable
const dbName = _getEnv("MONGO_INITDB_DATABASE") || "default_db_name";
print("Using database: " + dbName);

// Switch to the desired database
db = db.getSiblingDB(dbName);

// Create a collection and insert initial data
if (db.getCollection("fruit").exists()) {
    print("Dropping existing collection...");
    db.fruit.drop();
}

db.createCollection("fruit");
db.fruit.insertMany([
    {
        name: "apple",
        origin: "usa",
        price: 5
    },
    {
        name: "banana",
        origin: "guatamala",
        price: 2
    },
    {
        name: "durian",
        origin: "thailand",
        price: 20
    }
]);
print("Initialization complete.");