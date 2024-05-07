import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class P {
    public static void main(String[] args) throws SQLException {
        InvestmentFirm firm = new InvestmentFirm();
//        String profileName = "Growing1";
//        // Example sector holdings
//        Map<String, Integer> sectorHoldings = new HashMap<>();
//        sectorHoldings.put("Energy", 20);
//        sectorHoldings.put("Health Care", 20);
//        sectorHoldings.put("Information Technology", 40);
//        sectorHoldings.put("Consumer Staples", 10);
//        sectorHoldings.put("Cash", 10); // Assuming 'Cash' is treated as a sector for portfolio allocation
//        sectorHoldings.put("Industrial", 10); // Assuming 'Cash' is treated as a sector for portfolio allocation
//
////          InvestmentFirm.performDBOperations();
//            InvestmentFirm.defineSector("Agriculture");
//            InvestmentFirm.defineSector("Energy");
//            InvestmentFirm.defineSector("ENERGY");
//            InvestmentFirm.defineStock("Reliance","star","Agriculture");
//            InvestmentFirm.defineStock("TCS","moon","Agriculture");
//            InvestmentFirm.setStockPrice("star",15.07);
//            InvestmentFirm.setStockPrice("moon",25.07);
//            InvestmentFirm.defineProfile(profileName, sectorHoldings);
//           int advisorId = InvestmentFirm.addAdvisor("Rushil");
//           int clientId = InvestmentFirm.addClient("Vedant");
//           int advisorId1 = InvestmentFirm.addAdvisor("Shrey");
//           int accId  = InvestmentFirm.createAccount(clientId, advisorId,"Checking","star",true);
//           InvestmentFirm.tradeShares(accId,"star",-150);
//           int advisorId1 = InvestmentFirm.addAdvisor("Shrey");
//           InvestmentFirm.changeAdvisor(accId,advisorId1);
//            System.out.println(InvestmentFirm.accountValue(accId));
//        int accId1 = InvestmentFirm.createAccount(clientId, advisorId1,"Saving","star",true);
//        InvestmentFirm.tradeShares(accId1, "moon",15);
//        System.out.println(InvestmentFirm.advisorPortfolioValue(advisorId1));
////        InvestmentFirm.tradeShares(accId, "star",20);
//
//        InvestmentFirm.setStockPrice("star",20);
//        InvestmentFirm.tradeShares(accId, "star",20);
//
//        InvestmentFirm.defineStock("Samsung","vsp", "ENERGY");

//        int acc2 = InvestmentFirm.createAccount(clientId,advisorId1,"major","vsp",true);
//        int acc3 = InvestmentFirm.createAccount(clientId,advisorId1,"investing","vsp",true);
//        int acc4 = InvestmentFirm.createAccount(clientId,advisorId1,"interest","vsp",true);
//        InvestmentFirm.tradeShares(acc4, "tcs",10);
//        InvestmentFirm.tradeShares(acc3,"tcs",20);
//        InvestmentFirm.setStockPrice("vsp", 10);
//        InvestmentFirm.tradeShares(acc2, "vsp",10 );
//        InvestmentFirm.setStockPrice("vsp",15);
//        InvestmentFirm.tradeShares(acc2, "star",10 );
//        InvestmentFirm.setStockPrice("star", 15);
//        InvestmentFirm.tradeShares(acc2,"star",-10);
//        InvestmentFirm.setStockPrice("tcs", 290);
//        InvestmentFirm.tradeShares(5,"tcs", 10);
//        System.out.println(InvestmentFirm.investorProfit(clientId));
//        InvestmentFirm.defineStock("TATA","tcs","Information Technology");
//        InvestmentFirm.setStockPrice("tcs", 100);
//        InvestmentFirm.tradeShares(acc2,"cash",10000 );
//        InvestmentFirm.tradeShares(acc2,"tcs", 10);
//        InvestmentFirm.setStockPrice("tcs", 200);
//        System.out.println(InvestmentFirm.accountValue(3));
//        System.out.println(InvestmentFirm.profileSectorWeights(5));
//        InvestmentFirm.tradeShares(acc2,"vsp",10);
//        InvestmentFirm.defineProfile("star", sectorHoldings);
//        System.out.println(InvestmentFirm.divergentAccounts(5));
//        InvestmentFirm.tradeShares(1,"cash",5000);
//        InvestmentFirm.tradeShares(2,"cash",5000);
//        System.out.println(InvestmentFirm.disburseDividend("tcs",11));
//        System.out.println(InvestmentFirm.createAccount(clientId,advisorId1,null,"vsp",true));
//        InvestmentFirm.tradeShares(5,"tcs",10);
//          InvestmentFirm.createAccount(clientId,advisorId,"saving1","star",true);
//          InvestmentFirm.createAccount(clientId,advisorId,"saving2","star1",true);
//          InvestmentFirm.createAccount(clientId,advisorId,"saving3","star2",true);
//          InvestmentFirm.createAccount(clientId,advisorId,"saving4","star3",true);
//          InvestmentFirm.createAccount(clientId,advisorId,"saving5","star4",true);
//          InvestmentFirm.createAccount(clientId,advisorId,"saving6","star5",true);
//          InvestmentFirm.createAccount(clientId,advisorId,"saving7","star6",true);
//        InvestmentFirm.tradeShares(7,"tcs",20);
//        InvestmentFirm.tradeShares(8,"tcs",20);
//        InvestmentFirm.tradeShares(9,"tcs",20);
//        InvestmentFirm.tradeShares(10,"tcs",20);
//        InvestmentFirm.tradeShares(11,"tcs",20);
//        InvestmentFirm.tradeShares(12,"tcs",20);
//        InvestmentFirm.tradeShares(13,"tcs",20);
//        System.out.println(InvestmentFirm.disburseDividend("tcs",5));
//        System.out.println(InvestmentFirm.createAccount(25,74,"mansu","dd",true));
//        InvestmentFirm.tradeShares(5,"vsp",20);
//        System.out.println(InvestmentFirm.addClient("mansi"));
//        InvestmentFirm.tradeShares(5,"star",20);
//        InvestmentFirm.tradeShares(6,"moon",13);
//        InvestmentFirm.tradeShares(7,"moon",30);
//        System.out.println(InvestmentFirm.stockRecommendations(3,4,3) + "This is recommendation map");
//        InvestmentFirm.defineStock("vedanta","mvp","nothing");
//        InvestmentFirm.defineStock("vedanta","star","Information Technology");
//        InvestmentFirm.setStockPrice("bbbb",11);
//        InvestmentFirm.defineSector("sector1");
//        System.out.println(InvestmentFirm.investorProfit(1));
//        InvestmentFirm.tradeShares(13,"cash",5000);
//        System.out.println(InvestmentFirm.disburseDividend("tcs",2.7));
//        int accountid = InvestmentFirm.createAccount(clientId,advisorId1,"mine","srs",true);
//        InvestmentFirm.tradeShares(accountid,"vsp",10);
//        System.out.println(InvestmentFirm.advisorGroups(1,3));
//        InvestmentFirm.defineStock("maruti","mvp","Technology");
//        InvestmentFirm.defineSector("Energy");
//        InvestmentFirm.defineSector("Finance");
//        InvestmentFirm.defineSector("Real Estate");
//        InvestmentFirm.defineSector("Textile");
//        InvestmentFirm.defineSector("IT");
//        InvestmentFirm.defineSector("Health");

        // ADD STOCKS FOR EACH SECTOR
        //IT
//        InvestmentFirm.defineStock("Tata Company", "TCS", "IT");
//        InvestmentFirm.defineStock("Infosys", "INFY", "IT");
//        InvestmentFirm.defineStock("HCL Technology", "HCLTECH", "IT");
//
//        //Energy
//        InvestmentFirm.defineStock("Tata Company", "TATAPOWER", "Energy");
//        InvestmentFirm.defineStock("Adani Company", "ADANIPOWER", "Energy");
//        InvestmentFirm.defineStock("Oil and Natural Gas Corp.", "ONGC", "Energy");
//
//        //Finance
//        InvestmentFirm.defineStock("Life Insurance Company", "LIC", "Finance");
//        InvestmentFirm.defineStock("Adani Company", "ADANIENT", "Finance");
//        InvestmentFirm.defineStock("State Bank of India", "SBILIFE", "Finance");
//
//        //Real Estate
//        InvestmentFirm.defineStock("Godrej", "GODREJPROP", "Real Estate");
//        InvestmentFirm.defineStock("DLF", "DLF", "Real Estate");
//        InvestmentFirm.defineStock("Oberoi realty", "OBEROIRLTY", "Real Estate");
//
//        //Textile
//        InvestmentFirm.defineStock("Page Industries", "PAGEIND", "Textile");
//        InvestmentFirm.defineStock("Trident", "TRIDENT", "Textile");
//        InvestmentFirm.defineStock("KPR Mill", "KPRMILL", "Textile");
//
//        //Health
//        InvestmentFirm.defineStock("Sun Pharma", "SUNPHARMA", "Health");
//        InvestmentFirm.defineStock("Cipla", "CIPLA", "Health");
//        InvestmentFirm.defineStock("Zydus", "ZYDUS", "Health");


//        InvestmentFirm.setStockPrice("TCS", 10);
//        InvestmentFirm.setStockPrice("TATAPOWER", 2.5);
//        InvestmentFirm.setStockPrice("LIC", 15);
//        InvestmentFirm.setStockPrice("DLF", 2);
//        InvestmentFirm.setStockPrice("TRIDENT", 20);
//        InvestmentFirm.setStockPrice("ZYDUS", 25);

//        Map<String, Integer> commonProfile = new HashMap<>();
//        commonProfile.put("Energy", 13);
//        commonProfile.put("Finance", 17);
//        commonProfile.put("IT", 25);
//        commonProfile.put("Health", 10);
//        commonProfile.put("Textile", 20);
//        commonProfile.put("Real Estate", 15);
//
//        Map<String, Integer> commonProfileWithCash = new HashMap<>();
//        commonProfileWithCash.put("Energy", 13);
//        commonProfileWithCash.put("Finance", 17);
//        commonProfileWithCash.put("IT", 15);
//        commonProfileWithCash.put("Health", 10);
//        commonProfileWithCash.put("Textile", 20);
//        commonProfileWithCash.put("Real Estate", 10);
//        commonProfileWithCash.put("Cash", 15);

//        InvestmentFirm.defineProfile("commonProfile", commonProfile);
//        InvestmentFirm.defineProfile("commonProfileWithCash", commonProfileWithCash);

//        InvestmentFirm.addClient("Shrey");
//        InvestmentFirm.addClient("Rushil");
//        InvestmentFirm.addClient("Monka");
//
//        InvestmentFirm.addAdvisor("vedant");
//        InvestmentFirm.addAdvisor("jems");
//        InvestmentFirm.addAdvisor("kush");
//        InvestmentFirm.addAdvisor("shivang");
//
//        InvestmentFirm.createAccount(1, 1, "CommonAcc1", "commonProfile", true);
//        InvestmentFirm.createAccount(1, 2, "CommonAcc2", "commonProfileWithCash", false);
//        InvestmentFirm.createAccount(2, 3, "CommonAcc3", "commonProfileWithCash", true);
//        InvestmentFirm.createAccount(3, 2, "CommonAcc4", "commonProfile", true);

//        InvestmentFirm.tradeShares(1, "cash", 9000 );
//        InvestmentFirm.tradeShares(2, "cash", 9000 );
//        InvestmentFirm.tradeShares(3, "cash", 9000 );
//        InvestmentFirm.tradeShares(4, "cash", 9000 );
//        InvestmentFirm.tradeShares(3, "CIPLA", 40 );
//        InvestmentFirm.tradeShares(3, "TRIDENT", 15 );
//        InvestmentFirm.tradeShares(3, "TATAPOWER", 8 );
//        InvestmentFirm.tradeShares(3, "SBILIFE", 25 );
//        InvestmentFirm.tradeShares(3, "LIC", 10 );
//
//        InvestmentFirm.tradeShares(1,"INFY",20);
//        InvestmentFirm.tradeShares(1,"LIC",15);
//        InvestmentFirm.tradeShares(1,"TRIDENT",10);
//        InvestmentFirm.tradeShares(1,"zydus",5);
//        InvestmentFirm.tradeShares(1,"TCS",10);
//
//        InvestmentFirm.tradeShares(4,"OBEROIRLTY",100);
//        InvestmentFirm.tradeShares(4,"TRIDENT",10);
//        InvestmentFirm.tradeShares(4,"CIPLA",100);
//        InvestmentFirm.tradeShares(4,"ONGC",50);
//        InvestmentFirm.tradeShares(4,"TCS",15);
//
//        InvestmentFirm.tradeShares(2,"DLF",10);
//        InvestmentFirm.tradeShares(2,"TCS",25);
//        InvestmentFirm.tradeShares(2,"ZYDUS",7);
//        InvestmentFirm.tradeShares(2,"TRIDENT",3);
//        InvestmentFirm.tradeShares(2,"ONGC",6);

////        System.out.println(InvestmentFirm.accountValue(1));
////        System.out.println(InvestmentFirm.accountValue(2));
////        System.out.println(InvestmentFirm.accountValue(3));
////        System.out.println(InvestmentFirm.accountValue(4));
//        InvestmentFirm.setStockPrice("TCS", 25);
//        InvestmentFirm.setStockPrice("LIC", 25);
////        System.out.println(InvestmentFirm.accountValue(1));
////        System.out.println(InvestmentFirm.accountValue(2));
////        System.out.println(InvestmentFirm.accountValue(3));
////        System.out.println(InvestmentFirm.accountValue(4));

//        System.out.println(InvestmentFirm.advisorPortfolioValue(1));
//        System.out.println(InvestmentFirm.advisorPortfolioValue(2));
//        System.out.println(InvestmentFirm.advisorPortfolioValue(3));
//        System.out.println(InvestmentFirm.advisorPortfolioValue(4));
//        InvestmentFirm.changeAdvisor(4,4);
//        System.out.println(InvestmentFirm.advisorPortfolioValue(1));
//        System.out.println(InvestmentFirm.advisorPortfolioValue(2));
//        System.out.println(InvestmentFirm.advisorPortfolioValue(3));
//        System.out.println(InvestmentFirm.advisorPortfolioValue(4));

//        System.out.println(InvestmentFirm.investorProfit(1));
//        System.out.println(InvestmentFirm.investorProfit(2));
//        System.out.println(InvestmentFirm.investorProfit(3));

//        System.out.println(InvestmentFirm.profileSectorWeights(1));
//        System.out.println(InvestmentFirm.profileSectorWeights(2));
//        System.out.println(InvestmentFirm.profileSectorWeights(3));
//        System.out.println(InvestmentFirm.profileSectorWeights(4));
//
//        System.out.println(firm.divergentAccounts(90));
//        System.out.println(firm.divergentAccounts(50));
//        System.out.println(firm.disburseDividend("TCS", 1.5));
        System.out.println(firm.stockRecommendations(2, 5,4) + "This is final");
//        System.out.println(firm.advisorGroups(-1,10));
    }
}
