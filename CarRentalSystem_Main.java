
import java.util.*;

// ==================== ENUMS ====================
enum CarCategory {
    ECONOMY, SEDAN, SUV, LUXURY
}

// ==================== EXCEPTION CLASSES ====================
class CarNotFoundException extends Exception {

    public CarNotFoundException(String message) {
        super(message);
    }
}

class CarNotAvailableException extends Exception {

    public CarNotAvailableException(String message) {
        super(message);
    }
}

class InvalidInputException extends Exception {

    public InvalidInputException(String message) {
        super(message);
    }
}

// ==================== CAR CLASS ====================
class Car {

    private final String carId;
    private final String brand;
    private final String model;
    private final double basePricePerDay;
    private final CarCategory category;
    private boolean isAvailable;

    public Car(String carId, String brand, String model, double basePricePerDay, CarCategory category) {
        if (carId == null || carId.isBlank()) {
            throw new IllegalArgumentException("Car ID cannot be empty.");
        }
        if (basePricePerDay <= 0) {
            throw new IllegalArgumentException("Base price must be positive.");
        }
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.basePricePerDay = basePricePerDay;
        this.category = category;
        this.isAvailable = true;
    }

    public String getCarId() {
        return carId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public CarCategory getCategory() {
        return category;
    }

    public double getBasePricePerDay() {
        return basePricePerDay;
    }

    public double calculatePrice(int rentalDays) {
        if (rentalDays <= 0) {
            throw new IllegalArgumentException("Rental days must be positive.");
        }
        // Discount: 10% off for 7+ days, 20% off for 30+ days
        double discount = 1.0;
        if (rentalDays >= 30) {
            discount = 0.80; 
        }else if (rentalDays >= 7) {
            discount = 0.90;
        }
        return basePricePerDay * rentalDays * discount;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void rent() throws CarNotAvailableException {
        if (!isAvailable) {
            throw new CarNotAvailableException("Car " + carId + " is already rented.");
        }
        isAvailable = false;
    }

    public void returnCar() {
        isAvailable = true;
    }

    @Override
    public String toString() {
        return String.format("%-6s | %-10s %-12s | %-8s | $%.2f/day | %s",
                carId, brand, model, category, basePricePerDay,
                isAvailable ? "Available" : "Rented");
    }
}

// ==================== CUSTOMER CLASS ====================
class Customer {

    private final String customerId;
    private final String name;
    private final String phone;
    private final List<String> rentalHistory;

    public Customer(String customerId, String name, String phone) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.rentalHistory = new ArrayList<>();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void addRentalHistory(String record) {
        rentalHistory.add(record);
    }

    public List<String> getRentalHistory() {
        return Collections.unmodifiableList(rentalHistory);
    }

    @Override
    public String toString() {
        return String.format("ID: %-8s | Name: %-20s | Phone: %s", customerId, name, phone);
    }
}

// ==================== RENTAL CLASS ====================
class Rental {

    private final Car car;
    private final Customer customer;
    private final int days;
    private final double totalPrice;
    private final String rentalDate;

    public Rental(Car car, Customer customer, int days) {
        this.car = car;
        this.customer = customer;
        this.days = days;
        this.totalPrice = car.calculatePrice(days);
        this.rentalDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());
    }

    public Car getCar() {
        return car;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getDays() {
        return days;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getRentalDate() {
        return rentalDate;
    }

    public void printReceipt() {
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.println("│           RENTAL RECEIPT                │");
        System.out.println("├─────────────────────────────────────────┤");
        System.out.printf("│ Customer ID  : %-25s│%n", customer.getCustomerId());
        System.out.printf("│ Customer Name: %-25s│%n", customer.getName());
        System.out.printf("│ Phone        : %-25s│%n", customer.getPhone());
        System.out.println("├─────────────────────────────────────────┤");
        System.out.printf("│ Car          : %-25s│%n", car.getBrand() + " " + car.getModel());
        System.out.printf("│ Car ID       : %-25s│%n", car.getCarId());
        System.out.printf("│ Category     : %-25s│%n", car.getCategory());
        System.out.printf("│ Rental Days  : %-25d│%n", days);
        System.out.printf("│ Price/Day    : $%-24.2f│%n", car.getBasePricePerDay());
        System.out.printf("│ Rental Date  : %-25s│%n", rentalDate);
        System.out.println("├─────────────────────────────────────────┤");
        System.out.printf("│ TOTAL PRICE  : $%-24.2f│%n", totalPrice);
        System.out.println("└─────────────────────────────────────────┘");
    }
}

// ==================== CAR RENTAL SYSTEM ====================
class CarRentalSystem {

    private final List<Car> cars;
    private final Map<String, Customer> customers; // customerId -> Customer
    private final List<Rental> activeRentals;
    private final List<Rental> completedRentals;
    private int customerCounter = 1;

    public CarRentalSystem() {
        cars = new ArrayList<>();
        customers = new LinkedHashMap<>();
        activeRentals = new ArrayList<>();
        completedRentals = new ArrayList<>();
    }

    // ---- Car Management ----
    public void addCar(Car car) {
        // Prevent duplicate car IDs
        for (Car c : cars) {
            if (c.getCarId().equalsIgnoreCase(car.getCarId())) {
                System.out.println("⚠  Car with ID " + car.getCarId() + " already exists.");
                return;
            }
        }
        cars.add(car);
    }

    public Car findCarById(String carId) throws CarNotFoundException {
        for (Car car : cars) {
            if (car.getCarId().equalsIgnoreCase(carId)) {
                return car;
            }
        }
        throw new CarNotFoundException("No car found with ID: " + carId);
    }

    // ---- Customer Management ----
    public Customer registerCustomer(String name, String phone) {
        String id = "CUS" + String.format("%03d", customerCounter++);
        Customer customer = new Customer(id, name, phone);
        customers.put(id, customer);
        return customer;
    }

    // ---- Rental Operations ----
    public void rentCar(Car car, Customer customer, int days) throws CarNotAvailableException {
        car.rent(); // throws if not available
        Rental rental = new Rental(car, customer, days);
        activeRentals.add(rental);
        customer.addRentalHistory("Rented " + car.getBrand() + " " + car.getModel() + " for " + days + " days on " + rental.getRentalDate());
        rental.printReceipt();
    }

    public void returnCar(String carId) throws CarNotFoundException {
        Car car = findCarById(carId);

        if (car.isAvailable()) {
            System.out.println("⚠  This car is not currently rented.");
            return;
        }

        Rental rentalToRemove = null;
        for (Rental rental : activeRentals) {
            if (rental.getCar().getCarId().equalsIgnoreCase(carId)) {
                rentalToRemove = rental;
                break;
            }
        }

        if (rentalToRemove != null) {
            activeRentals.remove(rentalToRemove);
            completedRentals.add(rentalToRemove);
            car.returnCar();
            System.out.println("\n✔  Car returned successfully.");
            System.out.println("   Returned by: " + rentalToRemove.getCustomer().getName());
            System.out.printf("   Total charge was: $%.2f%n", rentalToRemove.getTotalPrice());
        } else {
            System.out.println("⚠  Rental record not found.");
        }
    }

    // ---- Display Methods ----
    public void displayAvailableCars() {
        System.out.println("\n┌──────────────────────────────────────────────────────────────┐");
        System.out.println("│                     AVAILABLE CARS                           │");
        System.out.println("├──────────────────────────────────────────────────────────────┤");
        System.out.printf("│ %-6s | %-23s | %-8s | %-12s │%n", "ID", "Brand & Model", "Category", "Price/Day");
        System.out.println("├──────────────────────────────────────────────────────────────┤");
        boolean found = false;
        for (Car car : cars) {
            if (car.isAvailable()) {
                System.out.printf("│ %-6s | %-10s %-12s | %-8s | $%-11.2f │%n",
                        car.getCarId(), car.getBrand(), car.getModel(),
                        car.getCategory(), car.getBasePricePerDay());
                found = true;
            }
        }
        if (!found) {
            System.out.println("│         No cars currently available.                         │");
        }
        System.out.println("└──────────────────────────────────────────────────────────────┘");
    }

    public void displayAllCars() {
        System.out.println("\n--- All Cars in Fleet ---");
        for (Car car : cars) {
            System.out.println("  " + car);
        }
    }

    public void displayActiveRentals() {
        System.out.println("\n--- Active Rentals ---");
        if (activeRentals.isEmpty()) {
            System.out.println("  No active rentals.");
            return;
        }
        for (Rental r : activeRentals) {
            System.out.printf("  Car: %-15s | Customer: %-20s | Days: %d | Total: $%.2f%n",
                    r.getCar().getCarId() + " " + r.getCar().getModel(),
                    r.getCustomer().getName(), r.getDays(), r.getTotalPrice());
        }
    }

    // ---- Menu ----
    public void menu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║     CAR RENTAL SYSTEM        ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║  1. Rent a Car               ║");
            System.out.println("║  2. Return a Car             ║");
            System.out.println("║  3. View All Cars            ║");
            System.out.println("║  4. View Active Rentals      ║");
            System.out.println("║  5. Exit                     ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("  Enter your choice: ");

            int choice = readInt(scanner);

            switch (choice) {
                case 1 ->
                    handleRent(scanner);
                case 2 ->
                    handleReturn(scanner);
                case 3 ->
                    displayAllCars();
                case 4 ->
                    displayActiveRentals();
                case 5 -> {
                    System.out.println("\n  Thank you for using the Car Rental System! Goodbye.");
                    return;
                }
                default ->
                    System.out.println("⚠  Invalid choice. Please enter 1-5.");
            }
        }
    }

    private void handleRent(Scanner scanner) {
        System.out.println("\n═══ RENT A CAR ═══");

        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();
        if (name.isBlank()) {
            System.out.println("⚠  Name cannot be empty.");
            return;
        }

        System.out.print("Enter your phone number: ");
        String phone = scanner.nextLine().trim();

        displayAvailableCars();

        System.out.print("\nEnter Car ID to rent: ");
        String carId = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter number of rental days: ");
        int days = readInt(scanner);
        if (days <= 0) {
            System.out.println("⚠  Days must be a positive number.");
            return;
        }

        try {
            Car car = findCarById(carId);
            if (!car.isAvailable()) {
                System.out.println("⚠  Sorry, that car is not available.");
                return;
            }

            // Show price preview
            double price = car.calculatePrice(days);
            System.out.println("\n  Preview:");
            System.out.printf("  Car    : %s %s%n", car.getBrand(), car.getModel());
            System.out.printf("  Days   : %d%n", days);
            if (days >= 30) {
                System.out.println("  Discount: 20% (30+ day discount applied)"); 
            }else if (days >= 7) {
                System.out.println("  Discount: 10% (7+ day discount applied)");
            }
            System.out.printf("  Total  : $%.2f%n", price);

            System.out.print("\n  Confirm rental? (Y/N): ");
            String confirm = scanner.nextLine().trim();

            if (confirm.equalsIgnoreCase("Y")) {
                Customer customer = registerCustomer(name, phone);
                rentCar(car, customer, days);
                System.out.println("\n✔  Rental confirmed successfully!");
            } else {
                System.out.println("  Rental cancelled.");
            }

        } catch (CarNotFoundException e) {
            System.out.println("⚠  " + e.getMessage());
        } catch (CarNotAvailableException e) {
            System.out.println("⚠  " + e.getMessage());
        }
    }

    private void handleReturn(Scanner scanner) {
        System.out.println("\n═══ RETURN A CAR ═══");
        displayActiveRentals();

        System.out.print("\nEnter Car ID to return: ");
        String carId = scanner.nextLine().trim().toUpperCase();

        try {
            returnCar(carId);
        } catch (CarNotFoundException e) {
            System.out.println("⚠  " + e.getMessage());
        }
    }

    // ---- Utility ----
    private int readInt(Scanner scanner) {
        while (true) {
            try {
                int val = Integer.parseInt(scanner.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                System.out.print("⚠  Please enter a valid number: ");
            }
        }
    }
}

// ==================== MAIN ====================
public class CarRentalSystem_Main {

    public static void main(String[] args) {
        CarRentalSystem system = new CarRentalSystem();

        // Economy cars
        system.addCar(new Car("C001", "Toyota", "Camry", 60.0, CarCategory.SEDAN));
        system.addCar(new Car("C002", "Honda", "Accord", 70.0, CarCategory.SEDAN));
        system.addCar(new Car("C003", "Mahindra", "Thar", 150.0, CarCategory.SUV));
        system.addCar(new Car("C004", "Maruti", "Swift", 40.0, CarCategory.ECONOMY));
        system.addCar(new Car("C005", "BMW", "5 Series", 200.0, CarCategory.LUXURY));

        system.menu();
    }
}
