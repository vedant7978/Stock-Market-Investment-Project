import java.sql.*;
import java.util.*;

public class InvestmentFirm {
    // Define the path to the properties file
    static String propertyFilename = "G:/SDC_Project/vedant/src/sample.prop";
    // Load the database configuration from the properties file
    static DBConfig config = new DBConfig(propertyFilename);
    // Establish a connection to the database using the configuration
    public static Connection connect = DBConnection.getConnection(config.getDbUrl(), config.getUsername(), config.getPassword());
    // Create an instance of the ShareTrader class
    ShareTrader shareTrader;
    // Constructor for the InvestmentFirm class
    public InvestmentFirm(){
        // Initialize the shareTrader instance
        shareTrader = new ShareTrader();
    }
    /**
     * Defines a new sector in the database.
     *
     * @param sectorName The name of the sector to be defined.
     *                   Must not be null or empty.
     */
    public static void defineSector(String sectorName) {
        if (sectorName == null || sectorName.isEmpty()) {
            return;
        }

        // Ensure the sectors table exists
        String createTableSQL = "CREATE TABLE IF NOT EXISTS sectors (sectorID INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE);";
        // SQL statement to insert a new sector
        String insertSectorSQL = "INSERT INTO sectors (name) VALUES (?);";
        // SQL statement to check if the "Cash" sector exists
        String checkCashSQL = "SELECT COUNT(*) AS count FROM sectors WHERE name = 'Cash';";

        try (Statement statement = connect.createStatement()) {
            // Create the sectors table if it doesn't exist
            statement.execute(createTableSQL);

            // Check if the "Cash" sector exists
            ResultSet rs = statement.executeQuery(checkCashSQL);
            boolean cashExists = rs.next() && rs.getInt("count") > 0;

            // Use try-with-resources statement to automatically close PreparedStatement
            try (PreparedStatement pstmt = connect.prepareStatement(insertSectorSQL)) {
                // Add the default "Cash" sector if it doesn't exist
                if (!cashExists) {
                    pstmt.setString(1, "Cash");
                    pstmt.executeUpdate(); // Execute the insert operation
                }

                // Add the passed sector
                pstmt.setString(1, sectorName); // Set the sector name parameter
                pstmt.executeUpdate(); // Execute the insert operation
            }
        } catch (SQLException e) {
            System.out.println("Failed to define sector: " + sectorName);
            System.out.println(e.getMessage());
        }
    }

    /**
     * Defines a new stock in the database with the given company name, stock symbol, and sector.
     *
     * @param companyName The name of the company issuing the stock.
     * @param stockSymbol The symbol representing the stock.
     * @param sector The sector to which the stock belongs.
     */
    public static void defineStock(String companyName, String stockSymbol, String sector) {

        if (companyName == null || companyName.isEmpty() || stockSymbol == null || stockSymbol.isEmpty() || sector == null || sector.isEmpty()) {
            return;
        }
        String getSectorIDSQL = "SELECT sectorID FROM sectors WHERE name = ?;";
        String insertStockSQL = "INSERT INTO stocks (companyName, stockSymbol, sectorID, currentPrice) VALUES (?, ?, ?, 1);";
        try(Statement statement = connect.createStatement()){
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS stocks (stockID INT AUTO_INCREMENT PRIMARY KEY, companyName VARCHAR(255), stockSymbol VARCHAR(50) UNIQUE NOT NULL, sectorID INT, currentPrice DECIMAL(10,2), FOREIGN KEY (sectorID) REFERENCES sectors(sectorID));");

            try (PreparedStatement getSectorStmt = connect.prepareStatement(getSectorIDSQL)) {

            getSectorStmt.setString(1, sector);
            ResultSet rs = getSectorStmt.executeQuery();

            if (rs.next()) {
                int sectorID = rs.getInt("sectorID");
                try (PreparedStatement insertStockStmt = connect.prepareStatement(insertStockSQL)) {
                    insertStockStmt.setString(1, companyName);
                    insertStockStmt.setString(2, stockSymbol);
                    insertStockStmt.setInt(3, sectorID);
                    insertStockStmt.executeUpdate();
                }catch (SQLException exception){
                    System.out.println("Stock already exists " + exception.getMessage());
                    System.out.println("Stock not defined: " + companyName + " (" + stockSymbol + ") in sector " + sector);
                }
            }else {

            }
            } catch (SQLException e) {
             System.out.println("Failed to define stock: " + stockSymbol);
            e.printStackTrace();
        }
      } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Sets the price per share for a specified stock symbol in the database.
     *
     * @param stockSymbol The symbol of the stock for which to set the price per share.
     * @param perSharePrice The new price per share to set for the stock.
     */
    public static void setStockPrice(String stockSymbol, double perSharePrice) {
        if (stockSymbol == null){
            return;
        }
        if (stockSymbol.isEmpty()){
            return;
        }
        if (perSharePrice < 0){
            return;
        }
        // SQL statement to update the stock price
        String updatePriceSQL = "UPDATE stocks SET currentPrice = ? WHERE stockSymbol = ?;";
        try(Statement statement = connect.createStatement()) {
            statement.execute("ALTER TABLE stocks ADD COLUMN IF NOT EXISTS currentPrice DECIMAL(10, 2);");
            try (PreparedStatement pstmt = connect.prepareStatement(updatePriceSQL)) {
                // Set the per share price and stock symbol in the PreparedStatement
                pstmt.setDouble(1, perSharePrice);
                pstmt.setString(2, stockSymbol);

                // Execute the update
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Updated stock price for " + stockSymbol + " to " + perSharePrice);
                } else {
                    System.out.println("Stock symbol not found: " + stockSymbol);
                }
            } catch (SQLException e) {
                System.out.println("Failed to set stock price for " + stockSymbol);
                e.printStackTrace();
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Defines a new investment profile with the given profile name and sector holdings.
     *
     * @param profileName The name of the investment profile to be defined.
     * @param sectorHoldings A map containing sector names as keys and their corresponding percentage holdings as values.
     */
    public static void defineProfile(String profileName, Map<String, Integer> sectorHoldings) {
        if (profileName == null){
            return;
        }
        if (profileName.isEmpty()){
            return;
        }
        if (sectorHoldings == null){
            return;
        }
        if (sectorHoldings.isEmpty()){
            return;
        }
        String checkProfileExistsSQL = "SELECT profileID FROM Profiles WHERE profileName = ?;";
        String insertProfileSQL = "INSERT INTO Profiles (profileName) VALUES (?);";
        String insertProfileSectorSQL = "INSERT INTO ProfileSectors (profileID, sectorID, percentage) VALUES (?, ?, ?);";
        String getSectorIDSQL = "SELECT sectorID FROM sectors WHERE name = ?;";
        try(Statement statement = connect.createStatement()){
            statement.execute("CREATE TABLE IF NOT EXISTS Profiles (profileID INT AUTO_INCREMENT PRIMARY KEY,profileName VARCHAR(255) UNIQUE NOT NULL);");
            try (PreparedStatement checkProfileStmt = connect.prepareStatement(checkProfileExistsSQL)) {
                checkProfileStmt.setString(1, profileName);
                ResultSet rs = checkProfileStmt.executeQuery();
                if (rs.next()) {
                    return; // Exit the method if profile exists
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try(Statement statement = connect.createStatement()){
            statement.execute("CREATE TABLE IF NOT EXISTS Profiles (profileID INT AUTO_INCREMENT PRIMARY KEY,profileName VARCHAR(255) UNIQUE NOT NULL);");
            statement.execute("CREATE TABLE IF NOT EXISTS ProfileSectors (" +
                    "profileID INT, " +
                    "sectorID INT, " +
                    "percentage INT, " +
                    "PRIMARY KEY (profileID, sectorID), " +
                    "FOREIGN KEY (profileID) REFERENCES Profiles(profileID), " +
                    "FOREIGN KEY (sectorID) REFERENCES sectors(sectorID));");
            try (PreparedStatement insertProfileStmt = connect.prepareStatement(insertProfileSQL, Statement.RETURN_GENERATED_KEYS)) {

            // Insert the profile
            insertProfileStmt.setString(1, profileName);
            int affectedRows = insertProfileStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating profile failed, no rows affected.");
            }
            boolean cashSectorPresent = false;
            try (ResultSet generatedKeys = insertProfileStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long profileID = generatedKeys.getLong(1);

                    // For each sector in the sectorHoldings map, insert a row into ProfileSectors
                    try (PreparedStatement getSectorStmt = connect.prepareStatement(getSectorIDSQL);
                         PreparedStatement insertProfileSectorStmt = connect.prepareStatement(insertProfileSectorSQL)) {
                        int totalPercentage = 0;
                        for (int value : sectorHoldings.values() ){
                            totalPercentage += value;
                        }
                        if (totalPercentage != 100){
                            return;
                        }

                        for (Map.Entry<String, Integer> entry : sectorHoldings.entrySet()) {
                            String sectorName = entry.getKey();
                            // Get the sectorID for the current sector name
                            getSectorStmt.setString(1, entry.getKey());
                            ResultSet rs = getSectorStmt.executeQuery();

                            if (rs.next()) {
                                int sectorID = rs.getInt("sectorID");
                                // Insert into ProfileSectors
                                insertProfileSectorStmt.setLong(1, profileID);
                                insertProfileSectorStmt.setInt(2, sectorID);
                                insertProfileSectorStmt.setInt(3, entry.getValue());
                                insertProfileSectorStmt.executeUpdate();
                                // Check if the sector name is "Cash"
                                if (sectorName.equalsIgnoreCase("Cash")) {
                                    cashSectorPresent = true;
                                }
                            } else {
                                System.out.println("Sector does not exist: " + entry.getKey());
                            }
                        }
                        // If "Cash" sector is not present, insert it with 0 percentage
                        if (!cashSectorPresent) {
                            getSectorStmt.setString(1, "Cash");
                            ResultSet rs = getSectorStmt.executeQuery();

                            if (rs.next()) {
                                int cashSectorID = rs.getInt("sectorID");

                                // Insert "Cash" sector with 0 percentage
                                insertProfileSectorStmt.setLong(1, profileID);
                                insertProfileSectorStmt.setInt(2, cashSectorID);
                                insertProfileSectorStmt.setInt(3, 0);
                                insertProfileSectorStmt.executeUpdate();

                            } else {
                                System.out.println("Cash sector does not exist in the database.");
                            }
                        }
                    }
                } else {
                    throw new SQLException("Creating profile failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to define profile: " + profileName);
            e.printStackTrace();
        }
      } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Adds an advisor with the given name to the database.
     *
     * @param advisorName The name of the advisor to be added.
     * @return The advisor's ID if added successfully, -1 otherwise.
     */
    public static int addAdvisor(String advisorName) {
        if (advisorName == null){
            return -1;
        }
        if (advisorName.isEmpty()){
            return -1;
        }
        String insertAdvisorSQL = "INSERT INTO Advisors (advisorName) VALUES (?) ON DUPLICATE KEY UPDATE advisorID=LAST_INSERT_ID(advisorID);";
        try(Statement statement = connect.createStatement()){
            statement.execute("CREATE TABLE IF NOT EXISTS Advisors (advisorID INT AUTO_INCREMENT PRIMARY KEY,advisorName VARCHAR(255) NOT NULL UNIQUE);");
        try (PreparedStatement pstmt = connect.prepareStatement(insertAdvisorSQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, advisorName);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int advisorID = rs.getInt(1);
                    return advisorID;
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Failed to add advisor: " + advisorName);
            e.printStackTrace();
        }

    } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1; // Indicating failure
    }
    /**
     * Adds a client with the given name to the database.
     *
     * @param clientName The name of the client to be added.
     * @return The client's ID if added successfully, -1 otherwise.
     */
    public static int addClient(String clientName) {
        if (clientName == null){
            return -1;
        }
        if (clientName.isEmpty()){
            return -1;
        }
        String insertClientSQL = "INSERT INTO Clients (clientName) VALUES (?) ON DUPLICATE KEY UPDATE clientID=LAST_INSERT_ID(clientID);";
        try(Statement statement = connect.createStatement()){
            statement.execute("CREATE TABLE IF NOT EXISTS Clients (clientID INT AUTO_INCREMENT PRIMARY KEY,clientName VARCHAR(255) NOT NULL UNIQUE);");
            try (PreparedStatement pstmt = connect.prepareStatement(insertClientSQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, clientName);
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int clientID = rs.getInt(1);
                        return clientID;
                    }
                }
            }
            catch (SQLException e) {
                System.out.println("Failed to add client: " + clientName);
                e.printStackTrace();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1; // Indicating failure
    }
    /**
     * Creates an account for a client with the specified parameters.
     *
     * @param clientId The ID of the client for whom the account is being created.
     * @param financialAdvisor The ID of the financial advisor associated with the account.
     * @param accountName The name of the account.
     * @param profileType The type of investment profile associated with the account.
     * @param reinvest A boolean indicating whether dividends will be reinvested in the account.
     * @return The ID of the newly created account if successful, -1 otherwise.
     */
    public static int createAccount(int clientId, int financialAdvisor, String accountName, String profileType, boolean reinvest) {
        if (accountName== null || profileType == null){
            return -1;
        }
        if (accountName.isEmpty() || profileType.isEmpty()){
            return -1;
        }
        if (!AccountCheck.clientExists(clientId,connect)) {
            return -1;
        }

        if (!AccountCheck.advisorExists(financialAdvisor,connect)) {
            return -1;
        }
        String insertAccountSQL = "INSERT INTO Accounts (clientID, advisorID, accountName, profileType, reinvest) VALUES (?, ?, ?, ?, ?);";
        String checkAccountExistsSQL = "SELECT accountID FROM Accounts WHERE clientID = ? AND advisorID = ? AND accountName = ? AND profileType = ?;";
        String checkAccountExistsSQL1 = "SELECT accountID FROM Accounts WHERE clientID = ? AND accountName = ?;";
        try(Statement statement = connect.createStatement()){
            statement.execute("CREATE TABLE IF NOT EXISTS Accounts (accountID INT AUTO_INCREMENT PRIMARY KEY, clientID INT,advisorID INT, accountName VARCHAR(255),profileType VARCHAR(255) NOT NULL,reinvest BOOLEAN, FOREIGN KEY (clientID) REFERENCES Clients(clientID),FOREIGN KEY (advisorID) REFERENCES Advisors(advisorID));");

            try (PreparedStatement checkStmt = connect.prepareStatement(checkAccountExistsSQL1)) {
                checkStmt.setInt(1, clientId);
                checkStmt.setString(2, accountName);

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    // Account with the same client ID and account name exists
                    int existingAccountId = rs.getInt("accountID");
                    return existingAccountId;
                }
            } catch (SQLException e) {
                System.out.println("Failed to check for existing account");
                e.printStackTrace();
                return -1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try(Statement statement = connect.createStatement()){
            statement.execute("CREATE TABLE IF NOT EXISTS Accounts (accountID INT AUTO_INCREMENT PRIMARY KEY, clientID INT,advisorID INT, accountName VARCHAR(255),profileType VARCHAR(255) NOT NULL,reinvest BOOLEAN, FOREIGN KEY (clientID) REFERENCES Clients(clientID),FOREIGN KEY (advisorID) REFERENCES Advisors(advisorID));");
            try (PreparedStatement pstmt = connect.prepareStatement(insertAccountSQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, clientId);
                pstmt.setInt(2, financialAdvisor);
                pstmt.setString(3, accountName);
                pstmt.setString(4, profileType);
                pstmt.setBoolean(5, reinvest);
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int accountID = rs.getInt(1);
                            return accountID;
                        } else {
                            throw new SQLException("Creating account failed, no ID obtained.");
                        }
                    }
                } else {
                    throw new SQLException("Creating account failed, no rows affected.");
                }
            }
            catch (SQLException e) {
                System.out.println("Failed to create account for clientID " + clientId);
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return -1; // Indicating failure
    }
    /**
     * Executes a share trading transaction for the specified account.
     *
     * @param accountID The ID of the account involved in the transaction.
     * @param stockSymbol The symbol of the stock being traded.
     * @param sharesExchanged The number of shares being bought or sold.
     */
    public static void tradeShares(int accountID, String stockSymbol, int sharesExchanged) {
        // Check if the stockSymbol exists in the stocks table
        if (!stockSymbol.equalsIgnoreCase("cash") && !StockTradingHelper.stockExists(stockSymbol,connect)) {
            return;
        }
        // Check if the accountId exists in the Accounts table
        if (!AccountCheck.accountExists(accountID,connect)) {
            return ; // Return 0 if account does not exist
        }

        try(Statement statement = connect.createStatement()){
           statement.execute("CREATE TABLE IF NOT EXISTS AccountStocks (accountID INT, stockSymbol VARCHAR(50),sharesOwned DECIMAL(10,2) DEFAULT 0, PRIMARY KEY (accountID, stockSymbol), FOREIGN KEY (accountID) REFERENCES Accounts(accountID), FOREIGN KEY (stockSymbol) REFERENCES stocks(stockSymbol));");
           statement.execute("ALTER TABLE Accounts ADD COLUMN IF NOT EXISTS cashBalance DECIMAL(10, 2) DEFAULT 0;");
           statement.execute("ALTER TABLE AccountStocks ADD COLUMN IF NOT EXISTS acb DECIMAL(10, 2) DEFAULT 0;");
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
        // Special handling for cash transactions
        if ("cash".equalsIgnoreCase(stockSymbol)) {
            ShareManager.updateCashBalance(accountID, sharesExchanged, connect);
            return;
        }

        // Assume this method fetches the current share price or defaults to $1
        double sharePrice = ShareManager.getCurrentSharePrice(stockSymbol,connect);

        if (sharesExchanged > 0) {
            // Buying shares
            ShareTrader.buyShares(accountID, stockSymbol, sharesExchanged, sharePrice, connect);
        } else {
            // Selling shares
            ShareTrader.sellShares(accountID, stockSymbol, sharesExchanged, sharePrice, connect);
        }
    }
    /**
     * Changes the financial advisor assigned to the specified account.
     *
     * @param accountId The ID of the account whose advisor needs to be changed.
     * @param newAdvisorId The ID of the new financial advisor to be assigned to the account.
     */
    public static void changeAdvisor(int accountId, int newAdvisorId) {
        String sql = "UPDATE Accounts SET advisorID = ? WHERE accountID = ?;";
        // Check if the accountId exists in the Accounts table
        if (!AccountCheck.accountExists(accountId,connect)) {
            return ; // Return if account does not exist
        }
        if (!AccountCheck.advisorExists(newAdvisorId,connect)) {
            return;
        }
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setInt(1, newAdvisorId);  // Set the new advisor ID
            pstmt.setInt(2, accountId);     // Specify which account to update

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Account ID: " + accountId + " has been assigned to the new advisor ID: " + newAdvisorId);
            } else {
                System.out.println("No account found with ID: " + accountId + ", or the account is already assigned to the specified advisor.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating advisor for account ID: " + accountId);
            e.printStackTrace();
        }
    }

    //Reporting on the system
    /**
     * Calculates the total value of an account by summing up the cash balance and the market value of all owned stocks.
     *
     * @param accountId The ID of the account for which the value needs to be calculated.
     * @return The total value of the account, including cash balance and market value of stocks.
     * @throws SQLException If an SQL exception occurs during database operations.
     */
    public static double accountValue(int accountId) throws SQLException {
        double totalValue = 0.0;
        // Check if the accountId exists in the Accounts table
        if (!AccountCheck.accountExists(accountId,connect)) {
            return totalValue; // Return 0 if account does not exist
        }
        // Fetch the cash balance for the account
        String sqlCashBalance = "SELECT cashBalance FROM Accounts WHERE accountID = ?;";
        try (PreparedStatement pstmt = connect.prepareStatement(sqlCashBalance)) {
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                totalValue += rs.getDouble("cashBalance");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching cash balance: " + e.getMessage());
            throw e;
        }

        // Fetch all stocks and the number of shares owned by the account
        String sqlStocks = "SELECT stockSymbol, sharesOwned FROM AccountStocks WHERE accountID = ?;";
        try (PreparedStatement pstmt = connect.prepareStatement(sqlStocks)) {
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String stockSymbol = rs.getString("stockSymbol");
                double sharesOwned = rs.getDouble("sharesOwned");
                double currentPrice = ShareManager.getCurrentSharePrice(stockSymbol,connect); // This method should be defined elsewhere

                // Calculate market value for each stock and add to total
                totalValue += currentPrice * sharesOwned;
            }
        } catch (SQLException e) {
            System.out.println("Error fetching stocks: " + e.getMessage());
            throw e;
        }

        return totalValue;
    }
    /**
     * Calculates the total portfolio value managed by a given financial advisor.
     *
     * @param advisorId The ID of the financial advisor.
     * @return The total portfolio value managed by the advisor.
     * @throws SQLException If an SQL exception occurs during database operations.
     */
    public static double advisorPortfolioValue(int advisorId) throws SQLException {
        double totalPortfolioValue = 0.0;
        if (!AccountCheck.advisorExists(advisorId,connect)) {
            return -1;
        }
        // Fetch all account IDs managed by the given financial advisor
        String sqlAccounts = "SELECT accountID FROM Accounts WHERE advisorID = ?;";
        try (PreparedStatement pstmt = connect.prepareStatement(sqlAccounts)) {
            pstmt.setInt(1, advisorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int accountId = rs.getInt("accountID");
                // Calculate the account value and add to the total portfolio value
                totalPortfolioValue += accountValue(accountId); // accountValue method should be defined elsewhere
            }
        } catch (SQLException e) {
            System.out.println("Error fetching accounts for advisor: " + e.getMessage());
            throw e;
        }

        return totalPortfolioValue;
    }
    /**
     * Calculates the profits for each account belonging to a specified client.
     *
     * @param clientId The ID of the client.
     * @return A map containing the account ID as key and the corresponding profit as value.
     */
    public static Map<Integer, Double> investorProfit(int clientId) {
        Map<Integer, Double> profitsByAccount = new HashMap<>();
        if (!AccountCheck.clientExists(clientId,connect)) {
            return new HashMap<>();
        }
        try {
            // Step 1: Fetch all account IDs for the client.
            String fetchAccountsSql = "SELECT accountID FROM Accounts WHERE clientID = ?";
            PreparedStatement fetchAccountsStmt = connect.prepareStatement(fetchAccountsSql);
            fetchAccountsStmt.setInt(1, clientId);
            ResultSet accountsRs = fetchAccountsStmt.executeQuery();

            while (accountsRs.next()) {
                int accountId = accountsRs.getInt("accountID");
                double totalProfit = 0.0;

                // Step 2: For each account, calculate the profit.
                String fetchStocksSql = "SELECT stockSymbol, sharesOwned, acb FROM AccountStocks WHERE accountID = ?";
                PreparedStatement fetchStocksStmt = connect.prepareStatement(fetchStocksSql);
                fetchStocksStmt.setInt(1, accountId);
                ResultSet stocksRs = fetchStocksStmt.executeQuery();

                while (stocksRs.next()) {
                    String stockSymbol = stocksRs.getString("stockSymbol");
                    double sharesOwned = stocksRs.getDouble("sharesOwned");
                    double acb = stocksRs.getDouble("acb");

                    // Fetch current market price for the stock.
                    double currentPrice = ShareManager.getCurrentSharePrice(stockSymbol,connect); // Assume this method is defined.
                    double sellingPrice = sharesOwned * currentPrice;
                    double profit = sellingPrice - (acb * sharesOwned);

                    totalProfit += profit;
                }

                // Step 3: Add the total profit for this account to the result map.
                profitsByAccount.put(accountId, totalProfit);
            }

            return profitsByAccount;
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions appropriately.
        }

        return new HashMap<>(); // In case of failure, adjust as per your error handling policy.
    }
    /**
     * Calculates the percentage weight of each sector in the portfolio associated with the specified account.
     *
     * @param accountId The ID of the account.
     * @return A map containing each sector name as key and its corresponding percentage weight in the portfolio as value.
     */
    public static Map<String, Integer> profileSectorWeights(int accountId) {
    Map<String, Double> sectorValues = new HashMap<>();
    double totalValue = 0;
        // Check if the accountId exists in the Accounts table
        if (!AccountCheck.accountExists(accountId,connect)) {
            return new HashMap<>(); // Return 0 if account does not exist
        }
    try {
        // Assuming 'name' is the correct column for the sector's name in your 'sectors' table.
        String queryStocks = "SELECT st.name AS sectorName, s.currentPrice, ast.sharesOwned " +
                "FROM AccountStocks ast " +
                "JOIN stocks s ON ast.stockSymbol = s.stockSymbol " +
                "JOIN sectors st ON s.sectorID = st.sectorID " +
                "WHERE ast.accountID = ?";
        PreparedStatement pstmt = connect.prepareStatement(queryStocks);
        pstmt.setInt(1, accountId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            String sectorName = rs.getString("sectorName"); // Correctly fetching the sector's name
            double currentPrice = rs.getDouble("currentPrice");
            double sharesOwned = rs.getDouble("sharesOwned");
            double value = currentPrice * sharesOwned;
            sectorValues.put(sectorName, sectorValues.getOrDefault(sectorName, 0.0) + value);
            totalValue += value;
        }

        // Including cash as a separate 'sector'
        String queryCash = "SELECT cashBalance FROM Accounts WHERE accountID = ?";
        pstmt = connect.prepareStatement(queryCash);
        pstmt.setInt(1, accountId);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            double cashBalance = rs.getDouble("cashBalance");
            sectorValues.put("Cash", cashBalance); // Assuming 'Cash' is treated like a sector for simplicity
            totalValue += cashBalance;
        }
        List<String> allSectorNames = StockTradingHelper.getAllSectorNames(connect); // Implement this method to retrieve all sector names

        // Conversion to percentage and rounding
        Map<String, Integer> sectorPercentages = new HashMap<>();
        for (Map.Entry<String, Double> entry : sectorValues.entrySet()) {
            for(String sector : allSectorNames){
                if (!sectorValues.containsKey(sector)){
                    sectorPercentages.put(sector, 0); // Sector not found in account, set percentage to 0
                }else{
                    int percentage = (int) Math.round(entry.getValue() * 100 / totalValue);
                    sectorPercentages.put(entry.getKey(), percentage);
                }
            }

        }

        return sectorPercentages;
    }catch (SQLException e) {
        System.out.println("Database access error: " + e.getMessage());
    }

    return new HashMap<>(); // In case of failure
}
    /**
     * Identifies divergent accounts based on their sector weights compared to target weights with a specified tolerance.
     * Divergent accounts are those where the sector weights deviate from the target weights beyond the specified tolerance.
     *
     * @param tolerance The tolerance within which sector weights can deviate from the target weights.
     * @return A set containing the IDs of divergent accounts.
     */
    public static Set<Integer> divergentAccounts(int tolerance) {
        if (tolerance < 0){
            return new HashSet<>();
        }
        Set<Integer> divergentAccountIds = new HashSet<>();
        try {
            // Fetch all accounts
            String fetchAccountsSql = "SELECT accountID, profileType FROM Accounts";
            Statement statement = connect.createStatement();
            ResultSet accountsRs = statement.executeQuery(fetchAccountsSql);

            while (accountsRs.next()) {
                int accountId = accountsRs.getInt("accountID");
                String profileType = accountsRs.getString("profileType");

                // Get current sector weights for the account
                Map<String, Integer> currentWeights = profileSectorWeights(accountId);

                // Fetch target profile weights
                Map<String, Integer> targetWeights = StockTradingHelper.getProfileWeights(profileType,connect);

                // Compare each sector weight against target weights with tolerance
                boolean isDivergent = false;
                for (Map.Entry<String, Integer> entry : targetWeights.entrySet()) {
                    String sector = entry.getKey();
                    int targetWeight = entry.getValue();
                    int currentWeight = currentWeights.getOrDefault(sector, 0);

                    // Calculate tolerance thresholds
                    int upperLimit = targetWeight + tolerance;
                    int lowerLimit = targetWeight - tolerance;

                    // Check if current weight is outside the tolerance range
                    if (currentWeight < lowerLimit || currentWeight > upperLimit) {
                        isDivergent = true;
                        break;
                    }
                }

                // If cash is part of the calculation, ensure to include its check as well
                // Assuming "Cash" is the key used for cash holdings in the map
                int cashTarget = targetWeights.getOrDefault("Cash", 0); // Assuming there's a target for cash
                int cashCurrent = currentWeights.getOrDefault("Cash", 0);
                if (cashCurrent < cashTarget - tolerance || cashCurrent > cashTarget + tolerance) {
                    isDivergent = true;
                }

                if (isDivergent) {
                    divergentAccountIds.add(accountId);
                }
            }
        } catch (SQLException e) {
            System.out.println("Database access error: " + e.getMessage());
        }

        return divergentAccountIds;
    }
    /**
     * Disburses dividends for a given stock symbol to all accounts holding the stock.
     * Dividends are distributed based on the number of shares owned by each account.
     * Accounts set to reinvest dividends will use the dividends to purchase additional shares,
     * while other accounts will receive the dividends as cash.
     *
     * @param stockSymbol The symbol of the stock for which dividends are to be disbursed.
     * @param dividendPerShare The dividend amount per share.
     * @return An integer representing the total number of fractional shares distributed among accounts, or -1 if there's an error.
     */
    public static int disburseDividend(String stockSymbol, double dividendPerShare) {
        double totalSharesBought = 0;
        double firmFractionalSharesBefore = FirmDividendManager.getFirmFractionalShares(stockSymbol,connect);
        double totalFractional = 0;
        if (stockSymbol == null || stockSymbol.isEmpty() || dividendPerShare < 0){
            return -1;
        }
        // Check if the stockSymbol exists in the stocks table
        if (!StockTradingHelper.stockExists(stockSymbol,connect)) {
            return -1;
        }
        try {
            // Retrieve current share price
            double sharePrice = ShareManager.getCurrentSharePrice(stockSymbol,connect);

            // Fetch all accounts holding the stock
            String sql = "SELECT accountID, sharesOwned FROM AccountStocks WHERE stockSymbol = ?";
            PreparedStatement pstmt = connect.prepareStatement(sql);
            pstmt.setString(1, stockSymbol);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int accountID = rs.getInt("accountID");
                double sharesOwned = rs.getDouble("sharesOwned");
                double dividends = sharesOwned * dividendPerShare;

                // Check if account is set to reinvest
                if (StockTradingHelper.shouldReinvest(accountID,connect)) {
                    double sharesToBuy = (dividends / sharePrice);
                    double fractionalShares = (dividends % sharePrice) / sharePrice;
                     totalFractional += fractionalShares;

                    // Buy shares
                    ShareTrader.buyShares(accountID, stockSymbol, sharesToBuy, sharePrice, connect);
                    totalSharesBought += sharesToBuy;

                } else {
                    // Update cash balance
                    ShareManager.updateCashBalance(accountID, dividends,connect);
                }
            }
        } catch (SQLException e) {
            System.out.println("Database access error: " + e.getMessage());
        }

        return FirmDividendManager.updateAccountFractionalShares(stockSymbol,totalFractional,connect); // This is a simplification
    }
    // Analysing the system
    /**
     * Generates stock recommendations for a given account based on the stock holdings of similar accounts.
     * The function calculates cosine similarity between the given account's stock vector and those of other accounts,
     * and recommends whether to buy or sell stocks based on the majority of similar accounts.
     *
     * @param accountId The ID of the account for which recommendations are to be generated.
     * @param maxRecommendations The maximum number of recommendations to be returned.
     * @param numComparators The number of similar accounts to compare for generating recommendations.
     * @return A map containing stock symbols as keys and boolean values indicating whether to buy (true) or sell (false).
     */
    public static Map<String, Boolean> stockRecommendations(int accountId, int maxRecommendations, int numComparators){
        if (!AccountCheck.accountExists(accountId,connect)){
            return new HashMap<>();
        }
        if (maxRecommendations <= 0 || numComparators <= 0){
            return new HashMap<>();
        }
        // Create a vector for the given account's stock holdings
        int recommendationCounter = 0;
        Map<String, Double> accountStockVector = StockVectorsCreator.createStockVectors(connect).get(accountId);
        // Create a map to store cosine similarities between accounts
        Map<Integer, Double> similarityMap = new HashMap<>();

        // Iterate over all accounts and calculate cosine similarity with the given account
        for (Map.Entry<Integer, Map<String, Double>> entry : StockVectorsCreator.createStockVectors(connect).entrySet()) {
            int otherAccountId = entry.getKey();
            if (otherAccountId != accountId) {
                Map<String, Double> otherAccountStockVector = entry.getValue();
                double similarity = CosineSimilarityCalculator.calculateCosineSimilarity(accountStockVector, otherAccountStockVector);
                similarityMap.put(otherAccountId, similarity);
            }
        }

        // Sort similarity map by values (cosine similarity)
        List<Map.Entry<Integer, Double>> sortedSimilarityList = StockUtils.sortMapByValue(similarityMap, false);
        // Create a map to store stock recommendations
        Map<String, Boolean> recommendations = new HashMap<>();
        Map<String,Map<String,Integer>> countMap = new HashMap<>();
        Map<String,Integer> buyingRecommendMap = new HashMap<>();
        Map<String,Integer> sellingRecommendMap = new HashMap<>();
        // Counter variables for buy and sell recommendations
        int buyCount = 0;
        int sellCount = 0;
        Map<Integer, Map<String, Double>> otherAccountVectors = new HashMap<>();


        for (int i = 0; i < numComparators && i < sortedSimilarityList.size(); i++) {
            Map.Entry<Integer, Double> entry = sortedSimilarityList.get(i);
            int otherAccountId = entry.getKey();
            double similarity = entry.getValue();
            Map<String, Double> otherAccountStockVector = StockVectorsCreator.createStockVectors(connect).get(otherAccountId);
            otherAccountVectors.put(otherAccountId, otherAccountStockVector);
        }

//      Iterate over each entry in the account stock vector
        for (Map.Entry<String, Double> stockEntry : accountStockVector.entrySet()) {
                String stockSymbol = stockEntry.getKey();
                double sharesOwned = stockEntry.getValue();

                    if (sharesOwned == 0) {
                        int zeroCount = 0;
                        int nonZeroCount = 0;
                        // Iterate over other account vectors to count zero and non-zero values for the stock
                        for (Map.Entry<Integer, Map<String, Double>> otherEntry : otherAccountVectors.entrySet()) {
                            Map<String, Double> vec = otherEntry.getValue();
                            if (vec.containsKey(stockSymbol)) {
                                double shares = vec.get(stockSymbol);
                                if (shares == 0) {
                                    zeroCount++;
                                } else {
                                    nonZeroCount++;
                                }
                            }
                        }

                        // If majority of other accounts have non-zero shares, recommend buying
                        if (nonZeroCount > zeroCount) {
                                buyingRecommendMap.put(stockSymbol,nonZeroCount);
                                countMap.put("buy",buyingRecommendMap);
                                recommendationCounter++;
                        }

                    } else {
                        int zeroCount = 0;
                        int nonZeroCount = 0;

                        // Iterate over other account vectors to count zero and non-zero values for the stock
                        for (Map.Entry<Integer, Map<String, Double>> otherEntry : otherAccountVectors.entrySet()) {
                            Map<String, Double> vec = otherEntry.getValue();
                            if (vec.containsKey(stockSymbol)) {
                                double shares = vec.get(stockSymbol);
                                if (shares == 0) {
                                    zeroCount++;
                                } else {
                                    nonZeroCount++;
                                }
                            }
                        }
                        // If majority of other accounts have zero shares, recommend selling
                        if (zeroCount > nonZeroCount) {
                                sellingRecommendMap.put(stockSymbol,zeroCount);
                                countMap.put("sell",sellingRecommendMap);
                                recommendationCounter++;
                        }
                    }

        }
        for (int i = 0; i < maxRecommendations; i++) {
            String maxKey = null;
            int maxValue = Integer.MIN_VALUE;
            boolean isFromBuy = false;

            for (Map.Entry<String, Integer> entry : countMap.get("buy").entrySet()) {
                if (!recommendations.containsKey(entry.getKey()) && entry.getValue() > maxValue) {
                    maxValue = entry.getValue();
                    maxKey = entry.getKey();
                    isFromBuy = true;
                }
            }

            for (Map.Entry<String, Integer> entry : countMap.get("sell").entrySet()) {
                if (!recommendations.containsKey(entry.getKey()) && entry.getValue() > maxValue) {
                    maxValue = entry.getValue();
                    maxKey = entry.getKey();
                    isFromBuy = false;
                }
            }
            if (maxKey != null) {
                    recommendations.put(maxKey, isFromBuy);
            }
        }
//         Collect keys with zero values in the account vector
        List<String> keysWithZeroValues = new ArrayList<>();
        for (Map.Entry<String, Double> entry : accountStockVector.entrySet()) {
            if (entry.getValue() == 0.0) {
                keysWithZeroValues.add(entry.getKey());
            }
        }

        // Iterate over keys with zero values in the account vector
        for (String key : keysWithZeroValues) {
            int nonZeroCountForKey = 0;
            for (Map.Entry<Integer, Map<String, Double>> otherEntry : otherAccountVectors.entrySet()) {
                Map<String, Double> otherAccountVector = otherEntry.getValue();
                if (otherAccountVector.containsKey(key) && otherAccountVector.get(key) != 0.0) {
                    nonZeroCountForKey++;
                }
            }
            // Check if other keys in keysWithZeroValues have higher non-zero counts
            boolean recommend = true;
            for (String otherKey : keysWithZeroValues) {
                if (!otherKey.equals(key)) {
                    int nonZeroCountForOtherKey = 0;
                    for (Map.Entry<Integer, Map<String, Double>> otherEntry : otherAccountVectors.entrySet()) {
                        Map<String, Double> otherAccountVector = otherEntry.getValue();
                        if (otherAccountVector.containsKey(otherKey) && otherAccountVector.get(otherKey) != 0.0) {
                            nonZeroCountForOtherKey++;
                        }
                    }
                    if (nonZeroCountForOtherKey > nonZeroCountForKey) {
                        if (recommendationCounter < maxRecommendations){
                            recommendations.put(otherKey, true);
                            recommendationCounter++;
                        }
                    }else{
                        if (recommendationCounter < maxRecommendations){
                            recommendations.put(key, true);
                            recommendationCounter++;
                        }
                    }
                }
            }
        }
        return recommendations;
    }
    /**
     * Clusters financial advisors based on their preferences using the k-means clustering algorithm.
     *
     * @param tolerance   the maximum distance tolerance between account sector difference vectors and cluster representatives
     * @param maxGroups   the maximum number of advisor groups to create
     * @return            a set containing sets of advisor IDs representing the grouped advisors
     * @throws SQLException if there is an error accessing the database
     */
    public static Set<Set<Integer>> advisorGroups(double tolerance, int maxGroups) throws SQLException {
        // Retrieve the sector weights for each account from the database
        Map<Integer, Map<String, Double>> sectorDifferences = AdvisorClusterer.calculateSectorDifferences(connect);

        // Initialize cluster representatives with random values
        List<Map<String, Double>> clusterRepresentatives = AdvisorClusterer.initializeClusters(maxGroups,connect);

        boolean allDistancesBelowTolerance = false;
        int numClusters = 1;
        Map<Integer, Integer> clusterAssignments = null;

        while (!allDistancesBelowTolerance && numClusters <= maxGroups) {
            // Assign each account to the closest cluster representative
            clusterAssignments = AdvisorClusterer.assignToClusters(sectorDifferences, clusterRepresentatives);

            // Recalculate each cluster representative as the average of all vectors associated with the cluster
            AdvisorClusterer.updateClusterRepresentatives(sectorDifferences, clusterAssignments, clusterRepresentatives);

            // Calculate the cosine similarity measure of each account sector difference vector to its cluster representative
            double maxDistance = AdvisorClusterer.calculateMaxDistance(sectorDifferences, clusterAssignments, clusterRepresentatives);

            // Check if all distances are below tolerance
            allDistancesBelowTolerance = maxDistance <= tolerance;

            numClusters++;
        }
        // Convert cluster assignments to advisor groups
        return AdvisorClusterer.convertToAdvisorGroups(clusterAssignments);

    }

}
